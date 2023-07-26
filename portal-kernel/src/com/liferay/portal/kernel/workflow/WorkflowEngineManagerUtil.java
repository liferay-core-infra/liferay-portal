/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class WorkflowEngineManagerUtil {

	public static boolean isDeployed() {
		if (_serviceTrackerList.size() == 0) {
			return false;
		}

		return true;
	}

	private static final ServiceTrackerList<WorkflowEngineManager>
		_serviceTrackerList = ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(), WorkflowEngineManager.class);

}