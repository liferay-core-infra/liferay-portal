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

import com.liferay.fragment.contributor.PortletAliasRegistration;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Pavel Savinov
 */
@Component(immediate = true, service = PortletRegistry.class)
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
		return new ArrayList<>(_portletNames.keySet());
	}

	@Override
	public String getPortletName(String alias) {
		return _portletNames.get(alias);
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
	protected void activate(BundleContext bundleContext)
		throws InvalidSyntaxException {

		String filterString = StringBundler.concat(
			"(&(", _ALIAS_KEY, "=*)(objectClass=",
			javax.portlet.Portlet.class.getName(), "))");

		_serviceTracker = new ServiceTracker<>(
			bundleContext, bundleContext.createFilter(filterString),
			new ServiceTrackerCustomizer
				<javax.portlet.Portlet, javax.portlet.Portlet>() {

				@Override
				public javax.portlet.Portlet addingService(
					ServiceReference<javax.portlet.Portlet> serviceReference) {

					String alias = GetterUtil.getString(
						serviceReference.getProperty(_ALIAS_KEY));

					_portletNames.put(
						alias,
						GetterUtil.getString(
							serviceReference.getProperty(
								"javax.portlet.name")));

					_serviceRegistrations.put(
						alias,
						bundleContext.registerService(
							PortletAliasRegistration.class,
							new PortletAliasRegistration() {
							},
							MapUtil.singletonDictionary(_ALIAS_KEY, alias)));

					return bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<javax.portlet.Portlet> serviceReference,
					javax.portlet.Portlet jxPortlet) {
				}

				@Override
				public void removedService(
					ServiceReference<javax.portlet.Portlet> serviceReference,
					javax.portlet.Portlet jxPortlet) {

					String alias = GetterUtil.getString(
						serviceReference.getProperty(_ALIAS_KEY));

					_portletNames.remove(
						alias,
						GetterUtil.getString(
							serviceReference.getProperty(
								"javax.portlet.name")));

					bundleContext.ungetService(serviceReference);

					ServiceRegistration<PortletAliasRegistration>
						serviceRegistration = _serviceRegistrations.remove(
							alias);

					serviceRegistration.unregister();
				}

			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private static final String _ALIAS_KEY =
		"com.liferay.fragment.entry.processor.portlet.alias";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletRegistryImpl.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PortletLocalService _portletLocalService;

	private final Map<String, String> _portletNames = new ConcurrentHashMap<>();
	private final Map<String, ServiceRegistration<PortletAliasRegistration>>
		_serviceRegistrations = new ConcurrentHashMap<>();
	private ServiceTracker<javax.portlet.Portlet, javax.portlet.Portlet>
		_serviceTracker;

}