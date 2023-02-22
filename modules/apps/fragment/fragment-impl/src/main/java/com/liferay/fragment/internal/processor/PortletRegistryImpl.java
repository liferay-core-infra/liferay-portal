/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.fragment.internal.processor;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.render.PortletRenderParts;
import com.liferay.portal.kernel.portlet.render.PortletRenderUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Pavel Savinov
 */
@Component(service = PortletRegistry.class)
public class PortletRegistryImpl implements PortletRegistry {

	@Override
	public List<String> getFragmentEntryLinkPortletIds(
		FragmentEntryLink fragmentEntryLink) {

		List<String> portletIds = new ArrayList<>();

		if (fragmentEntryLink.isTypePortlet()) {
			try {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					fragmentEntryLink.getEditableValues());

				String portletId = jsonObject.getString("portletId");

				if (Validator.isNotNull(portletId)) {
					String instanceId = jsonObject.getString("instanceId");

					portletIds.add(
						PortletIdCodec.encode(portletId, instanceId));
				}
			}
			catch (PortalException portalException) {
				_log.error("Unable to get portlet IDs", portalException);
			}

			return portletIds;
		}

		Document document = Jsoup.parseBodyFragment(
			fragmentEntryLink.getHtml());

		Document.OutputSettings outputSettings = new Document.OutputSettings();

		outputSettings.prettyPrint(false);

		document.outputSettings(outputSettings);

		for (Element element : document.select("*")) {
			String tagName = element.tagName();

			if (!StringUtil.startsWith(tagName, "lfr-widget-")) {
				continue;
			}

			String alias = StringUtil.removeSubstring(tagName, "lfr-widget-");

			String portletName = getPortletName(alias);

			if (Validator.isNull(portletName)) {
				continue;
			}

			String portletId = PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName),
				PortletIdCodec.decodeUserId(portletName),
				fragmentEntryLink.getNamespace() + element.attr("id"));

			portletIds.add(portletId);
		}

		return portletIds;
	}

	@Override
	public List<String> getPortletAliases() {
		return new ArrayList<>(_serviceTrackerMap.keySet());
	}

	@Override
	public String getPortletName(String alias) {
		return _serviceTrackerMap.getService(alias);
	}

	@Override
	public void writePortletPaths(
			FragmentEntryLink fragmentEntryLink,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		List<String> fragmentEntryLinkPortletIds =
			getFragmentEntryLinkPortletIds(fragmentEntryLink);

		if (ListUtil.isEmpty(fragmentEntryLinkPortletIds)) {
			return;
		}

		Stream<String> stream = fragmentEntryLinkPortletIds.stream();

		List<Portlet> portlets = stream.map(
			fragmentEntryLinkPortletId -> _portletLocalService.getPortletById(
				fragmentEntryLinkPortletId)
		).filter(
			portlet -> {
				if ((portlet == null) || !portlet.isActive() ||
					portlet.isUndeployedPortlet()) {

					return false;
				}

				return true;
			}
		).distinct(
		).collect(
			Collectors.toList()
		);

		for (Portlet portlet : portlets) {
			try {
				PortletRenderParts portletRenderParts =
					PortletRenderUtil.getPortletRenderParts(
						httpServletRequest, StringPool.BLANK, portlet);

				PortletRenderUtil.writeHeaderPaths(
					httpServletResponse, portletRenderParts);

				PortletRenderUtil.writeFooterPaths(
					httpServletResponse, portletRenderParts);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to write portlet paths " + portlet.getPortletId(),
					exception);
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, javax.portlet.Portlet.class,
			"(com.liferay.fragment.entry.processor.portlet.alias=*)",
			(serviceReference, emitter) -> emitter.emit(
				GetterUtil.getString(
					serviceReference.getProperty(
						"com.liferay.fragment.entry.processor.portlet.alias"))),
			new ServiceTrackerCustomizer<javax.portlet.Portlet, String>() {

				@Override
				public String addingService(
					ServiceReference<javax.portlet.Portlet> serviceReference) {

					return GetterUtil.getString(
						serviceReference.getProperty("javax.portlet.name"));
				}

				@Override
				public void modifiedService(
					ServiceReference<javax.portlet.Portlet> serviceReference,
					String service) {
				}

				@Override
				public void removedService(
					ServiceReference<javax.portlet.Portlet> serviceReference,
					String service) {
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletRegistryImpl.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PortletLocalService _portletLocalService;

	private ServiceTrackerMap<String, String> _serviceTrackerMap;

}