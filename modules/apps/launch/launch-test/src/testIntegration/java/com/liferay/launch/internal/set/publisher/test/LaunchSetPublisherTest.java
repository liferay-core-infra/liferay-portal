/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.publisher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.launch.set.publisher.LaunchSetPublisher;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 * @author Jose Luis Navarro
 */
@RunWith(Arquillian.class)
public class LaunchSetPublisherTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_launchSetObjectEntry = _addLaunchSetObjectEntry();
	}

	@After
	public void tearDown() throws Exception {
		_launchSetPublisher.unschedulePublish(
			_launchSetObjectEntry.getObjectEntryId());

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testPublish() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ObjectEntry launchEntryObjectEntry = _addLaunchEntryObjectEntry(
			_launchSetObjectEntry.getObjectEntryId(),
			JournalArticle.class.getName(), journalArticle.getResourcePrimKey(),
			String.valueOf(journalArticle.getVersion()));

		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.publish(
				launchEntryObjectEntry.getObjectEntryId()));

		String title = RandomTestUtil.randomString();

		long draftId = _updateLaunchDraft(launchEntryObjectEntry, title);

		String unknownClassName1 = RandomTestUtil.randomString();

		ObjectEntry unknownLaunchEntryObjectEntry1 = _addLaunchEntryObjectEntry(
			_launchSetObjectEntry.getObjectEntryId(), unknownClassName1,
			RandomTestUtil.randomInt(), RandomTestUtil.randomString());

		String unknownClassName2 = RandomTestUtil.randomString();

		ObjectEntry unknownLaunchEntryObjectEntry2 = _addLaunchEntryObjectEntry(
			_launchSetObjectEntry.getObjectEntryId(), unknownClassName2,
			RandomTestUtil.randomInt(), RandomTestUtil.randomString());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.launch.internal.set.publisher." +
					"LaunchSetPublisherImpl",
				LoggerTestUtil.WARN)) {

			try {
				_launchSetPublisher.publish(
					_launchSetObjectEntry.getObjectEntryId());

				Assert.fail();
			}
			catch (PortalException portalException) {
				Assert.assertEquals(
					"Unable to publish 2 launch entries of launch set " +
						_launchSetObjectEntry.getObjectEntryId(),
					portalException.getMessage());
			}

			Map<String, String> messages = new HashMap<>();

			for (LogEntry logEntry : logCapture.getLogEntries()) {
				Throwable throwable = logEntry.getThrowable();

				messages.put(logEntry.getMessage(), throwable.getMessage());
			}

			Assert.assertEquals(messages.toString(), 2, messages.size());
			Assert.assertEquals(
				"No launch entry manager exists for " + unknownClassName1,
				messages.get(
					"Unable to publish launch entry " +
						unknownLaunchEntryObjectEntry1.getObjectEntryId()));
			Assert.assertEquals(
				"No launch entry manager exists for " + unknownClassName2,
				messages.get(
					"Unable to publish launch entry " +
						unknownLaunchEntryObjectEntry2.getObjectEntryId()));
		}

		LaunchEntryManager launchEntryManager =
			_getJournalArticleLaunchEntryManager();

		LaunchEntryManager.Version version =
			launchEntryManager.fetchPublishedVersion(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			String.valueOf(journalArticle.getVersion()),
			version.getVersionKey());

		Assert.assertEquals(
			draftId,
			MapUtil.getLong(
				_getValues(
					_objectEntryLocalService.getObjectEntry(
						launchEntryObjectEntry.getObjectEntryId())),
				"draftId"));

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				_launchSetObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			2,
			MapUtil.getLong(
				launchSetObjectEntry.getValues(), "publishErrorsCount"));
		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, launchSetObjectEntry.getStatus());

		List<ObjectEntry> launchSetErrorObjectEntries =
			_getLaunchSetErrorObjectEntries(_launchSetObjectEntry);

		Assert.assertEquals(
			launchSetErrorObjectEntries.toString(), 2,
			launchSetErrorObjectEntries.size());

		_assertLaunchSetError(
			unknownLaunchEntryObjectEntry1, launchSetErrorObjectEntries,
			"No launch entry manager exists for " + unknownClassName1);
		_assertLaunchSetError(
			unknownLaunchEntryObjectEntry2, launchSetErrorObjectEntries,
			"No launch entry manager exists for " + unknownClassName2);

		_objectEntryLocalService.deleteObjectEntry(
			unknownLaunchEntryObjectEntry1);
		_objectEntryLocalService.deleteObjectEntry(
			unknownLaunchEntryObjectEntry2);

		_launchSetPublisher.publish(_launchSetObjectEntry.getObjectEntryId());

		LaunchEntryManager.Version publishedVersion =
			launchEntryManager.fetchPublishedVersion(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(title, _getTitle(journalArticle));
		Assert.assertNotEquals(
			String.valueOf(journalArticle.getVersion()),
			publishedVersion.getVersionKey());

		_assertPublished(
			draftId, launchEntryObjectEntry, launchEntryManager,
			publishedVersion);

		launchSetObjectEntry = _objectEntryLocalService.getObjectEntry(
			_launchSetObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			launchSetObjectEntry.getStatus());

		launchSetErrorObjectEntries = _getLaunchSetErrorObjectEntries(
			_launchSetObjectEntry);

		Assert.assertEquals(
			launchSetErrorObjectEntries.toString(), 0,
			launchSetErrorObjectEntries.size());

		try {
			_launchSetPublisher.publish(
				_launchSetObjectEntry.getObjectEntryId());

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertTrue(
				portalException.getMessage(),
				StringUtil.endsWith(
					portalException.getMessage(), "was already published"));
		}

		ObjectEntry otherLaunchSetObjectEntry = _addLaunchSetObjectEntry();

		ObjectEntry otherLaunchEntryObjectEntry = _addLaunchEntryObjectEntry(
			otherLaunchSetObjectEntry.getObjectEntryId(),
			JournalArticle.class.getName(), journalArticle.getResourcePrimKey(),
			String.valueOf(journalArticle.getVersion()));

		String otherTitle = RandomTestUtil.randomString();

		long otherDraftId = _updateLaunchDraft(
			otherLaunchEntryObjectEntry, otherTitle);

		JournalTestUtil.updateArticle(
			_journalArticleLocalService.getLatestArticle(
				journalArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED),
			RandomTestUtil.randomString());

		_launchSetPublisher.publish(
			otherLaunchSetObjectEntry.getObjectEntryId());

		LaunchEntryManager.Version otherPublishedVersion =
			launchEntryManager.fetchPublishedVersion(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(otherTitle, _getTitle(journalArticle));
		Assert.assertNotEquals(
			publishedVersion.getVersionKey(),
			otherPublishedVersion.getVersionKey());

		_assertPublished(
			otherDraftId, otherLaunchEntryObjectEntry, launchEntryManager,
			otherPublishedVersion);

		// Launch entry whose publish fails with a blank message

		ObjectEntry blankMessageLaunchSetObjectEntry =
			_addLaunchSetObjectEntry();

		long blankMessageLaunchSetId =
			blankMessageLaunchSetObjectEntry.getObjectEntryId();

		String blankMessageClassName = RandomTestUtil.randomString();

		ObjectEntry blankMessageLaunchEntryObjectEntry =
			_addLaunchEntryObjectEntry(
				blankMessageLaunchSetObjectEntry.getObjectEntryId(),
				blankMessageClassName, RandomTestUtil.randomInt(), null);

		ServiceRegistration<LaunchEntryManager> serviceRegistration =
			_registerLaunchEntryManager(
				blankMessageClassName,
				() -> {
					throw new PortalException(StringPool.SPACE);
				});

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.launch.internal.set.publisher." +
					"LaunchSetPublisherImpl",
				LoggerTestUtil.WARN)) {

			Assert.assertThrows(
				PortalException.class,
				() -> _launchSetPublisher.publish(blankMessageLaunchSetId));

			_assertLogEntry(
				PortalException.class, StringPool.SPACE,
				blankMessageLaunchEntryObjectEntry, logCapture);
		}
		finally {
			serviceRegistration.unregister();
		}

		blankMessageLaunchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(blankMessageLaunchSetId);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			blankMessageLaunchSetObjectEntry.getStatus());

		launchSetErrorObjectEntries = _getLaunchSetErrorObjectEntries(
			blankMessageLaunchSetObjectEntry);

		Assert.assertEquals(
			launchSetErrorObjectEntries.toString(), 1,
			launchSetErrorObjectEntries.size());

		ObjectEntry launchSetErrorObjectEntry = launchSetErrorObjectEntries.get(
			0);

		Map<String, Serializable> values =
			launchSetErrorObjectEntry.getValues();

		Assert.assertEquals(
			blankMessageLaunchEntryObjectEntry.getObjectEntryId(),
			MapUtil.getLong(values, "launchEntryId"));
		Assert.assertEquals(
			PortalException.class.getName(),
			MapUtil.getString(values, "message"));

		// Launch entry whose publish fails with a long message

		ObjectEntry longMessageLaunchSetObjectEntry =
			_addLaunchSetObjectEntry();

		long longMessageLaunchSetId =
			longMessageLaunchSetObjectEntry.getObjectEntryId();

		String longMessageClassName = RandomTestUtil.randomString();

		ObjectEntry longMessageLaunchEntryObjectEntry =
			_addLaunchEntryObjectEntry(
				longMessageLaunchSetObjectEntry.getObjectEntryId(),
				longMessageClassName, RandomTestUtil.randomInt(), null);

		String longMessage = RandomTestUtil.randomString(70000);

		serviceRegistration = _registerLaunchEntryManager(
			longMessageClassName,
			() -> {
				throw new PortalException(longMessage);
			});

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.launch.internal.set.publisher." +
					"LaunchSetPublisherImpl",
				LoggerTestUtil.WARN)) {

			Assert.assertThrows(
				PortalException.class,
				() -> _launchSetPublisher.publish(longMessageLaunchSetId));

			_assertLogEntry(
				PortalException.class, longMessage,
				longMessageLaunchEntryObjectEntry, logCapture);
		}
		finally {
			serviceRegistration.unregister();
		}

		launchSetErrorObjectEntries = _getLaunchSetErrorObjectEntries(
			longMessageLaunchSetObjectEntry);

		Assert.assertEquals(
			launchSetErrorObjectEntries.toString(), 1,
			launchSetErrorObjectEntries.size());

		launchSetErrorObjectEntry = launchSetErrorObjectEntries.get(0);

		values = launchSetErrorObjectEntry.getValues();

		String message = MapUtil.getString(values, "message");

		Assert.assertEquals(65000, message.length());
		Assert.assertTrue(message.startsWith(longMessage.substring(0, 64000)));

		// Launch entry with a title longer than a text field

		String longTitle = RandomTestUtil.randomString(300);

		JournalArticle longTitleJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, longTitle,
			RandomTestUtil.randomString());

		ObjectEntry longTitleLaunchSetObjectEntry = _addLaunchSetObjectEntry();

		ObjectEntry longTitleLaunchEntryObjectEntry =
			_addLaunchEntryObjectEntry(
				longTitleLaunchSetObjectEntry.getObjectEntryId(),
				JournalArticle.class.getName(),
				longTitleJournalArticle.getResourcePrimKey(),
				String.valueOf(longTitleJournalArticle.getVersion()));

		long longTitleDraftId = MapUtil.getLong(
			_getValues(longTitleLaunchEntryObjectEntry), "draftId");

		launchEntryManager.deleteDraft(
			longTitleJournalArticle.getResourcePrimKey(), longTitleDraftId);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.launch.internal.set.publisher." +
					"LaunchSetPublisherImpl",
				LoggerTestUtil.WARN)) {

			Assert.assertThrows(
				PortalException.class,
				() -> _launchSetPublisher.publish(
					longTitleLaunchSetObjectEntry.getObjectEntryId()));

			_assertLogEntry(
				NoSuchArticleException.class,
				"No JournalArticle exists with the key {resourcePrimKey=" +
					longTitleDraftId + "}",
				longTitleLaunchEntryObjectEntry, logCapture);
		}

		launchSetErrorObjectEntries = _getLaunchSetErrorObjectEntries(
			longTitleLaunchSetObjectEntry);

		Assert.assertEquals(
			launchSetErrorObjectEntries.toString(), 1,
			launchSetErrorObjectEntries.size());

		launchSetErrorObjectEntry = launchSetErrorObjectEntries.get(0);

		values = launchSetErrorObjectEntry.getValues();

		Assert.assertEquals(
			longTitleLaunchEntryObjectEntry.getObjectEntryId(),
			MapUtil.getLong(values, "launchEntryId"));
		Assert.assertEquals(
			longTitle, MapUtil.getString(values, "launchEntryTitle"));

		// Launch set published by another publish while this one fails

		ObjectEntry concurrentLaunchSetObjectEntry = _addLaunchSetObjectEntry();

		long concurrentLaunchSetId =
			concurrentLaunchSetObjectEntry.getObjectEntryId();

		String concurrentClassName = RandomTestUtil.randomString();

		ObjectEntry concurrentLaunchEntryObjectEntry =
			_addLaunchEntryObjectEntry(
				concurrentLaunchSetObjectEntry.getObjectEntryId(),
				concurrentClassName, RandomTestUtil.randomInt(), null);

		String concurrentMessage = RandomTestUtil.randomString();

		serviceRegistration = _registerLaunchEntryManager(
			concurrentClassName,
			() -> {
				TransactionInvokerUtil.invoke(
					_requiresNewTransactionConfig,
					() -> _objectEntryLocalService.updateStatus(
						TestPropsValues.getUserId(), concurrentLaunchSetId,
						WorkflowConstants.STATUS_APPROVED, _serviceContext));

				throw new PortalException(concurrentMessage);
			});

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.launch.internal.set.publisher." +
					"LaunchSetPublisherImpl",
				LoggerTestUtil.WARN)) {

			Assert.assertThrows(
				PortalException.class,
				() -> _launchSetPublisher.publish(concurrentLaunchSetId));

			_assertLogEntry(
				PortalException.class, concurrentMessage,
				concurrentLaunchEntryObjectEntry, logCapture);
		}
		finally {
			serviceRegistration.unregister();
		}

		concurrentLaunchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				concurrentLaunchSetObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			concurrentLaunchSetObjectEntry.getStatus());

		launchSetErrorObjectEntries = _getLaunchSetErrorObjectEntries(
			concurrentLaunchSetObjectEntry);

		Assert.assertEquals(
			launchSetErrorObjectEntries.toString(), 0,
			launchSetErrorObjectEntries.size());
	}

	@Test
	public void testSchedulePublish() throws Exception {
		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		ObjectEntry launchEntryObjectEntry = _addLaunchEntryObjectEntry(
			launchSetId, RandomTestUtil.randomString(),
			RandomTestUtil.randomInt(), RandomTestUtil.randomString());

		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.schedulePublish(
				new Date(System.currentTimeMillis() + Time.DAY),
				launchEntryObjectEntry.getObjectEntryId(),
				TestPropsValues.getUserId()));

		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.schedulePublish(
				new Date(System.currentTimeMillis() - Time.MINUTE), launchSetId,
				TestPropsValues.getUserId()));
		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.schedulePublish(
				null, launchSetId, TestPropsValues.getUserId()));

		Date date = new Date(System.currentTimeMillis() + Time.DAY);

		_launchSetPublisher.schedulePublish(
			date, launchSetId, TestPropsValues.getUserId());

		_assertScheduledDateAndStatus(date, WorkflowConstants.STATUS_SCHEDULED);

		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.schedulePublish(
				new Date(System.currentTimeMillis() + Time.DAY), launchSetId,
				TestPropsValues.getUserId()));
		_assertScheduledDateAndStatus(date, WorkflowConstants.STATUS_SCHEDULED);
	}

	@Test
	public void testUnschedulePublish() throws Exception {
		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		ObjectEntry launchEntryObjectEntry = _addLaunchEntryObjectEntry(
			launchSetId, RandomTestUtil.randomString(),
			RandomTestUtil.randomInt(), RandomTestUtil.randomString());

		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.unschedulePublish(
				launchEntryObjectEntry.getObjectEntryId()));

		_launchSetPublisher.unschedulePublish(launchSetId);

		_assertScheduledDateAndStatus(null, WorkflowConstants.STATUS_DRAFT);

		_launchSetPublisher.schedulePublish(
			new Date(System.currentTimeMillis() + Time.DAY), launchSetId,
			TestPropsValues.getUserId());

		_launchSetPublisher.unschedulePublish(launchSetId);

		_assertScheduledDateAndStatus(null, WorkflowConstants.STATUS_DRAFT);

		Date date = new Date(System.currentTimeMillis() + Time.WEEK);

		_launchSetPublisher.schedulePublish(
			date, launchSetId, TestPropsValues.getUserId());

		_assertScheduledDateAndStatus(date, WorkflowConstants.STATUS_SCHEDULED);
	}

	private ObjectEntry _addLaunchEntryObjectEntry(
			long launchSetId, String className, long classPK,
			String baseVersionKey)
		throws Exception {

		return _addObjectEntry(
			"LaunchEntry",
			HashMapBuilder.<String, Serializable>put(
				"baseVersionKey", baseVersionKey
			).put(
				"className", className
			).put(
				"classPK", classPK
			).put(
				"r_launchSetToLaunchEntries_c_launchSetId", launchSetId
			).build());
	}

	private ObjectEntry _addLaunchSetObjectEntry() throws Exception {
		return _addObjectEntry(
			"LaunchSet",
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).build());
	}

	private ObjectEntry _addObjectEntry(
			String objectDefinitionName, Map<String, Serializable> values)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), objectDefinitionName);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), values,
			serviceContext);
	}

	private void _assertLaunchSetError(
		ObjectEntry launchEntryObjectEntry,
		List<ObjectEntry> launchSetErrorObjectEntries, String message) {

		Map<String, Serializable> values = null;

		for (ObjectEntry launchSetErrorObjectEntry :
				launchSetErrorObjectEntries) {

			long launchEntryId = MapUtil.getLong(
				launchSetErrorObjectEntry.getValues(), "launchEntryId");

			if (launchEntryId == launchEntryObjectEntry.getObjectEntryId()) {
				values = launchSetErrorObjectEntry.getValues();

				break;
			}
		}

		Assert.assertNotNull(launchSetErrorObjectEntries.toString(), values);
		Assert.assertEquals(
			StringPool.BLANK, MapUtil.getString(values, "launchEntryTitle"));
		Assert.assertEquals(message, MapUtil.getString(values, "message"));
		Assert.assertEquals("error", MapUtil.getString(values, "severity"));
	}

	private void _assertLogEntry(
		Class<?> exceptionClass, String exceptionMessage,
		ObjectEntry launchEntryObjectEntry, LogCapture logCapture) {

		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(
			"Unable to publish launch entry " +
				launchEntryObjectEntry.getObjectEntryId(),
			logEntry.getMessage());
		Assert.assertEquals("WARN", logEntry.getPriority());

		Throwable throwable = logEntry.getThrowable();

		Assert.assertSame(exceptionClass, throwable.getClass());
		Assert.assertEquals(exceptionMessage, throwable.getMessage());
	}

	private void _assertPublished(
			long draftId, ObjectEntry launchEntryObjectEntry,
			LaunchEntryManager launchEntryManager,
			LaunchEntryManager.Version publishedVersion)
		throws Exception {

		Map<String, Serializable> values = _getValues(launchEntryObjectEntry);

		Assert.assertEquals(
			publishedVersion.getVersionKey(),
			MapUtil.getString(values, "versionKey"));
		Assert.assertEquals(0, MapUtil.getLong(values, "draftId"));

		Assert.assertNull(
			launchEntryManager.fetchDraft(
				MapUtil.getLong(values, "classPK"), draftId));
	}

	private void _assertScheduledDateAndStatus(
			Date expectedDate, int expectedStatus)
		throws Exception {

		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(launchSetId);

		Assert.assertEquals(expectedStatus, launchSetObjectEntry.getStatus());

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				StringBundler.concat(
					launchSetId, StringPool.AT,
					launchSetObjectEntry.getCompanyId()),
				"liferay/launch_set_scheduled_publish", StorageType.PERSISTED);

		Date scheduledDate = null;

		if (schedulerResponse != null) {
			scheduledDate = _schedulerEngineHelper.getStartDate(
				schedulerResponse);
		}

		Map<String, Serializable> values = launchSetObjectEntry.getValues();

		Date scheduledDateValue = (Date)values.get("scheduledDate");

		if (expectedDate == null) {
			Assert.assertNull(scheduledDate);
			Assert.assertNull(scheduledDateValue);

			return;
		}

		Assert.assertEquals(expectedDate, scheduledDate);
		Assert.assertEquals(
			expectedDate.getTime() / Time.SECOND,
			scheduledDateValue.getTime() / Time.SECOND);
	}

	private LaunchEntryManager _getJournalArticleLaunchEntryManager() {
		return _launchEntryManagerRegistry.getLaunchEntryManager(
			JournalArticle.class.getName());
	}

	private List<ObjectEntry> _getLaunchSetErrorObjectEntries(
			ObjectEntry launchSetObjectEntry)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				launchSetObjectEntry.getObjectDefinitionId(),
				"launchSetToLaunchSetErrors");

		return _objectEntryLocalService.getOneToManyObjectEntries(
			launchSetObjectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, false,
			launchSetObjectEntry.getObjectEntryId(), true, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private String _getTitle(JournalArticle journalArticle) throws Exception {
		JournalArticle latestJournalArticle =
			_journalArticleLocalService.getLatestArticle(
				journalArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED);

		return latestJournalArticle.getTitle(LocaleUtil.getSiteDefault());
	}

	private Map<String, Serializable> _getValues(ObjectEntry objectEntry)
		throws Exception {

		ObjectEntry reloadedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

		return reloadedObjectEntry.getValues();
	}

	private ServiceRegistration<LaunchEntryManager> _registerLaunchEntryManager(
		String className,
		UnsafeSupplier<LaunchEntryManager.Version, Throwable> unsafeSupplier) {

		Bundle bundle = FrameworkUtil.getBundle(LaunchSetPublisherTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			LaunchEntryManager.class,
			(LaunchEntryManager)ProxyUtil.newProxyInstance(
				LaunchEntryManager.class.getClassLoader(),
				new Class<?>[] {LaunchEntryManager.class},
				(proxy, method, arguments) -> {
					if (Objects.equals(method.getName(), "publishDraft")) {
						return unsafeSupplier.get();
					}

					return null;
				}),
			HashMapDictionaryBuilder.<String, Object>put(
				"launch.entry.manager.class.name", className
			).build());
	}

	private long _updateLaunchDraft(
			ObjectEntry launchEntryObjectEntry, String title)
		throws Exception {

		long draftId = MapUtil.getLong(
			_getValues(launchEntryObjectEntry), "draftId");

		JournalArticle launchDraftJournalArticle =
			_journalArticleLocalService.getLatestArticle(
				draftId, WorkflowConstants.STATUS_ANY, false);

		ServiceContext launchDraftServiceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		launchDraftServiceContext.setWorkflowAction(
			WorkflowConstants.ACTION_SAVE_DRAFT);

		_journalArticleLocalService.updateArticle(
			TestPropsValues.getUserId(), launchDraftJournalArticle.getGroupId(),
			launchDraftJournalArticle.getFolderId(),
			launchDraftJournalArticle.getArticleId(),
			launchDraftJournalArticle.getVersion(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), title
			).build(),
			launchDraftJournalArticle.getDescriptionMap(),
			launchDraftJournalArticle.getContent(),
			launchDraftJournalArticle.getLayoutUuid(),
			launchDraftServiceContext);

		return draftId;
	}

	private static final TransactionConfig _requiresNewTransactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	private ObjectEntry _launchSetObjectEntry;

	@Inject
	private LaunchSetPublisher _launchSetPublisher;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

	private ServiceContext _serviceContext;

}