/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.internal.configuration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.AuditConfigurationProvider;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class AuditConfigurationProviderImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			_FACTORY_PID, _getProperties(true));
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteFactoryConfiguration(_pid, _FACTORY_PID);
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testIsPseudonymizationEnabled() throws Exception {
		Assert.assertFalse(
			_auditConfigurationProvider.isPseudonymizationEnabled(
				CompanyConstants.SYSTEM));
		Assert.assertTrue(
			_auditConfigurationProvider.isPseudonymizationEnabled(
				TestPropsValues.getCompanyId()));

		_testIsPseudonymizationEnabledWhenCompanyConfigurationIsDeleted();
		_testIsPseudonymizationEnabledWhenFeatureFlagIsDisabled();
		_testIsPseudonymizationEnabledWhenPseudonymizationIsDisabled();
	}

	private Dictionary<String, Object> _getProperties(
			boolean pseudonymizationEnabled)
		throws Exception {

		return HashMapDictionaryBuilder.<String, Object>put(
			"companyId", TestPropsValues.getCompanyId()
		).put(
			"pseudonymizationEnabled", pseudonymizationEnabled
		).build();
	}

	private void _testIsPseudonymizationEnabledWhenCompanyConfigurationIsDeleted()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					AuditConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"pseudonymizationEnabled", false
					).build())) {

			Assert.assertTrue(
				_auditConfigurationProvider.isPseudonymizationEnabled(
					TestPropsValues.getCompanyId()));

			ConfigurationTestUtil.deleteFactoryConfiguration(
				_pid, _FACTORY_PID);

			Assert.assertFalse(
				_auditConfigurationProvider.isPseudonymizationEnabled(
					TestPropsValues.getCompanyId()));
		}

		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			_FACTORY_PID, _getProperties(true));
	}

	private void _testIsPseudonymizationEnabledWhenFeatureFlagIsDisabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-6417")) {

			Assert.assertFalse(
				_auditConfigurationProvider.isPseudonymizationEnabled(
					TestPropsValues.getCompanyId()));
		}
	}

	private void _testIsPseudonymizationEnabledWhenPseudonymizationIsDisabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_pid, _getProperties(false))) {

			Assert.assertFalse(
				_auditConfigurationProvider.isPseudonymizationEnabled(
					TestPropsValues.getCompanyId()));
		}
	}

	private static final String _FACTORY_PID =
		AuditConfiguration.class.getName() + ".scoped";

	@Inject
	private AuditConfigurationProvider _auditConfigurationProvider;

	private String _pid;

}