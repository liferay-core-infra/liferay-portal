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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
		new LiferayIntegrationTestRule();

	@Test
	public void testAddAuditEventPseudonymField() throws Exception {
		_testAddAuditEventPseudonymField(
			AuditEventConstants.CONTEXT_NAME_INSTANCE);
		_testAddAuditEventPseudonymField(StringPool.BLANK);
		_testAddAuditEventPseudonymField(null);

		String contextName = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		AuditEventPseudonymField auditEventPseudonymField =
			_addAuditEventPseudonymField(contextName, value);

		Assert.assertEquals(
			contextName, auditEventPseudonymField.getContextName());

		AuditEventPseudonymField curAuditEventPseudonymField =
			_addAuditEventPseudonymField(contextName, value);

		Assert.assertEquals(
			auditEventPseudonymField.getAuditEventPseudonymFieldId(),
			curAuditEventPseudonymField.getAuditEventPseudonymFieldId());

		_addAuditEventPseudonymField(null, value);

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

		long auditEventPseudonymFieldsCount =
			_getAuditEventPseudonymFieldsCount();

		String json = String.valueOf(auditMessage.toJSONObject());
		AuditMessage pseudonymizedAuditMessage = _pseudonymizeAuditMessage(
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
			auditMessage.getUserId(), pseudonymizedAuditMessage.getUserId());
		Assert.assertEquals(
			pseudonymizedAuditMessage.getUserEmailAddress(),
			pseudonymizedAuditMessage.getUserLogin());

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
		pseudonymizedAuditMessage = _pseudonymizeAuditMessage(auditMessage);

		JSONAssert.assertEquals(
			json, String.valueOf(pseudonymizedAuditMessage.toJSONObject()),
			JSONCompareMode.STRICT);

		Assert.assertEquals(
			auditEventPseudonymFieldsCount + 6,
			_getAuditEventPseudonymFieldsCount());

		_testPseudonymizeAuditMessageWhenAllocationCollides();
		_testPseudonymizeAuditMessageWhenPseudonymizationFails();
		_testPseudonymizeAuditMessageWhenTransactionIsRolledBack();
		_testPseudonymizeAuditMessageWhenUserLoginIsUserId();
		_testPseudonymizeAuditMessageWithImpersonatedUserValues();
		_testPseudonymizeAuditMessageWithUnmatchedValues();
	}

	private AuditEventPseudonymField _addAuditEventPseudonymField(
			String contextName, String value)
		throws Exception {

		AuditEventPseudonymField auditEventPseudonymField =
			_auditEventPseudonymFieldLocalService.addAuditEventPseudonymField(
				TestPropsValues.getCompanyId(), contextName, _NAME, value);

		_auditEventPseudonymFields.add(auditEventPseudonymField);

		return auditEventPseudonymField;
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

	private AuditMessage _pseudonymizeAuditMessage(AuditMessage auditMessage) {
		AuditMessage pseudonymizedAuditMessage =
			_auditEventPseudonymFieldLocalService.pseudonymizeAuditMessage(
				auditMessage);

		for (String pseudonym :
				new String[] {
					pseudonymizedAuditMessage.getClientIP(),
					pseudonymizedAuditMessage.getImpersonatedUserEmailAddress(),
					pseudonymizedAuditMessage.getImpersonatedUserName(),
					pseudonymizedAuditMessage.getObjectName(),
					pseudonymizedAuditMessage.getUserEmailAddress(),
					pseudonymizedAuditMessage.getUserName()
				}) {

			AuditEventPseudonymField auditEventPseudonymField =
				_auditEventPseudonymFieldLocalService.
					fetchAuditEventPseudonymField(
						GetterUtil.getLong(pseudonym));

			if (auditEventPseudonymField == null) {
				continue;
			}

			_auditEventPseudonymFields.add(auditEventPseudonymField);
		}

		return pseudonymizedAuditMessage;
	}

	private void _testAddAuditEventPseudonymField(String contextName)
		throws Exception {

		String value = RandomTestUtil.randomString();

		AuditEventPseudonymField auditEventPseudonymField =
			_addAuditEventPseudonymField(contextName, value);

		Assert.assertEquals(
			AuditEventConstants.CONTEXT_NAME_INSTANCE,
			auditEventPseudonymField.getContextName());
		Assert.assertEquals(
			DigesterUtil.digestHex(DigesterUtil.SHA_256, value),
			auditEventPseudonymField.getValueHash());

		AuditEventPseudonymField curAuditEventPseudonymField =
			_addAuditEventPseudonymField(null, value);

		Assert.assertEquals(
			auditEventPseudonymField.getAuditEventPseudonymFieldId(),
			curAuditEventPseudonymField.getAuditEventPseudonymFieldId());
	}

	private void _testAddAuditEventPseudonymFieldWithInvalidValue(
		String expectedMessage, String value) {

		AssertUtils.assertFailure(
			AuditEventPseudonymFieldValueException.class, expectedMessage,
			() -> _addAuditEventPseudonymField(null, value));
	}

	private void _testAddAuditEventPseudonymFieldWithSimilarValues(String value)
		throws Exception {

		AuditEventPseudonymField auditEventPseudonymField =
			_addAuditEventPseudonymField(null, value);

		Assert.assertEquals(value, auditEventPseudonymField.getValue());
	}

	private void _testPseudonymizeAuditMessageWhenAllocationCollides()
		throws Exception {

		AuditMessage auditMessage = new AuditMessage(
			TestPropsValues.getCompanyId(), RandomTestUtil.randomLong(),
			StringPool.BLANK, RandomTestUtil.randomString());
		String clientIP = RandomTestUtil.randomString();

		auditMessage.setClientIP(clientIP);

		AtomicReference<AuditEventPseudonymField>
			auditEventPseudonymFieldReference = new AtomicReference<>();
		AtomicBoolean collided = new AtomicBoolean();

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

						if (collided.getAndSet(true)) {
							return;
						}

						try {
							auditEventPseudonymFieldReference.set(
								TransactionInvokerUtil.invoke(
									_transactionConfig,
									() ->
										_auditEventPseudonymFieldLocalService.
											addAuditEventPseudonymField(
												auditMessage.getCompanyId(),
												auditMessage.getContextName(),
												"clientIP", clientIP)));
						}
						catch (Throwable throwable) {
							throw new ModelListenerException(throwable);
						}
					}

				},
				null);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"org.hibernate.engine.jdbc.spi.SqlExceptionHelper",
				LoggerTestUtil.OFF)) {

			AuditMessage pseudonymizedAuditMessage = _pseudonymizeAuditMessage(
				auditMessage);

			AuditEventPseudonymField auditEventPseudonymField =
				auditEventPseudonymFieldReference.get();

			Assert.assertEquals(
				String.valueOf(
					auditEventPseudonymField.getAuditEventPseudonymFieldId()),
				pseudonymizedAuditMessage.getClientIP());
		}
		finally {
			serviceRegistration.unregister();
		}
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

			AuditMessage pseudonymizedAuditMessage = _pseudonymizeAuditMessage(
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

	private void _testPseudonymizeAuditMessageWhenTransactionIsRolledBack()
		throws Exception {

		AuditMessage auditMessage = new AuditMessage(
			TestPropsValues.getCompanyId(), RandomTestUtil.randomLong(),
			StringPool.BLANK, RandomTestUtil.randomString());

		auditMessage.setClientIP(RandomTestUtil.randomString());

		AtomicReference<AuditMessage> pseudonymizedAuditMessageReference =
			new AtomicReference<>();

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					pseudonymizedAuditMessageReference.set(
						_pseudonymizeAuditMessage(auditMessage));

					throw new PortalException("Roll back");
				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertEquals("Roll back", throwable.getMessage());
		}

		AuditMessage pseudonymizedAuditMessage =
			pseudonymizedAuditMessageReference.get();

		_assertAuditEventPseudonymField(
			"clientIP", pseudonymizedAuditMessage.getClientIP(),
			auditMessage.getClientIP());
	}

	private void _testPseudonymizeAuditMessageWhenUserLoginIsUserId()
		throws Exception {

		AuditMessage auditMessage = _createAuditMessage();

		String userLogin = String.valueOf(auditMessage.getUserId());

		auditMessage.setUserLogin(userLogin);

		AuditMessage pseudonymizedAuditMessage = _pseudonymizeAuditMessage(
			auditMessage);

		Assert.assertEquals(
			userLogin, pseudonymizedAuditMessage.getUserLogin());
	}

	private void _testPseudonymizeAuditMessageWithImpersonatedUserValues()
		throws Exception {

		AuditMessage auditMessage = _createAuditMessage();

		auditMessage.setImpersonatedUserEmailAddress(
			auditMessage.getUserEmailAddress());
		auditMessage.setImpersonatedUserName(auditMessage.getUserName());

		AuditMessage pseudonymizedAuditMessage = _pseudonymizeAuditMessage(
			auditMessage);

		Assert.assertEquals(
			pseudonymizedAuditMessage.getUserEmailAddress(),
			pseudonymizedAuditMessage.getImpersonatedUserEmailAddress());
		Assert.assertEquals(
			pseudonymizedAuditMessage.getUserName(),
			pseudonymizedAuditMessage.getImpersonatedUserName());
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

		AuditMessage pseudonymizedAuditMessage = _pseudonymizeAuditMessage(
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

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	@Inject
	private AuditEventPseudonymFieldLocalService
		_auditEventPseudonymFieldLocalService;

	@DeleteAfterTestRun
	private final Set<AuditEventPseudonymField> _auditEventPseudonymFields =
		new HashSet<>();

}