/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.publisher;

import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.launch.internal.constants.LaunchDestinationNames;
import com.liferay.launch.internal.helper.LaunchSetErrorHelper;
import com.liferay.launch.set.publisher.LaunchSetPublisher;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Jose Luis Navarro
 */
@Component(service = LaunchSetPublisher.class)
public class LaunchSetPublisherImpl implements LaunchSetPublisher {

	@Override
	public void publish(long launchSetId) throws PortalException {
		ObjectEntry launchSetObjectEntry = _getLaunchSetObjectEntry(
			launchSetId);

		if (launchSetObjectEntry.getStatus() ==
				WorkflowConstants.STATUS_APPROVED) {

			throw new PortalException(
				"Launch set " + launchSetId + " was already published");
		}

		List<Map<String, Serializable>> launchSetErrorValuesList =
			new ArrayList<>();
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		try {
			TransactionInvokerUtil.invoke(
				_requiresNewTransactionConfig,
				() -> {
					_publish(
						launchSetErrorValuesList, launchSetObjectEntry,
						serviceContext);

					return null;
				});
		}
		catch (Throwable throwable) {
			if (launchSetErrorValuesList.isEmpty()) {
				launchSetErrorValuesList.add(
					_launchSetErrorHelper.getLaunchSetErrorValues(
						null, throwable));
			}

			try {
				_launchSetErrorHelper.markPublishFailed(
					launchSetErrorValuesList, launchSetObjectEntry,
					serviceContext.getUserId());
			}
			catch (Throwable throwable2) {
				_log.error(
					"Unable to store the publish errors of launch set " +
						launchSetId,
					throwable2);

				throwable.addSuppressed(throwable2);
			}

			if (throwable instanceof PortalException) {
				throw (PortalException)throwable;
			}

			throw new PortalException(throwable);
		}
	}

	@Override
	public void schedulePublish(Date date, long launchSetId, long userId)
		throws PortalException {

		ObjectEntry launchSetObjectEntry = _getLaunchSetObjectEntry(
			launchSetId);

		if (launchSetObjectEntry.getStatus() !=
				WorkflowConstants.STATUS_DRAFT) {

			throw new PortalException(
				"Launch set " + launchSetId + " must be open to be scheduled");
		}

		if ((date == null) || !date.after(new Date())) {
			throw new PortalException(
				"Launch set " + launchSetId +
					" must be scheduled for a future date");
		}

		ServiceContext serviceContext = _createServiceContext(
			launchSetObjectEntry, userId);

		_updateScheduledDate(
			date, launchSetObjectEntry, serviceContext, userId);

		_objectEntryLocalService.updateStatus(
			userId, launchSetId, WorkflowConstants.STATUS_SCHEDULED,
			serviceContext);

		Message message = new Message();

		message.put("companyId", launchSetObjectEntry.getCompanyId());
		message.put("launchSetId", launchSetId);
		message.put("userId", userId);

		_schedulerEngineHelper.schedule(
			_triggerFactory.createTrigger(
				_getJobName(launchSetObjectEntry),
				LaunchDestinationNames.LAUNCH_SET_SCHEDULED_PUBLISH, date, null,
				0, null),
			StorageType.PERSISTED, String.valueOf(launchSetId),
			LaunchDestinationNames.LAUNCH_SET_SCHEDULED_PUBLISH, message);
	}

	@Override
	public void unschedulePublish(long launchSetId) throws PortalException {
		ObjectEntry launchSetObjectEntry = _getLaunchSetObjectEntry(
			launchSetId);

		if (launchSetObjectEntry.getStatus() ==
				WorkflowConstants.STATUS_SCHEDULED) {

			long userId = PrincipalThreadLocal.getUserId();

			ServiceContext serviceContext = _createServiceContext(
				launchSetObjectEntry, userId);

			_objectEntryLocalService.updateStatus(
				userId, launchSetId, WorkflowConstants.STATUS_DRAFT,
				serviceContext);

			_updateScheduledDate(
				null, launchSetObjectEntry, serviceContext, userId);
		}

		_schedulerEngineHelper.delete(
			_getJobName(launchSetObjectEntry),
			LaunchDestinationNames.LAUNCH_SET_SCHEDULED_PUBLISH,
			StorageType.PERSISTED);
	}

	private ServiceContext _createServiceContext(
		ObjectEntry launchSetObjectEntry, long userId) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(launchSetObjectEntry.getCompanyId());
		serviceContext.setUserId(userId);
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return serviceContext;
	}

	private String _getJobName(ObjectEntry launchSetObjectEntry) {
		return StringBundler.concat(
			launchSetObjectEntry.getObjectEntryId(), StringPool.AT,
			launchSetObjectEntry.getCompanyId());
	}

	private ObjectEntry _getLaunchSetObjectEntry(long launchSetId)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			launchSetId);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_LAUNCH_SET")) {

			throw new PortalException(
				"Object entry " + launchSetId + " is not a launch set");
		}

		return objectEntry;
	}

	private List<ObjectEntry> _getOneToManyObjectEntries(
			ObjectEntry launchSetObjectEntry, String objectRelationshipName)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				launchSetObjectEntry.getObjectDefinitionId(),
				objectRelationshipName);

		return _objectEntryLocalService.getOneToManyObjectEntries(
			launchSetObjectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, false,
			launchSetObjectEntry.getObjectEntryId(), true, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private void _publish(
			List<Map<String, Serializable>> launchSetErrorValuesList,
			ObjectEntry launchSetObjectEntry, ServiceContext serviceContext)
		throws PortalException {

		_launchSetErrorHelper.deleteLaunchSetErrors(launchSetObjectEntry);

		for (ObjectEntry launchEntryObjectEntry :
				_getOneToManyObjectEntries(
					launchSetObjectEntry, "launchSetToLaunchEntries")) {

			Map<String, Serializable> values =
				launchEntryObjectEntry.getValues();

			if ((MapUtil.getLong(values, "draftId") == 0) &&
				Validator.isNotNull(MapUtil.getString(values, "versionKey"))) {

				continue;
			}

			try {
				TransactionInvokerUtil.invoke(
					_nestedTransactionConfig,
					() -> {
						_publishLaunchEntryObjectEntry(
							launchEntryObjectEntry, serviceContext);

						return null;
					});
			}
			catch (Throwable throwable) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to publish launch entry " +
							launchEntryObjectEntry.getObjectEntryId(),
						throwable);
				}

				launchSetErrorValuesList.add(
					_launchSetErrorHelper.getLaunchSetErrorValues(
						launchEntryObjectEntry, throwable));
			}
		}

		if (!launchSetErrorValuesList.isEmpty()) {
			throw new PortalException(
				StringBundler.concat(
					"Unable to publish ", launchSetErrorValuesList.size(),
					" launch entries of launch set ",
					launchSetObjectEntry.getObjectEntryId()));
		}

		_objectEntryLocalService.updateStatus(
			serviceContext.getUserId(), launchSetObjectEntry.getObjectEntryId(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _publishLaunchEntryObjectEntry(
			ObjectEntry launchEntryObjectEntry, ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> values = launchEntryObjectEntry.getValues();

		String className = MapUtil.getString(values, "className");

		LaunchEntryManager launchEntryManager =
			_launchEntryManagerRegistry.getLaunchEntryManager(className);

		if (launchEntryManager == null) {
			throw new PortalException(
				"No launch entry manager exists for " + className);
		}

		LaunchEntryManager.Version version = launchEntryManager.publishDraft(
			MapUtil.getLong(values, "classPK"),
			MapUtil.getLong(values, "draftId"));

		values.put("draftId", 0L);
		values.put("versionKey", version.getVersionKey());

		_objectEntryLocalService.updateObjectEntry(
			serviceContext.getUserId(),
			launchEntryObjectEntry.getObjectEntryId(),
			launchEntryObjectEntry.getObjectEntryFolderId(), values,
			serviceContext);
	}

	private void _updateScheduledDate(
			Date date, ObjectEntry launchSetObjectEntry,
			ServiceContext serviceContext, long userId)
		throws PortalException {

		Map<String, Serializable> values = launchSetObjectEntry.getValues();

		values.put("scheduledDate", date);

		_objectEntryLocalService.updateObjectEntry(
			userId, launchSetObjectEntry.getObjectEntryId(),
			launchSetObjectEntry.getObjectEntryFolderId(), values,
			serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchSetPublisherImpl.class);

	private static final TransactionConfig _nestedTransactionConfig =
		TransactionConfig.Factory.create(
			Propagation.NESTED, new Class<?>[] {Exception.class});
	private static final TransactionConfig _requiresNewTransactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	@Reference
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	@Reference
	private LaunchSetErrorHelper _launchSetErrorHelper;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private TriggerFactory _triggerFactory;

}