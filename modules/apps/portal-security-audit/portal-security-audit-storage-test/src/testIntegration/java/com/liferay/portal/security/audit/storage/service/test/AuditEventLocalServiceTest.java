/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.constants.AuditEventConstants;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.model.AuditEventPseudonymField;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.security.audit.storage.service.AuditEventPseudonymFieldLocalService;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@RunWith(Arquillian.class)
public class AuditEventLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			_FACTORY_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"pseudonymizationEnabled", true
			).build());
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteFactoryConfiguration(_pid, _FACTORY_PID);
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testAddAuditEvent() throws Exception {
		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new ArrayList<>());

		long companyId = RandomTestUtil.randomLong();

		auditMessage.setCompanyId(companyId);

		auditMessage.setCorrelationId(RandomTestUtil.randomString());
		auditMessage.setHttpMethod(RandomTestUtil.randomString());
		auditMessage.setImpersonated(RandomTestUtil.randomBoolean());
		auditMessage.setImpersonatedUserEmailAddress(
			RandomTestUtil.randomString());
		auditMessage.setImpersonatedUserId(RandomTestUtil.randomLong());
		auditMessage.setImpersonatedUserName(RandomTestUtil.randomString());
		auditMessage.setObjectName(RandomTestUtil.randomString());
		auditMessage.setRequestId(RandomTestUtil.randomString());
		auditMessage.setRequestIdGenerated(RandomTestUtil.randomBoolean());
		auditMessage.setResourceAction(RandomTestUtil.randomString());
		auditMessage.setResourceType(RandomTestUtil.randomString());
		auditMessage.setRoles(RandomTestUtil.randomString());
		auditMessage.setUserAgent(RandomTestUtil.randomString());
		auditMessage.setUserEmailAddress(RandomTestUtil.randomString());

		AuditEvent auditEvent = _auditEventLocalService.addAuditEvent(
			auditMessage);

		_auditEvents.add(auditEvent);

		Assert.assertEquals(
			auditEvent.getAccountEntryId(), auditMessage.getAccountEntryId());
		Assert.assertEquals(
			auditEvent.getContextName(), auditMessage.getContextName());
		Assert.assertEquals(
			auditEvent.getCorrelationId(), auditMessage.getCorrelationId());
		Assert.assertEquals(
			auditEvent.getHttpMethod(), auditMessage.getHttpMethod());
		Assert.assertEquals(
			auditEvent.isImpersonated(), auditMessage.isImpersonated());
		Assert.assertEquals(
			auditEvent.getImpersonatedUserEmailAddress(),
			auditMessage.getImpersonatedUserEmailAddress());
		Assert.assertEquals(
			auditEvent.getImpersonatedUserId(),
			auditMessage.getImpersonatedUserId());
		Assert.assertEquals(
			auditEvent.getImpersonatedUserName(),
			auditMessage.getImpersonatedUserName());
		Assert.assertEquals(
			auditEvent.getObjectName(), auditMessage.getObjectName());
		Assert.assertFalse(auditEvent.isPseudonymized());
		Assert.assertEquals(
			auditEvent.getRequestId(), auditMessage.getRequestId());
		Assert.assertEquals(
			auditEvent.isRequestIdGenerated(),
			auditMessage.isRequestIdGenerated());
		Assert.assertEquals(
			auditEvent.getResourceAction(), auditMessage.getResourceAction());
		Assert.assertEquals(
			auditEvent.getResourceType(), auditMessage.getResourceType());
		Assert.assertEquals(auditEvent.getRoles(), auditMessage.getRoles());
		Assert.assertEquals(
			auditEvent.getUserAgent(), auditMessage.getUserAgent());
		Assert.assertEquals(
			auditEvent.getUserEmailAddress(),
			auditMessage.getUserEmailAddress());

		AuditEvent persistedAuditEvent =
			_auditEventLocalService.fetchAuditEvent(
				auditEvent.getAuditEventId());

		Assert.assertEquals(companyId, persistedAuditEvent.getCompanyId());

		_testAddAuditEvent(StringPool.BLANK);
		_testAddAuditEvent(null);
		_testAddAuditEventInCTCollection();
		_testAddAuditEventWhenPseudonymizationIsEnabled();
	}

	@Test
	public void testGetAuditEvents() {
		long companyId = RandomTestUtil.randomLong();
		String value = RandomTestUtil.randomString();

		_addAuditEvent(companyId, true, value + RandomTestUtil.randomString());

		AuditEvent auditEvent1 = _addAuditEvent(
			companyId, false,
			RandomTestUtil.randomString() + value +
				RandomTestUtil.randomString());
		AuditEvent auditEvent2 = _addAuditEvent(
			companyId, false, value + RandomTestUtil.randomString());
		AuditEvent auditEvent3 = _addAuditEvent(companyId, true, value);

		_testGetAuditEvents(
			false, null, null, companyId, value, auditEvent1, auditEvent2,
			auditEvent3);
		_testGetAuditEvents(
			false, null, value, companyId, null, auditEvent1, auditEvent2,
			auditEvent3);
		_testGetAuditEvents(
			false, value, null, companyId, null, auditEvent1, auditEvent2,
			auditEvent3);
		_testGetAuditEvents(
			true, null, null, companyId, value, auditEvent1, auditEvent2,
			auditEvent3);
		_testGetAuditEvents(
			true, null, value, companyId, null, auditEvent1, auditEvent2,
			auditEvent3);
		_testGetAuditEvents(
			true, value, null, companyId, null, auditEvent1, auditEvent2,
			auditEvent3);
	}

	private AuditEvent _addAuditEvent(
		long companyId, boolean pseudonymized, String value) {

		AuditEvent auditEvent = _auditEventLocalService.createAuditEvent(
			RandomTestUtil.nextLong());

		auditEvent.setCompanyId(companyId);
		auditEvent.setUserName(value);
		auditEvent.setClientHost(value);
		auditEvent.setClientIP(value);
		auditEvent.setPseudonymized(pseudonymized);

		auditEvent = _auditEventLocalService.addAuditEvent(auditEvent);

		_auditEvents.add(auditEvent);

		return auditEvent;
	}

	private void _testAddAuditEvent(String contextName) {
		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), contextName,
			RandomTestUtil.randomString(), new ArrayList<>());

		auditMessage.setCompanyId(RandomTestUtil.randomLong());

		AuditEvent auditEvent = _auditEventLocalService.addAuditEvent(
			auditMessage);

		_auditEvents.add(auditEvent);

		Assert.assertEquals(
			AuditEventConstants.CONTEXT_NAME_INSTANCE,
			auditEvent.getContextName());
	}

	private void _testAddAuditEventInCTCollection() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_testAddAuditEventWhenPseudonymizationIsEnabled();
		}
	}

	private void _testAddAuditEventWhenPseudonymizationIsEnabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					true, "LPD-6417")) {

			AuditMessage auditMessage = new AuditMessage(
				TestPropsValues.getCompanyId(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			auditMessage.setClientIP(RandomTestUtil.randomString());

			AuditEvent auditEvent = _auditEventLocalService.addAuditEvent(
				auditMessage);

			_auditEvents.add(auditEvent);

			AuditEventPseudonymField clientIPAuditEventPseudonymField =
				_auditEventPseudonymFieldLocalService.
					fetchAuditEventPseudonymField(
						GetterUtil.getLong(auditEvent.getClientIP()));

			_auditEventPseudonymFields.add(clientIPAuditEventPseudonymField);

			AuditEventPseudonymField userNameAuditEventPseudonymField =
				_auditEventPseudonymFieldLocalService.
					fetchAuditEventPseudonymField(
						GetterUtil.getLong(auditEvent.getUserName()));

			_auditEventPseudonymFields.add(userNameAuditEventPseudonymField);

			Assert.assertEquals(
				auditMessage.getClientIP(),
				clientIPAuditEventPseudonymField.getValue());
			Assert.assertTrue(auditEvent.isPseudonymized());
			Assert.assertEquals(
				auditMessage.getUserName(),
				userNameAuditEventPseudonymField.getValue());
		}
	}

	private void _testGetAuditEvents(
		boolean andSearch, String clientHost, String clientIP, long companyId,
		String userName, AuditEvent... expectedAuditEvents) {

		Assert.assertEquals(
			SetUtil.fromArray(expectedAuditEvents),
			SetUtil.fromList(
				_auditEventLocalService.getAuditEvents(
					companyId, 0, 0, userName, null, null, null, null, null,
					clientHost, clientIP, null, null, null, 0, null, andSearch,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)));
		Assert.assertEquals(
			expectedAuditEvents.length,
			_auditEventLocalService.getAuditEventsCount(
				companyId, 0, 0, userName, null, null, null, null, null,
				clientHost, clientIP, null, null, null, 0, null, andSearch));
	}

	private static final String _FACTORY_PID =
		AuditConfiguration.class.getName() + ".scoped";

	@Inject
	private AuditEventLocalService _auditEventLocalService;

	@Inject
	private AuditEventPseudonymFieldLocalService
		_auditEventPseudonymFieldLocalService;

	@DeleteAfterTestRun
	private final List<AuditEventPseudonymField> _auditEventPseudonymFields =
		new ArrayList<>();

	@DeleteAfterTestRun
	private final List<AuditEvent> _auditEvents = new ArrayList<>();

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	private String _pid;

}