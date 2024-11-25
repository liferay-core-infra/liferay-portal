/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.extender.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.language.UTF8Control;
import com.liferay.portal.kernel.resource.bundle.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.CacheResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ClassResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.LanguageResources;

import java.net.URL;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Preston Crary
 */
@Component(service = {})
public class LanguageResourcesExtender
	implements BundleTrackerCustomizer<List<ServiceRegistration<?>>> {

	@Override
	public List<ServiceRegistration<?>> addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		List<BundleCapability> bundleCapabilities =
			bundleWiring.getCapabilities("liferay.language.resources");

		if (ListUtil.isEmpty(bundleCapabilities)) {
			return null;
		}

		List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

		for (BundleCapability bundleCapability : bundleCapabilities) {
			Map<String, Object> attributes = bundleCapability.getAttributes();

			Object baseName = attributes.get("resource.bundle.base.name");

			if (baseName instanceof String) {
				if (GetterUtil.getBoolean(attributes.get("module.only"))) {
					_registerResourceBundleLoader(
						bundle, bundleCapability, (String)baseName,
						serviceRegistrations);

					continue;
				}

				_registerResourceBundles(
					bundle, (String)baseName,
					GetterUtil.getInteger(
						attributes.get(Constants.SERVICE_RANKING)),
					serviceRegistrations);
			}
		}

		return serviceRegistrations;
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		List<ServiceRegistration<?>> serviceRegistrations) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		List<ServiceRegistration<?>> serviceRegistrations) {

		for (ServiceRegistration<?> serviceRegistration :
				serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, this);

		_bundleTracker.open();

		DependencyManagerSyncUtil.registerSyncCallable(
			() -> {
				bundleContext.addServiceListener(
					_serviceListener,
					"(&(!(javax.portlet.name=*))(language.id=*)(objectClass=" +
						ResourceBundle.class.getName() + "))");

				return null;
			});
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext.removeServiceListener(_serviceListener);

		_bundleTracker.close();
	}

	private void _registerResourceBundleLoader(
		Bundle bundle, BundleCapability bundleCapability, String baseName,
		List<ServiceRegistration<?>> serviceRegistrations) {

		Dictionary<String, Object> attributes = HashMapDictionaryBuilder.create(
			bundleCapability.getAttributes()
		).build();

		Object bundleSymbolicName = attributes.get("bundle.symbolic.name");
		Object serviceRanking = attributes.get(Constants.SERVICE_RANKING);
		Object servletContextName = attributes.get("servlet.context.name");

		if (bundleSymbolicName == null) {
			attributes.put("bundle.symbolic.name", bundle.getSymbolicName());
		}

		attributes.put(
			Constants.SERVICE_RANKING, GetterUtil.getInteger(serviceRanking));

		if (servletContextName == null) {
			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			String webContextName = headers.get("Web-ContextName");

			if (Validator.isNotNull(webContextName)) {
				attributes.put("servlet.context.name", webContextName);
			}
			else {
				String webContextPath = headers.get("Web-ContextPath");

				if (Validator.isNotNull(webContextPath)) {
					attributes.put(
						"servlet.context.name", webContextPath.substring(1));
				}
			}
		}

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ResourceBundleLoader resourceBundleLoader =
			new CacheResourceBundleLoader(
				new AggregateResourceBundleLoader(
					new ClassResourceBundleLoader(
						baseName, bundleWiring.getClassLoader()),
					LanguageResources.PORTAL_RESOURCE_BUNDLE_LOADER));

		serviceRegistrations.add(
			_bundleContext.registerService(
				ResourceBundleLoader.class, resourceBundleLoader, attributes));
	}

	private void _registerResourceBundles(
		Bundle bundle, String baseName, int serviceRanking,
		List<ServiceRegistration<?>> serviceRegistrations) {

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		int index = baseName.lastIndexOf(StringPool.PERIOD);

		String path = StringPool.SLASH;
		String name = baseName;

		if (index > 0) {
			path = baseName.substring(0, index);

			path =
				StringPool.SLASH +
					StringUtil.replace(path, CharPool.PERIOD, CharPool.SLASH);

			name = baseName.substring(index + 1);
		}

		Enumeration<URL> enumeration = bundle.findEntries(
			path, name.concat("*.properties"), false);

		if (enumeration == null) {
			return;
		}

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			String urlPath = url.getPath();

			String languageId = StringPool.BLANK;

			index = urlPath.indexOf(StringPool.UNDERLINE, path.length());

			if (index > -1) {
				languageId = urlPath.substring(
					index + 1, urlPath.length() - ".properties".length());
			}

			Locale locale = LocaleUtil.fromLanguageId(languageId, false);

			ServiceRegistration<?> serviceRegistration =
				_bundleContext.registerService(
					ResourceBundle.class,
					new ServiceFactory<ResourceBundle>() {

						@Override
						public ResourceBundle getService(
							Bundle bundle,
							ServiceRegistration<ResourceBundle>
								serviceRegistration) {

							return ResourceBundle.getBundle(
								baseName, locale, bundleWiring.getClassLoader(),
								UTF8Control.INSTANCE);
						}

						@Override
						public void ungetService(
							Bundle bundle,
							ServiceRegistration<ResourceBundle>
								serviceRegistration,
							ResourceBundle resourceBundle) {
						}

					},
					HashMapDictionaryBuilder.<String, Object>put(
						Constants.SERVICE_RANKING, serviceRanking
					).put(
						"language.id", languageId
					).build());

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private BundleContext _bundleContext;
	private BundleTracker<?> _bundleTracker;
	private final ServiceListener _serviceListener =
		serviceEvent -> CacheResourceBundleLoader.clearCache();

}