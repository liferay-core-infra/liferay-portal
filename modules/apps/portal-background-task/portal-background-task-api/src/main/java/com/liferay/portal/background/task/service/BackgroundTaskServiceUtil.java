/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * Provides the remote service utility for BackgroundTask. This utility wraps
 * <code>com.liferay.portal.background.task.service.impl.BackgroundTaskServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see BackgroundTaskService
 * @generated
 */
public class BackgroundTaskServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.background.task.service.impl.BackgroundTaskServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static int getBackgroundTasksCount(
			long groupId, String taskExecutorClassName, boolean completed)
		throws PortalException {

		return getService().getBackgroundTasksCount(
			groupId, taskExecutorClassName, completed);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static BackgroundTaskService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<BackgroundTaskService> _serviceSnapshot =
		new Snapshot<>(
			BackgroundTaskServiceUtil.class, BackgroundTaskService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:304331164