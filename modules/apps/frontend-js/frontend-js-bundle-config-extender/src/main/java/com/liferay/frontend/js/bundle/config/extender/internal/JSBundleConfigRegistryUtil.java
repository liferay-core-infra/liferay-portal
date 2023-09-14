/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.bundle.config.extender.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;

import java.net.URL;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 */
public class JSBundleConfigRegistryUtil {

	public static Collection<JSConfig> getJSConfigs() {
		return _jsConfigs.values();
	}

	public static long getLastModified() {
		return _lastModified;
	}

	public static class JSConfig {

		public ServletContext getServletContext() {
			return _servletContext;
		}

		public URL getURL() {
			return _url;
		}

		private JSConfig(ServletContext servletContext, URL url) {
			_servletContext = servletContext;
			_url = url;
		}

		private final ServletContext _servletContext;
		private final URL _url;

	}

	private static final Map<ServiceReference<ServletContext>, JSConfig>
		_jsConfigs = new ConcurrentSkipListMap<>();
	private static volatile long _lastModified = System.currentTimeMillis();
	private static final ServiceTrackerMap
		<String, ServiceReference<ServletContext>> _serviceTrackerMap;

	private static class JSBundleConfigServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ServletContext, ServiceReference<ServletContext>> {

		public JSBundleConfigServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public ServiceReference<ServletContext> addingService(
			ServiceReference<ServletContext> serviceReference) {

			Bundle bundle = serviceReference.getBundle();

			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			String jsConfig = headers.get("Liferay-JS-Config");

			if (jsConfig != null) {
				URL url = bundle.getEntry(jsConfig);

				if (url != null) {
					ServletContext servletContext = _bundleContext.getService(
						serviceReference);

					_jsConfigs.put(
						serviceReference, new JSConfig(servletContext, url));

					_lastModified = System.currentTimeMillis();

					return serviceReference;
				}
			}

			return null;
		}

		@Override
		public void modifiedService(
			ServiceReference<ServletContext> serviceReference,
			ServiceReference<ServletContext> trackedServiceReference) {

			removedService(serviceReference, trackedServiceReference);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<ServletContext> serviceReference,
			ServiceReference<ServletContext> trackedServiceReference) {

			JSConfig jsConfig = _jsConfigs.remove(serviceReference);

			if (jsConfig != null) {
				_bundleContext.ungetService(serviceReference);

				_lastModified = System.currentTimeMillis();
			}
		}

		private final BundleContext _bundleContext;

	}

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			JSBundleConfigRegistryUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ServletContext.class, "osgi.web.contextpath",
			new JSBundleConfigServiceTrackerCustomizer(bundleContext));
	}

}