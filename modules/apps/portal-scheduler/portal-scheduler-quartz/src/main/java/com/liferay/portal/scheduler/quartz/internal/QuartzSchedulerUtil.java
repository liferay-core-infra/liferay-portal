/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.quartz.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import org.quartz.JobKey;

/**
 * @author Kevin Lee
 */
public class QuartzSchedulerUtil {

	public static JobKey getPartitionedJobKey(JobKey jobKey) {
		return new JobKey(
			getPartitionedName(jobKey.getName()), jobKey.getGroup());
	}

	public static JobKey getPartitionedJobKey(long companyId, JobKey jobKey) {
		return new JobKey(
			getPartitionedName(companyId, jobKey.getName()), jobKey.getGroup());
	}

	public static String getPartitionedName(long companyId, String name) {
		if (name.matches("^.+@\\d+$")) {
			return name;
		}

		return name.concat(StringPool.AT + companyId);
	}

	public static String getPartitionedName(String name) {
		return getPartitionedName(CompanyThreadLocal.getCompanyId(), name);
	}

}