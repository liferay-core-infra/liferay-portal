/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.security.audit.constants.AuditEventConstants;
import com.liferay.portal.security.audit.storage.exception.AuditEventPseudonymFieldValueException;
import com.liferay.portal.security.audit.storage.model.AuditEventPseudonymField;
import com.liferay.portal.security.audit.storage.service.AuditEventPseudonymFieldLocalService;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class AuditEventPseudonymFieldLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.security.audit.storage.service"));

	@Test
	public void testAddAuditEventPseudonymField() throws Exception {
		_testAddAuditEventPseudonymField(
			AuditEventConstants.CONTEXT_NAME_INSTANCE);
		_testAddAuditEventPseudonymField(StringPool.BLANK);
		_testAddAuditEventPseudonymField(null);

		String contextName = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		AuditEventPseudonymField auditEventPseudonymField =
			_auditEventPseudonymFieldLocalService.addAuditEventPseudonymField(
				TestPropsValues.getCompanyId(), contextName, _NAME, value);

		Assert.assertEquals(
			contextName, auditEventPseudonymField.getContextName());

		AuditEventPseudonymField curAuditEventPseudonymField =
			_auditEventPseudonymFieldLocalService.addAuditEventPseudonymField(
				TestPropsValues.getCompanyId(), contextName, _NAME, value);

		Assert.assertEquals(
			auditEventPseudonymField.getAuditEventPseudonymFieldId(),
			curAuditEventPseudonymField.getAuditEventPseudonymFieldId());

		_addAuditEventPseudonymField(value);

		DynamicQuery dynamicQuery =
			_auditEventPseudonymFieldLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyId", TestPropsValues.getCompanyId()));
		dynamicQuery.add(RestrictionsFactoryUtil.eq("name", _NAME));
		dynamicQuery.add(RestrictionsFactoryUtil.eq("value", value));

		Assert.assertEquals(
			2,
			_auditEventPseudonymFieldLocalService.dynamicQueryCount(
				dynamicQuery));

		_testAddAuditEventPseudonymFieldWithInvalidValue(
			"Maximum length of value exceeded",
			RandomTestUtil.randomString(256));
		_testAddAuditEventPseudonymFieldWithInvalidValue(
			"Value is blank", StringPool.BLANK);
		_testAddAuditEventPseudonymFieldWithInvalidValue(
			"Value is blank", null);

		_testAddAuditEventPseudonymFieldWithSimilarValues("CASEY");
		_testAddAuditEventPseudonymFieldWithSimilarValues("Casey");
		_testAddAuditEventPseudonymFieldWithSimilarValues("Jose");
		_testAddAuditEventPseudonymFieldWithSimilarValues("José");
		_testAddAuditEventPseudonymFieldWithSimilarValues("casey");
		_testAddAuditEventPseudonymFieldWithSimilarValues("casey ");
	}

	@Test
	public void testPseudonymizeAuditMessage() throws Exception {
		AuditMessage auditMessage = _createAuditMessage();

		String json = String.valueOf(auditMessage.toJSONObject());

		long auditEventPseudonymFieldsCount =
			_getAuditEventPseudonymFieldsCount();

		AuditMessage pseudonymizedAuditMessage =
			_auditEventPseudonymFieldLocalService.pseudonymizeAuditMessage(
				auditMessage);

		JSONAssert.assertEquals(
			json, String.valueOf(auditMessage.toJSONObject()),
			JSONCompareMode.STRICT);

		Assert.assertEquals(
			auditMessage.getClassPK(), pseudonymizedAuditMessage.getClassPK());
		Assert.assertEquals(
			pseudonymizedAuditMessage.getClientIP(),
			pseudonymizedAuditMessage.getClientHost());
		Assert.assertEquals(
			auditMessage.getImpersonatedUserId(),
			pseudonymizedAuditMessage.getImpersonatedUserId());
		Assert.assertTrue(pseudonymizedAuditMessage.isPseudonymized());
		Assert.assertEquals(
			pseudonymizedAuditMessage.getUserEmailAddress(),
			pseudonymizedAuditMessage.getUserLogin());
		Assert.assertEquals(
			auditMessage.getUserId(), pseudonymizedAuditMessage.getUserId());

		_assertAuditEventPseudonymField(
			"clientIP", pseudonymizedAuditMessage.getClientIP(),
			auditMessage.getClientIP());
		_assertAuditEventPseudonymField(
			"objectName", pseudonymizedAuditMessage.getObjectName(),
			auditMessage.getObjectName());
		_assertAuditEventPseudonymField(
			"userEmailAddress",
			pseudonymizedAuditMessage.getImpersonatedUserEmailAddress(),
			auditMessage.getImpersonatedUserEmailAddress());
		_assertAuditEventPseudonymField(
			"userEmailAddress", pseudonymizedAuditMessage.getUserEmailAddress(),
			auditMessage.getUserEmailAddress());
		_assertAuditEventPseudonymField(
			"userName", pseudonymizedAuditMessage.getImpersonatedUserName(),
			auditMessage.getImpersonatedUserName());
		_assertAuditEventPseudonymField(
			"userName", pseudonymizedAuditMessage.getUserName(),
			auditMessage.getUserName());

		Assert.assertEquals(
			auditEventPseudonymFieldsCount + 6,
			_getAuditEventPseudonymFieldsCount());

		json = String.valueOf(pseudonymizedAuditMessage.toJSONObject());

		pseudonymizedAuditMessage =
			_auditEventPseudonymFieldLocalService.pseudonymizeAuditMessage(
				auditMessage);

		JSONAssert.assertEquals(
			json, String.valueOf(pseudonymizedAuditMessage.toJSONObject()),
			JSONCompareMode.STRICT);

		Assert.assertEquals(
			auditEventPseudonymFieldsCount + 6,
			_getAuditEventPseudonymFieldsCount());

		_testPseudonymizeAuditMessageWhenPseudonymizationFails();
		_testPseudonymizeAuditMessageWhenUserLoginIsUserId();
		_testPseudonymizeAuditMessageWithUnmatchedValues();
	}

	private AuditEventPseudonymField _addAuditEventPseudonymField(String value)
		throws Exception {

		return _auditEventPseudonymFieldLocalService.
			addAuditEventPseudonymField(
				TestPropsValues.getCompanyId(), null, _NAME, value);
	}

	private void _assertAuditEventPseudonymField(
			String name, String pseudonym, String value)
		throws Exception {

		AuditEventPseudonymField auditEventPseudonymField =
			_auditEventPseudonymFieldLocalService.fetchAuditEventPseudonymField(
				GetterUtil.getLong(pseudonym));

		Assert.assertEquals(
			TestPropsValues.getCompanyId(),
			auditEventPseudonymField.getCompanyId());
		Assert.assertEquals(name, auditEventPseudonymField.getName());
		Assert.assertEquals(value, auditEventPseudonymField.getValue());
	}

	private AuditMessage _createAuditMessage() throws Exception {
		long userId = RandomTestUtil.randomLong();

		AuditMessage auditMessage = new AuditMessage(
			TestPropsValues.getCompanyId(), userId,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		auditMessage.setClassName(RandomTestUtil.randomString());
		auditMessage.setClassPK(userId);

		String clientIP = RandomTestUtil.randomString();

		auditMessage.setClientHost(clientIP);
		auditMessage.setClientIP(clientIP);

		auditMessage.setImpersonatedUserEmailAddress(
			RandomTestUtil.randomString());
		auditMessage.setImpersonatedUserId(RandomTestUtil.randomLong());
		auditMessage.setImpersonatedUserName(RandomTestUtil.randomString());
		auditMessage.setMessage(RandomTestUtil.randomString());
		auditMessage.setObjectName(RandomTestUtil.randomString());

		String userEmailAddress = RandomTestUtil.randomString();

		auditMessage.setUserEmailAddress(userEmailAddress);
		auditMessage.setUserLogin(userEmailAddress);

		return auditMessage;
	}

	private long _getAuditEventPseudonymFieldsCount() throws Exception {
		DynamicQuery dynamicQuery =
			_auditEventPseudonymFieldLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyId", TestPropsValues.getCompanyId()));

		return _auditEventPseudonymFieldLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	private void _testAddAuditEventPseudonymField(String contextName)
		throws Exception {

		String value = RandomTestUtil.randomString();

		AuditEventPseudonymField auditEventPseudonymField =
			_auditEventPseudonymFieldLocalService.addAuditEventPseudonymField(
				TestPropsValues.getCompanyId(), contextName, _NAME, value);

		Assert.assertEquals(
			AuditEventConstants.CONTEXT_NAME_INSTANCE,
			auditEventPseudonymField.getContextName());
		Assert.assertEquals(
			DigesterUtil.digestHex(DigesterUtil.SHA_256, value),
			auditEventPseudonymField.getValueHash());

		AuditEventPseudonymField curAuditEventPseudonymField =
			_addAuditEventPseudonymField(value);

		Assert.assertEquals(
			auditEventPseudonymField.getAuditEventPseudonymFieldId(),
			curAuditEventPseudonymField.getAuditEventPseudonymFieldId());
	}

	private void _testAddAuditEventPseudonymFieldWithInvalidValue(
		String expectedMessage, String value) {

		AssertUtils.assertFailure(
			AuditEventPseudonymFieldValueException.class, expectedMessage,
			() -> _addAuditEventPseudonymField(value));
	}

	private void _testAddAuditEventPseudonymFieldWithSimilarValues(String value)
		throws Exception {

		AuditEventPseudonymField auditEventPseudonymField =
			_addAuditEventPseudonymField(value);

		Assert.assertEquals(value, auditEventPseudonymField.getValue());
	}

	private void _testPseudonymizeAuditMessageWhenPseudonymizationFails()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				ModelListener.class,
				new BaseModelListener<AuditEventPseudonymField>() {

					@Override
					public void onBeforeCreate(
							AuditEventPseudonymField auditEventPseudonymField)
						throws ModelListenerException {

						throw new ModelListenerException();
					}

				},
				null);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.security.audit.storage.service.impl." +
					"AuditEventPseudonymFieldLocalServiceImpl",
				LoggerTestUtil.ERROR)) {

			AuditMessage auditMessage = _createAuditMessage();

			long auditEventPseudonymFieldsCount =
				_getAuditEventPseudonymFieldsCount();

			AuditMessage pseudonymizedAuditMessage =
				_auditEventPseudonymFieldLocalService.pseudonymizeAuditMessage(
					auditMessage);

			Assert.assertEquals(
				auditMessage.getClassPK(),
				pseudonymizedAuditMessage.getClassPK());
			Assert.assertEquals(
				StringPool.BLANK, pseudonymizedAuditMessage.getClientHost());
			Assert.assertEquals(
				StringPool.BLANK, pseudonymizedAuditMessage.getClientIP());
			Assert.assertEquals(
				StringPool.BLANK,
				pseudonymizedAuditMessage.getImpersonatedUserEmailAddress());
			Assert.assertEquals(
				auditMessage.getImpersonatedUserId(),
				pseudonymizedAuditMessage.getImpersonatedUserId());
			Assert.assertEquals(
				StringPool.BLANK,
				pseudonymizedAuditMessage.getImpersonatedUserName());
			Assert.assertEquals(
				StringPool.BLANK, pseudonymizedAuditMessage.getObjectName());
			Assert.assertTrue(pseudonymizedAuditMessage.isPseudonymized());
			Assert.assertEquals(
				StringPool.BLANK,
				pseudonymizedAuditMessage.getUserEmailAddress());
			Assert.assertEquals(
				auditMessage.getUserId(),
				pseudonymizedAuditMessage.getUserId());
			Assert.assertEquals(
				StringPool.BLANK, pseudonymizedAuditMessage.getUserLogin());
			Assert.assertEquals(
				StringPool.BLANK, pseudonymizedAuditMessage.getUserName());

			Assert.assertEquals(
				auditEventPseudonymFieldsCount,
				_getAuditEventPseudonymFieldsCount());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to pseudonymize the audit message",
				logEntry.getMessage());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testPseudonymizeAuditMessageWhenUserLoginIsUserId()
		throws Exception {

		AuditMessage auditMessage = _createAuditMessage();

		String userLogin = String.valueOf(auditMessage.getUserId());

		auditMessage.setUserLogin(userLogin);

		AuditMessage pseudonymizedAuditMessage =
			_auditEventPseudonymFieldLocalService.pseudonymizeAuditMessage(
				auditMessage);

		Assert.assertEquals(
			userLogin, pseudonymizedAuditMessage.getUserLogin());
	}

	private void _testPseudonymizeAuditMessageWithUnmatchedValues()
		throws Exception {

		AuditMessage auditMessage = new AuditMessage(
			TestPropsValues.getCompanyId(), RandomTestUtil.randomLong(),
			StringPool.BLANK, RandomTestUtil.randomString());

		auditMessage.setClientHost(RandomTestUtil.randomString());
		auditMessage.setClientIP(RandomTestUtil.randomString());
		auditMessage.setUserLogin(RandomTestUtil.randomString());

		long auditEventPseudonymFieldsCount =
			_getAuditEventPseudonymFieldsCount();

		AuditMessage pseudonymizedAuditMessage =
			_auditEventPseudonymFieldLocalService.pseudonymizeAuditMessage(
				auditMessage);

		Assert.assertEquals(
			StringPool.BLANK, pseudonymizedAuditMessage.getClientHost());
		Assert.assertEquals(
			StringPool.BLANK, pseudonymizedAuditMessage.getUserLogin());

		Assert.assertEquals(
			auditEventPseudonymFieldsCount + 1,
			_getAuditEventPseudonymFieldsCount());
	}

	private static final String _NAME = RandomTestUtil.randomString();

	@Inject
	private AuditEventPseudonymFieldLocalService
		_auditEventPseudonymFieldLocalService;

}