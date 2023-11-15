/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.access.control.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.access.control.AccessControl;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierConfiguration;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.security.auth.AuthVerifierPipeline;
import com.liferay.portal.security.auth.registry.AuthVerifierRegistry;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Peter Fellwock
 */
public class AccessControlTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpAccessControl();
		_setUpAuthVerifier();
		_setUpAuthVerifierConfiguration();
		_setUpAuthVerifierRegistry();
		_setUpPortal();
		_setUpUserLocalService();
	}

	@Test
	public void testVerifyRequest() {
		String contextPath = "";
		String includeURLs = StringBundler.concat(
			_BASE_URL, "/regular/*,", _BASE_URL, "/legacy*");

		String legacyRequestURI = contextPath + _BASE_URL + "/legacy/Hello";
		String regularRequestURI = contextPath + _BASE_URL + "/regular/Hello";

		AuthVerifierResult.State expectedState =
			AuthVerifierResult.State.SUCCESS;

		_assertAuthVerifierResult(
			contextPath, includeURLs, legacyRequestURI, expectedState);
		_assertAuthVerifierResult(
			contextPath, includeURLs, regularRequestURI, expectedState);
	}

	@Test
	public void testVerifyRequestWithContextPath() {
		String contextPath = "/abc";
		String includeURLs = StringBundler.concat(
			_BASE_URL, "/regular/*,", _BASE_URL, "/legacy*");

		String requestURI = contextPath + _BASE_URL + "/regular/Hello";

		AuthVerifierResult.State expectedState =
			AuthVerifierResult.State.SUCCESS;

		_assertAuthVerifierResult(
			contextPath, includeURLs, requestURI, expectedState);
	}

	@Test
	public void testVerifyRequestWithContextPathNotAffectedByPortalProxyPath() {
		String contextPath = "/abc";
		String includeURLs = StringBundler.concat(
			_BASE_URL, "/regular/*,", _BASE_URL, "/legacy*");

		String requestURI = contextPath + _BASE_URL + "/regular/Hello";

		AuthVerifierResult.State expectedState =
			AuthVerifierResult.State.SUCCESS;

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"PORTAL_PROXY_PATH", "/proxy")) {

			_setUpPortal();

			_assertAuthVerifierResult(
				contextPath, includeURLs, requestURI, expectedState);
		}
	}

	@Test
	public void testVerifyRequestWithNonmatchingRequestURI() {
		String contextPath = "";
		String includeURLs = StringBundler.concat(
			_BASE_URL, "/regular/*,", _BASE_URL, "/legacy*");

		String requestURI = contextPath + _BASE_URL + "/non/matching";

		AuthVerifierResult.State expectedState =
			AuthVerifierResult.State.UNSUCCESSFUL;

		_assertAuthVerifierResult(
			contextPath, includeURLs, requestURI, expectedState);
	}

	private void _assertAuthVerifierResult(
		String contextPath, String includeURLs, String requestURI,
		AuthVerifierResult.State expectedState) {

		AuthVerifierResult authVerifierResult = _verifyRequest(
			contextPath, includeURLs, requestURI);

		Assert.assertSame(expectedState, authVerifierResult.getState());
	}

	private void _setUpAccessControl() {
		ReflectionTestUtil.setFieldValue(
			_accessControl, "_authVerifierRegistry", _authVerifierRegistry);
		ReflectionTestUtil.setFieldValue(_accessControl, "_portal", _portal);
		ReflectionTestUtil.setFieldValue(
			_accessControl, "_userLocalService", _userLocalService);
	}

	private void _setUpAuthVerifier() {
		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		authVerifierResult.setSettings(new HashMap<>());
		authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);

		_authVerifier = (AuthVerifier)ProxyUtil.newProxyInstance(
			AuthVerifier.class.getClassLoader(),
			new Class<?>[] {AuthVerifier.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "verify")) {
					return authVerifierResult;
				}

				return null;
			});
	}

	private void _setUpAuthVerifierConfiguration() {
		_authVerifierConfiguration = new AuthVerifierConfiguration();

		Class<? extends AuthVerifier> clazz = _authVerifier.getClass();

		_authVerifierConfiguration.setAuthVerifierClassName(clazz.getName());
	}

	private void _setUpAuthVerifierRegistry() {
		Mockito.when(
			_authVerifierRegistry.getAuthVerifier(
				_authVerifierConfiguration.getAuthVerifierClassName())
		).thenReturn(
			_authVerifier
		);
	}

	private void _setUpPortal() {
		Mockito.when(
			_portal.getCompanyId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			0L
		);
	}

	private void _setUpUserLocalService() throws Exception {
		User user = new UserImpl();

		user.setStatus(WorkflowConstants.STATUS_APPROVED);

		Mockito.when(
			_userLocalService.fetchUser(Mockito.anyLong())
		).thenReturn(
			user
		);

		Mockito.when(
			_userLocalService.getGuestUserId(Mockito.anyLong())
		).thenReturn(
			user.getUserId()
		);
	}

	private AuthVerifierResult _verifyRequest(
		String contextPath, String includeURLs, String requestURI) {

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<AuthVerifier> serviceRegistration =
			bundleContext.registerService(
				AuthVerifier.class, _authVerifier,
				MapUtil.singletonDictionary("urls.includes", includeURLs));

		try {
			Properties properties = new Properties();

			properties.put("urls.includes", includeURLs);

			_authVerifierConfiguration.setProperties(properties);

			AccessControlContext accessControlContext =
				new AccessControlContext();

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest(new MockServletContext());

			mockHttpServletRequest.setRequestURI(requestURI);

			accessControlContext.setRequest(mockHttpServletRequest);

			return ReflectionTestUtil.invoke(
				_accessControl, "_verifyRequest",
				new Class<?>[] {
					AccessControlContext.class, AuthVerifierPipeline.class
				},
				accessControlContext,
				new AuthVerifierPipeline(
					Collections.singletonList(_authVerifierConfiguration),
					contextPath));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private static final String _BASE_URL = "/TestAuthVerifier";

	private static final AccessControl _accessControl = new AccessControlImpl();
	private static final AuthVerifierRegistry _authVerifierRegistry =
		Mockito.mock(AuthVerifierRegistry.class);
	private static final Portal _portal = Mockito.mock(Portal.class);
	private static final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

	private AuthVerifier _authVerifier;
	private AuthVerifierConfiguration _authVerifierConfiguration;

}