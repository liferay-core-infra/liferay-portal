/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.background.task;

import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.launch.internal.helper.LaunchSetErrorHelper;
import com.liferay.launch.set.publisher.LaunchSetPublisher;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.launch.internal.background.task.LaunchSetScheduledPublishBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class LaunchSetScheduledPublishBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	public LaunchSetScheduledPublishBackgroundTaskExecutor() {
		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				MapUtil.getLong(
					backgroundTask.getTaskContextMap(), "launchSetId"));

		if ((launchSetObjectEntry == null) ||
			(launchSetObjectEntry.getStatus() !=
				WorkflowConstants.STATUS_SCHEDULED)) {

			return BackgroundTaskResult.SUCCESS;
		}

		long userId = backgroundTask.getUserId();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(launchSetObjectEntry.getCompanyId());
		serviceContext.setUserId(userId);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			return _publish(launchSetObjectEntry, serviceContext, userId);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return null;
	}

	@Activate
	protected void activate() {
		_launchSetErrorHelper = new LaunchSetErrorHelper(
			_launchEntryManagerRegistry, _objectDefinitionLocalService,
			_objectEntryLocalService, _objectRelationshipLocalService,
			_userLocalService);
	}

	private BackgroundTaskResult _publish(
			ObjectEntry launchSetObjectEntry, ServiceContext serviceContext,
			long userId)
		throws PortalException {

		try {
			Group group = _groupLocalService.getGroup(
				launchSetObjectEntry.getCompanyId(), GroupConstants.GUEST);

			serviceContext.setScopeGroupId(group.getGroupId());

			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(
					_userLocalService.getUser(userId)));
			PrincipalThreadLocal.setName(userId);
		}
		catch (PortalException portalException) {
			try {
				_launchSetErrorHelper.markPublishFailed(
					List.of(
						_launchSetErrorHelper.getLaunchSetErrorValues(
							null, portalException)),
					launchSetObjectEntry, userId);
			}
			catch (Throwable throwable) {
				portalException.addSuppressed(throwable);
			}

			_log.error(
				"Unable to publish launch set " +
					launchSetObjectEntry.getObjectEntryId(),
				portalException);

			return new BackgroundTaskResult(
				BackgroundTaskConstants.STATUS_FAILED,
				portalException.getMessage());
		}

		try {
			_launchSetPublisher.publish(
				launchSetObjectEntry.getObjectEntryId());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to publish launch set " +
					launchSetObjectEntry.getObjectEntryId(),
				portalException);

			return new BackgroundTaskResult(
				BackgroundTaskConstants.STATUS_FAILED,
				portalException.getMessage());
		}

		return BackgroundTaskResult.SUCCESS;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchSetScheduledPublishBackgroundTaskExecutor.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	private LaunchSetErrorHelper _launchSetErrorHelper;

	@Reference
	private LaunchSetPublisher _launchSetPublisher;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private UserLocalService _userLocalService;

}