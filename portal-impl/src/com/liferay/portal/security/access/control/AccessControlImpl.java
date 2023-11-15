/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.access.control;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.url.pattern.mapper.URLPatternMapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.access.control.AccessControl;
import com.liferay.portal.kernel.security.access.control.AccessControlThreadLocal;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierConfiguration;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.auth.AuthVerifierPipeline;
import com.liferay.portal.security.auth.registry.AuthVerifierRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Raymond Augé
 */
public class AccessControlImpl implements AccessControl {

	@Override
	public void initAccessControlContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Map<String, Object> settings) {

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext != null) {
			throw new IllegalStateException(
				"Authentication context is already initialized");
		}

		accessControlContext = new AccessControlContext();

		accessControlContext.setRequest(httpServletRequest);
		accessControlContext.setResponse(httpServletResponse);

		Map<String, Object> accessControlContextSettings =
			accessControlContext.getSettings();

		accessControlContextSettings.putAll(settings);

		AccessControlUtil.setAccessControlContext(accessControlContext);
	}

	@Override
	public void initContextUser(long userId) throws AuthException {
		try {
			User user = UserLocalServiceUtil.getUser(userId);

			CompanyThreadLocal.setCompanyId(user.getCompanyId());

			PrincipalThreadLocal.setName(userId);

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			AccessControlThreadLocal.setRemoteAccess(false);
		}
		catch (Exception exception) {
			throw new AuthException(exception.getMessage(), exception);
		}
	}

	@Override
	public AuthVerifierResult.State verifyRequest() throws PortalException {
		AuthVerifierResult authVerifierResult = null;

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		Map<String, Object> settings = accessControlContext.getSettings();

		if (!settings.containsKey(AuthVerifierPipeline.class.getName())) {
			authVerifierResult = _verifyRequest(
				accessControlContext,
				AuthVerifierPipeline.getPortalAuthVerifierPipeline());
		}
		else {
			authVerifierResult = _verifyRequest(
				accessControlContext,
				(AuthVerifierPipeline)settings.get(
					AuthVerifierPipeline.class.getName()));

			if ((authVerifierResult.getState() ==
					AuthVerifierResult.State.NOT_APPLICABLE) ||
				(authVerifierResult.getState() ==
					AuthVerifierResult.State.UNSUCCESSFUL)) {

				authVerifierResult = _verifyRequest(
					accessControlContext,
					AuthVerifierPipeline.getPortalAuthVerifierPipeline());
			}
		}

		Map<String, Object> authVerifierResultSettings =
			authVerifierResult.getSettings();

		if (authVerifierResultSettings != null) {
			settings.putAll(authVerifierResultSettings);
		}

		accessControlContext.setAuthVerifierResult(authVerifierResult);

		return authVerifierResult.getState();
	}

	private AuthVerifierResult _createGuestVerificationResult(
			AccessControlContext accessControlContext)
		throws PortalException {

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		authVerifierResult.setState(AuthVerifierResult.State.UNSUCCESSFUL);

		HttpServletRequest httpServletRequest =
			accessControlContext.getRequest();

		authVerifierResult.setUserId(
			UserLocalServiceUtil.getGuestUserId(
				PortalUtil.getCompanyId(httpServletRequest)));

		return authVerifierResult;
	}

	private AuthVerifierResult _verifyRequest(
			AccessControlContext accessControlContext,
			AuthVerifierPipeline authVerifierPipeline)
		throws PortalException {

		if (accessControlContext == null) {
			throw new IllegalArgumentException(
				"Access control context is null");
		}

		HttpServletRequest httpServletRequest =
			accessControlContext.getRequest();

		String requestURI = httpServletRequest.getRequestURI();

		AuthVerifierConfigurationConsumer authVerifierConfigurationConsumer =
			new AuthVerifierConfigurationConsumer(
				accessControlContext,
				authVerifierPipeline.getExcludeURLPatternMapper(), requestURI);

		URLPatternMapper<List<AuthVerifierConfiguration>>
			includeURLPatternMapper =
				authVerifierPipeline.getIncludeURLPatternMapper();

		includeURLPatternMapper.consumeValues(
			authVerifierConfigurationConsumer, requestURI);

		if (authVerifierConfigurationConsumer.getAuthVerifierResult() != null) {
			return authVerifierConfigurationConsumer.getAuthVerifierResult();
		}

		return _createGuestVerificationResult(accessControlContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccessControlImpl.class);

	private static class AuthVerifierConfigurationConsumer
		implements Consumer<List<AuthVerifierConfiguration>> {

		@Override
		public void accept(
			List<AuthVerifierConfiguration> authVerifierConfigurations) {

			if (_authVerifierResult != null) {
				return;
			}

			if (_excludedAuthVerifierConfigurations == null) {
				_excludedAuthVerifierConfigurations = new HashSet<>();

				_excludeURLPatternMapper.consumeValues(
					_excludedAuthVerifierConfigurations::addAll, _requestURI);
			}

			for (AuthVerifierConfiguration authVerifierConfiguration :
					authVerifierConfigurations) {

				if (_excludedAuthVerifierConfigurations.contains(
						authVerifierConfiguration)) {

					continue;
				}

				_authVerifierResult = _verifyWithAuthVerifierConfiguration(
					_accessControlContext, authVerifierConfiguration);

				if (_authVerifierResult != null) {
					return;
				}
			}
		}

		public AuthVerifierResult getAuthVerifierResult() {
			return _authVerifierResult;
		}

		private AuthVerifierConfigurationConsumer(
			AccessControlContext accessControlContext,
			URLPatternMapper<List<AuthVerifierConfiguration>>
				excludeURLPatternMapper,
			String requestURI) {

			_accessControlContext = accessControlContext;
			_excludeURLPatternMapper = excludeURLPatternMapper;
			_requestURI = requestURI;
		}

		private Map<String, Object> _mergeSettings(
			Properties properties, Map<String, Object> settings) {

			Map<String, Object> mergedSettings = new HashMap<>(settings);

			if (properties != null) {
				for (Map.Entry<Object, Object> entry : properties.entrySet()) {
					mergedSettings.put(
						(String)entry.getKey(), entry.getValue());
				}
			}

			return mergedSettings;
		}

		private AuthVerifierResult _verifyWithAuthVerifierConfiguration(
			AccessControlContext accessControlContext,
			AuthVerifierConfiguration authVerifierConfiguration) {

			AuthVerifierResult authVerifierResult = null;

			AuthVerifier authVerifier = AuthVerifierRegistry.getAuthVerifier(
				authVerifierConfiguration.getAuthVerifierClassName());

			if (authVerifier == null) {
				return authVerifierResult;
			}

			Properties properties = authVerifierConfiguration.getProperties();

			try {
				authVerifierResult = authVerifier.verify(
					accessControlContext, properties);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					Class<?> authVerifierClass = authVerifier.getClass();

					_log.debug(
						"Skipping " + authVerifierClass.getName(), exception);
				}

				return null;
			}

			if (authVerifierResult == null) {
				Class<?> authVerifierClass = authVerifier.getClass();

				_log.error(
					"Auth verifier " + authVerifierClass.getName() +
						" did not return an auth verifier result");

				return null;
			}

			if (authVerifierResult.getState() ==
					AuthVerifierResult.State.NOT_APPLICABLE) {

				return null;
			}

			User user = UserLocalServiceUtil.fetchUser(
				authVerifierResult.getUserId());

			if ((user != null) && !user.isActive()) {
				if (_log.isDebugEnabled()) {
					Class<?> authVerifierClass = authVerifier.getClass();

					_log.debug(
						StringBundler.concat(
							"Auth verifier ", authVerifierClass.getName(),
							" returned inactive user",
							authVerifierResult.getUserId()));
				}

				authVerifierResult.setState(
					AuthVerifierResult.State.UNSUCCESSFUL);
			}

			Map<String, Object> settings = _mergeSettings(
				properties, authVerifierResult.getSettings());

			settings.put(
				AuthVerifierPipeline.AUTH_TYPE, authVerifier.getAuthType());

			authVerifierResult.setSettings(settings);

			return authVerifierResult;
		}

		private final AccessControlContext _accessControlContext;
		private AuthVerifierResult _authVerifierResult;
		private Set<AuthVerifierConfiguration>
			_excludedAuthVerifierConfigurations;
		private final URLPatternMapper<List<AuthVerifierConfiguration>>
			_excludeURLPatternMapper;
		private final String _requestURI;

	}

}