/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;

import jakarta.servlet.ServletContext;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Minhchau Dang
 */
public class ResourceUtil {

	public static ObjectValuePair<ServletContext, URL> getObjectValuePair(
			String requestPath, String requestURI,
			ServletContext defaultServletContext)
		throws IOException {

		ServletContext servletContext = defaultServletContext;

		URL resourceURL = servletContext.getResource(requestURI);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		servletContext = PortalWebResourcesUtil.getPathServletContext(
			requestPath);

		resourceURL = PortalWebResourcesUtil.getResource(
			servletContext, requestPath);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		servletContext = _getPathServletContext(requestPath);

		resourceURL = _getResource(servletContext, requestPath);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		servletContext = DynamicResourceIncludeUtil.getPathServletContext(
			requestPath);

		resourceURL = DynamicResourceIncludeUtil.getResource(
			servletContext, requestPath);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		return null;
	}

	public static ServletContext getPathServletContext(
			String requestPath, String requestURI,
			ServletContext defaultServletContext)
		throws IOException {

		ObjectValuePair<ServletContext, URL> objectValuePair =
			getObjectValuePair(requestPath, requestURI, defaultServletContext);

		if (objectValuePair == null) {
			return null;
		}

		return objectValuePair.getKey();
	}

	public static URL getResourceURL(
			String requestPath, String requestURI,
			ServletContext defaultServletContext)
		throws IOException {

		ObjectValuePair<ServletContext, URL> objectValuePair =
			getObjectValuePair(requestPath, requestURI, defaultServletContext);

		if (objectValuePair == null) {
			return null;
		}

		return objectValuePair.getValue();
	}

	private static ServletContext _getPathServletContext(String path) {
		for (ServletContext servletContext : _serviceTrackerMap.values()) {
			if (path.startsWith(servletContext.getContextPath())) {
				return servletContext;
			}
		}

		return null;
	}

	private static URL _getResource(
		ServletContext servletContext, String path) {

		if (servletContext == null) {
			return null;
		}

		path = PortalWebResourcesUtil.stripContextPath(servletContext, path);

		try {
			URL url = servletContext.getResource(path);

			if (url != null) {
				return url;
			}
		}
		catch (MalformedURLException malformedURLException) {
			if (_log.isDebugEnabled()) {
				_log.debug(malformedURLException);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(ResourceUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap<String, ServletContext>
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, Portlet.class, null,
			(serviceReference, emitter) -> {
				Portlet portlet = _bundleContext.getService(serviceReference);

				try {
					if (portlet != null) {
						PortletApp portletApp = portlet.getPortletApp();

						if ((portletApp != null) && portletApp.isWARFile()) {
							ServletContext servletContext =
								portletApp.getServletContext();

							if (servletContext != null) {
								emitter.emit(servletContext.getContextPath());
							}
						}
					}
				}
				finally {
					_bundleContext.ungetService(serviceReference);
				}
			},
			new ServiceTrackerCustomizer<Portlet, ServletContext>() {

				@Override
				public ServletContext addingService(
					ServiceReference<Portlet> serviceReference) {

					Portlet portlet = _bundleContext.getService(
						serviceReference);

					if (portlet == null) {
						return null;
					}

					PortletApp portletApp = portlet.getPortletApp();

					if ((portletApp != null) && portletApp.isWARFile()) {
						return portletApp.getServletContext();
					}

					return null;
				}

				@Override
				public void modifiedService(
					ServiceReference<Portlet> serviceReference,
					ServletContext servletContext) {
				}

				@Override
				public void removedService(
					ServiceReference<Portlet> serviceReference,
					ServletContext servletContext) {

					_bundleContext.ungetService(serviceReference);
				}

			});

}