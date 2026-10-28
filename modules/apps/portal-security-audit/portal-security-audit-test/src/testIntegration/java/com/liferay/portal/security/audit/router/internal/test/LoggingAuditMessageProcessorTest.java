/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration;
import com.liferay.portal.security.audit.storage.model.AuditEventPseudonymField;
import com.liferay.portal.security.audit.storage.service.AuditEventPseudonymFieldLocalService;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;

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
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class LoggingAuditMessageProcessorTest {

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
	public void testProcess() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					LoggingAuditMessageProcessorConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", true
					).put(
						"logMessageFormat", "JSON"
					).build())) {

			Bundle bundle = FrameworkUtil.getBundle(getClass());

			BundleContext bundleContext = bundle.getBundleContext();

			Collection<ServiceReference<AuditMessageProcessor>>
				serviceReferences = bundleContext.getServiceReferences(
					AuditMessageProcessor.class,
					"(component.name=" + _CLASS_NAME + ")");

			Assert.assertEquals(
				serviceReferences.toString(), 1, serviceReferences.size());

			for (ServiceReference<AuditMessageProcessor> serviceReference :
					serviceReferences) {

				AuditMessageProcessor auditMessageProcessor =
					bundleContext.getService(serviceReference);

				try {
					_testProcessWhenPseudonymizationIsDisabled(
						auditMessageProcessor);
					_testProcessWhenPseudonymizationIsEnabled(
						auditMessageProcessor);
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			}
		}
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

	private void _testProcessWhenPseudonymizationIsDisabled(
			AuditMessageProcessor auditMessageProcessor)
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(_pid, _getProperties(false));
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.INFO)) {

			AuditMessage auditMessage = new AuditMessage(
				TestPropsValues.getCompanyId(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			auditMessage.setClientIP(RandomTestUtil.randomString());

			auditMessageProcessor.process(auditMessage);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				logEntry.getMessage());

			Assert.assertEquals(
				auditMessage.getClientIP(), jsonObject.getString("clientIP"));
			Assert.assertFalse(jsonObject.getBoolean("pseudonymized"));
			Assert.assertEquals(
				auditMessage.getUserName(), jsonObject.getString("userName"));
		}
	}

	private void _testProcessWhenPseudonymizationIsEnabled(
			AuditMessageProcessor auditMessageProcessor)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.INFO)) {

			AuditMessage auditMessage = new AuditMessage(
				TestPropsValues.getCompanyId(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			String clientIP = RandomTestUtil.randomString();

			auditMessage.setClientIP(clientIP);

			auditMessageProcessor.process(auditMessage);

			Assert.assertEquals(clientIP, auditMessage.getClientIP());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				logEntry.getMessage());

			AuditEventPseudonymField clientIPAuditEventPseudonymField =
				_auditEventPseudonymFieldLocalService.
					fetchAuditEventPseudonymField(
						GetterUtil.getLong(jsonObject.getString("clientIP")));

			_auditEventPseudonymFields.add(clientIPAuditEventPseudonymField);

			AuditEventPseudonymField userNameAuditEventPseudonymField =
				_auditEventPseudonymFieldLocalService.
					fetchAuditEventPseudonymField(
						GetterUtil.getLong(jsonObject.getString("userName")));

			_auditEventPseudonymFields.add(userNameAuditEventPseudonymField);

			Assert.assertEquals(
				clientIP, clientIPAuditEventPseudonymField.getValue());
			Assert.assertTrue(jsonObject.getBoolean("pseudonymized"));
			Assert.assertEquals(
				auditMessage.getUserName(),
				userNameAuditEventPseudonymField.getValue());
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.security.audit.router.internal." +
			"LoggingAuditMessageProcessor";

	private static final String _FACTORY_PID =
		AuditConfiguration.class.getName() + ".scoped";

	@Inject
	private AuditEventPseudonymFieldLocalService
		_auditEventPseudonymFieldLocalService;

	@DeleteAfterTestRun
	private final List<AuditEventPseudonymField> _auditEventPseudonymFields =
		new ArrayList<>();

	private String _pid;

}