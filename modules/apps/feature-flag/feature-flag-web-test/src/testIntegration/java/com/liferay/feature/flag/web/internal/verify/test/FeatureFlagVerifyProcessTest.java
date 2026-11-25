/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portlet.PortalPreferencesWrapper;

import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class FeatureFlagVerifyProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			FeatureFlagVerifyProcessTest.class);

		_bundleContext = bundle.getBundleContext();

		_companyId = TestPropsValues.getCompanyId();

		PropsUtil.set(
			FeatureFlagConstants.getKey(_FEATURE_FLAG_KEY),
			Boolean.TRUE.toString());
		PropsUtil.set(
			FeatureFlagConstants.getKey(_FEATURE_FLAG_KEY, "type"),
			String.valueOf(FeatureFlagType.DEPRECATION));
	}

	@After
	public void tearDown() {
		PropsUtil.set(FeatureFlagConstants.getKey(_FEATURE_FLAG_KEY), null);
		PropsUtil.set(
			FeatureFlagConstants.getKey(_FEATURE_FLAG_KEY, "type"), null);

		PortalPreferences portalPreferences = _getPortalPreferences();

		portalPreferences.setValue(
			FeatureFlagConstants.PREFERENCE_NAMESPACE, _FEATURE_FLAG_KEY, null);
		portalPreferences.setValue(
			FeatureFlagConstants.PREFERENCE_NAMESPACE,
			FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED,
			Boolean.TRUE.toString());

		_portalPreferencesLocalService.updatePreferences(
			_companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			portalPreferences);

		_clearCache();
	}

	@Test
	public void testVerify() throws Exception {
		PortalPreferences portalPreferences = _getPortalPreferences();

		portalPreferences.setValue(
			FeatureFlagConstants.PREFERENCE_NAMESPACE,
			FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED, null);

		_portalPreferencesLocalService.updatePreferences(
			_companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			portalPreferences);

		_clearCache();

		Assert.assertTrue(
			_featureFlagManager.isEnabled(_companyId, _FEATURE_FLAG_KEY));

		Collection<ServiceReference<VerifyProcess>> serviceReferences =
			_bundleContext.getServiceReferences(
				VerifyProcess.class,
				"(component.name=com.liferay.feature.flag.web.internal." +
					"verify.FeatureFlagVerifyProcess)");

		Assert.assertEquals(
			serviceReferences.toString(), 1, serviceReferences.size());

		for (ServiceReference<VerifyProcess> serviceReference :
				serviceReferences) {

			try {
				VerifyProcess verifyProcess = _bundleContext.getService(
					serviceReference);

				verifyProcess.verify();
			}
			finally {
				_bundleContext.ungetService(serviceReference);
			}
		}

		Assert.assertFalse(
			_featureFlagManager.isEnabled(_companyId, _FEATURE_FLAG_KEY));

		portalPreferences = _getPortalPreferences();

		Assert.assertEquals(
			Boolean.FALSE.toString(),
			portalPreferences.getValue(
				FeatureFlagConstants.PREFERENCE_NAMESPACE, _FEATURE_FLAG_KEY));
		Assert.assertEquals(
			Boolean.TRUE.toString(),
			portalPreferences.getValue(
				FeatureFlagConstants.PREFERENCE_NAMESPACE,
				FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED));
	}

	private void _clearCache() {
		ServiceReference<?> serviceReference =
			_bundleContext.getServiceReference(
				"com.liferay.feature.flag.web.internal.feature.flag." +
					"FeatureFlagsBagProvider");

		try {
			ReflectionTestUtil.invoke(
				_bundleContext.getService(serviceReference), "clearCache",
				new Class<?>[0]);
		}
		finally {
			_bundleContext.ungetService(serviceReference);
		}
	}

	private PortalPreferences _getPortalPreferences() {
		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				_portalPreferencesLocalService.getPreferences(
					_companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return portalPreferencesWrapper.getPortalPreferencesImpl();
	}

	private static final String _FEATURE_FLAG_KEY =
		"FAKE-" + RandomTestUtil.randomInt();

	private BundleContext _bundleContext;
	private long _companyId;

	@Inject
	private FeatureFlagManager _featureFlagManager;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

}