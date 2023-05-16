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

package com.liferay.portal.kernel.portlet;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Eudaldo Alonso
 */
public class PortletProviderUtil {

	public static String getPortletId(
		String className, PortletProvider.Action action) {

		PortletProvider portletProvider = getPortletProvider(className, action);

		if (portletProvider != null) {
			return portletProvider.getPortletName();
		}

		return StringPool.BLANK;
	}

	public static PortletURL getPortletURL(
			HttpServletRequest httpServletRequest, Group group,
			String className, PortletProvider.Action action)
		throws PortalException {

		PortletProvider portletProvider = getPortletProvider(className, action);

		if (portletProvider != null) {
			return portletProvider.getPortletURL(httpServletRequest, group);
		}

		return null;
	}

	public static PortletURL getPortletURL(
			HttpServletRequest httpServletRequest, String className,
			PortletProvider.Action action)
		throws PortalException {

		PortletProvider portletProvider = getPortletProvider(className, action);

		if (portletProvider != null) {
			return portletProvider.getPortletURL(httpServletRequest);
		}

		return null;
	}

	public static PortletURL getPortletURL(
			PortletRequest portletRequest, Group group, String className,
			PortletProvider.Action action)
		throws PortalException {

		return getPortletURL(
			PortalUtil.getHttpServletRequest(portletRequest), group, className,
			action);
	}

	public static PortletURL getPortletURL(
			PortletRequest portletRequest, String className,
			PortletProvider.Action action)
		throws PortalException {

		return getPortletURL(
			PortalUtil.getHttpServletRequest(portletRequest), className,
			action);
	}

	protected static PortletProvider getPortletProvider(
		String className, PortletProvider.Action action) {

		Map<String, PortletProvider> portletProviderMap =
			_portletProviderActionPortletProviderMap.get(action);

		if (portletProviderMap == null) {
			return null;
		}

		return portletProviderMap.get(className);
	}

	protected static PortletProvider getPortletProvider(
		String className,
		ServiceTrackerMap<String, ? extends PortletProvider>
			serviceTrackerMap) {

		PortletProvider portletProvider = serviceTrackerMap.getService(
			className);

		if ((portletProvider == null) && isAssetObject(className)) {
			portletProvider = serviceTrackerMap.getService(
				AssetEntry.class.getName());
		}

		return portletProvider;
	}

	protected static boolean isAssetObject(String className) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory != null) {
			return true;
		}

		return false;
	}

	private static final Map
		<PortletProvider.Action, Map<String, PortletProvider>>
			_portletProviderActionPortletProviderMap =
				new ConcurrentHashMap<>();
	private static final ServiceTracker<PortletProvider, PortletProvider>
		_portletProviderServiceTracker;

	static {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_portletProviderServiceTracker =
			new ServiceTracker<PortletProvider, PortletProvider>(
				bundleContext, PortletProvider.class,
				new ServiceTrackerCustomizer
					<PortletProvider, PortletProvider>() {

					@Override
					public PortletProvider addingService(
						ServiceReference<PortletProvider> serviceReference) {

						PortletProvider portletProvider =
							bundleContext.getService(serviceReference);

						for (PortletProvider.Action portletProviderAction :
								portletProvider.getSupportedActions()) {

							Map<String, PortletProvider> portletProviderMap =
								_portletProviderActionPortletProviderMap.
									computeIfAbsent(
										portletProviderAction,
										key -> new HashMap<>());

							portletProviderMap.put(
								(String)serviceReference.getProperty(
									"model.class.name"),
								portletProvider);
						}

						return portletProvider;
					}

					@Override
					public void modifiedService(
						ServiceReference<PortletProvider> serviceReference,
						PortletProvider portletProvider) {
					}

					@Override
					public void removedService(
						ServiceReference<PortletProvider> serviceReference,
						PortletProvider portletProvider) {

						for (PortletProvider.Action portletProviderAction :
								portletProvider.getSupportedActions()) {

							Map<String, PortletProvider> portletProviderMap =
								_portletProviderActionPortletProviderMap.get(
									portletProviderAction);

							portletProviderMap.remove(
								(String)serviceReference.getProperty(
									"model.class.name"));
						}

						bundleContext.ungetService(serviceReference);
					}

				});

		_portletProviderServiceTracker.open();
	}

}