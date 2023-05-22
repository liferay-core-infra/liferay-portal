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
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
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

		PortletProvider portletProvider = portletProviderMap.get(className);

		if ((portletProvider == null) && isAssetObject(className)) {
			portletProvider = portletProviderMap.get(
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
	private static final Map
		<Class<? extends PortletProvider>, PortletProvider.Action>
			_portletProviderClassActionMap =
				HashMapBuilder.
					<Class<? extends PortletProvider>, PortletProvider.Action>
						put(
							AddPortletProvider.class, PortletProvider.Action.ADD
					).put(
						BrowsePortletProvider.class,
						PortletProvider.Action.BROWSE
					).put(
						EditPortletProvider.class, PortletProvider.Action.EDIT
					).put(
						ManagePortletProvider.class,
						PortletProvider.Action.MANAGE
					).put(
						PreviewPortletProvider.class,
						PortletProvider.Action.PREVIEW
					).put(
						ViewPortletProvider.class, PortletProvider.Action.VIEW
					).build();
	private static final ServiceTracker<Object, PortletProvider>
		_portletProviderServiceTracker;

	static {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		try {
			_portletProviderServiceTracker =
				new ServiceTracker<Object, PortletProvider>(
					bundleContext,
					bundleContext.createFilter(
						"(&(model.class.name=*)(objectClass=" +
							"*PortletProvider))"),
					new ServiceTrackerCustomizer<Object, PortletProvider>() {

						@Override
						public PortletProvider addingService(
							ServiceReference<Object> serviceReference) {

							List<String> modelClassNames = StringUtil.asList(
								serviceReference.getProperty(
									"model.class.name"));

							PortletProvider portletProvider =
								(PortletProvider)bundleContext.getService(
									serviceReference);

							List<PortletProvider.Action>
								supportedPortletProviderActions =
									portletProvider.getSupportedActions();

							for (PortletProvider.Action portletProviderAction :
									supportedPortletProviderActions) {

								_registerPortletProvider(
									modelClassNames, portletProvider,
									portletProviderAction);
							}

							if (supportedPortletProviderActions.isEmpty()) {
								for (Class<?> portletProviderInterface :
										ReflectionUtil.getInterfaces(
											portletProvider)) {

									if (_portletProviderClassActionMap.
											containsKey(
												portletProviderInterface)) {

										_registerPortletProvider(
											modelClassNames, portletProvider,
											_portletProviderClassActionMap.get(
												portletProviderInterface));
									}
								}
							}

							return portletProvider;
						}

						@Override
						public void modifiedService(
							ServiceReference<Object> serviceReference,
							PortletProvider portletProvider) {
						}

						@Override
						public void removedService(
							ServiceReference<Object> serviceReference,
							PortletProvider portletProvider) {

							List<String> modelClassNames = StringUtil.asList(
								serviceReference.getProperty(
									"model.class.name"));

							List<PortletProvider.Action>
								supportedPortletProviderActions =
									portletProvider.getSupportedActions();

							for (PortletProvider.Action portletProviderAction :
									supportedPortletProviderActions) {

								_unregisterPortletProvider(
									modelClassNames, portletProviderAction);
							}

							if (supportedPortletProviderActions.isEmpty()) {
								for (Class<?> portletProviderInterface :
										ReflectionUtil.getInterfaces(
											portletProvider)) {

									if (_portletProviderClassActionMap.
											containsKey(
												portletProviderInterface)) {

										_unregisterPortletProvider(
											modelClassNames,
											_portletProviderClassActionMap.get(
												portletProviderInterface));
									}
								}
							}

							bundleContext.ungetService(serviceReference);
						}

						private void _registerPortletProvider(
							List<String> modelClassNames,
							PortletProvider portletProvider,
							PortletProvider.Action portletProviderAction) {

							Map<String, PortletProvider> portletProviderMap =
								_portletProviderActionPortletProviderMap.
									computeIfAbsent(
										portletProviderAction,
										key -> new HashMap<>());

							for (String modelClassName : modelClassNames) {
								portletProviderMap.put(
									modelClassName, portletProvider);
							}
						}

						private void _unregisterPortletProvider(
							List<String> modelClassNames,
							PortletProvider.Action portletProviderAction) {

							Map<String, PortletProvider> portletProviderMap =
								_portletProviderActionPortletProviderMap.get(
									portletProviderAction);

							for (String modelClassName : modelClassNames) {
								portletProviderMap.remove(modelClassName);
							}
						}

					});

			_portletProviderServiceTracker.open();
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new ExceptionInInitializerError(invalidSyntaxException);
		}
	}

}