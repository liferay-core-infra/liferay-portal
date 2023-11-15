/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.url.pattern.mapper.URLPatternMapper;
import com.liferay.petra.url.pattern.mapper.URLPatternMapperFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierConfiguration;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.context.PortalContextLoaderListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tomas Polesovsky
 * @author Peter Fellwock
 * @author Arthur Chan
 * @author Carlos Sierra Andrés
 */
public class AuthVerifierPipeline {

	public static final String AUTH_TYPE = "auth.type";

	public static String getAuthVerifierPropertyName(String className) {
		String simpleClassName = StringUtil.extractLast(
			className, StringPool.PERIOD);

		return StringBundler.concat(
			PropsKeys.AUTH_VERIFIER, simpleClassName, StringPool.PERIOD);
	}

	public static AuthVerifierPipeline getPortalAuthVerifierPipeline() {
		return PortalAuthVerifierPipelineHolder._PORTAL_AUTH_VERIFIER_PIPELINE;
	}

	public AuthVerifierPipeline(
		List<AuthVerifierConfiguration> authVerifierConfigurations,
		String contextPath) {

		_authVerifierConfigurations = new ArrayList<>(
			authVerifierConfigurations);

		_contextPath = contextPath;

		_buildURLPatternMapper();
	}

	public URLPatternMapper<List<AuthVerifierConfiguration>>
		getExcludeURLPatternMapper() {

		return _excludeURLPatternMapper;
	}

	public URLPatternMapper<List<AuthVerifierConfiguration>>
		getIncludeURLPatternMapper() {

		return _includeURLPatternMapper;
	}

	private synchronized void _addAuthVerifierConfiguration(
		AuthVerifierConfiguration authVerifierConfiguration) {

		_authVerifierConfigurations.add(authVerifierConfiguration);

		_buildURLPatternMapper();
	}

	private void _buildURLPatternMapper() {
		Map<String, List<AuthVerifierConfiguration>>
			excludeAuthVerifierConfigurationsMap = new HashMap<>();
		Map<String, List<AuthVerifierConfiguration>>
			includeAuthVerifierConfigurationsMap = new HashMap<>();

		for (AuthVerifierConfiguration authVerifierConfiguration :
				_authVerifierConfigurations) {

			Properties properties = authVerifierConfiguration.getProperties();

			String[] urlsExcludes = StringUtil.split(
				properties.getProperty("urls.excludes"));

			for (String urlsExclude : urlsExcludes) {
				urlsExclude = _contextPath + _fixLegacyURLPattern(urlsExclude);

				List<AuthVerifierConfiguration>
					excludeAuthVerifierConfigurations =
						excludeAuthVerifierConfigurationsMap.computeIfAbsent(
							urlsExclude, key -> new ArrayList<>());

				excludeAuthVerifierConfigurations.add(
					authVerifierConfiguration);
			}

			String[] urlsIncludes = StringUtil.split(
				properties.getProperty("urls.includes"));

			for (String urlsInclude : urlsIncludes) {
				urlsInclude = _contextPath + _fixLegacyURLPattern(urlsInclude);

				List<AuthVerifierConfiguration>
					includeAuthVerifierConfigurations =
						includeAuthVerifierConfigurationsMap.computeIfAbsent(
							urlsInclude, key -> new ArrayList<>());

				includeAuthVerifierConfigurations.add(
					authVerifierConfiguration);
			}
		}

		_excludeURLPatternMapper = URLPatternMapperFactory.create(
			excludeAuthVerifierConfigurationsMap);
		_includeURLPatternMapper = URLPatternMapperFactory.create(
			includeAuthVerifierConfigurationsMap);
	}

	private String _fixLegacyURLPattern(String urlPattern) {
		if ((urlPattern == null) || (urlPattern.length() == 0) ||
			(urlPattern.charAt(urlPattern.length() - 1) != '*')) {

			return urlPattern;
		}

		if ((urlPattern.length() > 1) &&
			(urlPattern.charAt(urlPattern.length() - 2) == '/')) {

			return urlPattern;
		}

		return urlPattern.substring(0, urlPattern.length() - 1) + "/*";
	}

	private synchronized void _removeAuthVerifierConfiguration(
		AuthVerifierConfiguration authVerifierConfiguration) {

		_authVerifierConfigurations.remove(authVerifierConfiguration);

		_buildURLPatternMapper();
	}

	private final List<AuthVerifierConfiguration> _authVerifierConfigurations;
	private final String _contextPath;
	private volatile URLPatternMapper<List<AuthVerifierConfiguration>>
		_excludeURLPatternMapper;
	private volatile URLPatternMapper<List<AuthVerifierConfiguration>>
		_includeURLPatternMapper;

	private static class PortalAuthVerifierPipelineHolder {

		private static final AuthVerifierPipeline
			_PORTAL_AUTH_VERIFIER_PIPELINE;

		static {
			AuthVerifierPipeline portalAuthVerifierPipeline =
				new AuthVerifierPipeline(
					Collections.emptyList(),
					PortalContextLoaderListener.getPortalServletContextPath());

			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			ServiceTracker<AuthVerifierConfiguration, AuthVerifierConfiguration>
				serviceTracker = new ServiceTracker<>(
					bundleContext, AuthVerifierConfiguration.class,
					new ServiceTrackerCustomizer
						<AuthVerifierConfiguration,
						 AuthVerifierConfiguration>() {

						@Override
						public AuthVerifierConfiguration addingService(
							ServiceReference<AuthVerifierConfiguration>
								serviceReference) {

							AuthVerifierConfiguration
								authVerifierConfiguration =
									bundleContext.getService(serviceReference);

							if (authVerifierConfiguration != null) {
								portalAuthVerifierPipeline.
									_addAuthVerifierConfiguration(
										authVerifierConfiguration);
							}

							return authVerifierConfiguration;
						}

						@Override
						public void modifiedService(
							ServiceReference<AuthVerifierConfiguration>
								serviceReference,
							AuthVerifierConfiguration
								authVerifierConfiguration) {
						}

						@Override
						public void removedService(
							ServiceReference<AuthVerifierConfiguration>
								serviceReference,
							AuthVerifierConfiguration
								authVerifierConfiguration) {

							portalAuthVerifierPipeline.
								_removeAuthVerifierConfiguration(
									authVerifierConfiguration);

							bundleContext.ungetService(serviceReference);
						}

					});

			serviceTracker.open();

			_PORTAL_AUTH_VERIFIER_PIPELINE = portalAuthVerifierPipeline;
		}

	}

}