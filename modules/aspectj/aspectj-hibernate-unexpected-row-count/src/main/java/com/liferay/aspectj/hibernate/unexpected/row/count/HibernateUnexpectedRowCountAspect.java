/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.aspectj.hibernate.unexpected.row.count;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.SuppressAjWarnings;

import org.hibernate.engine.jdbc.batch.internal.BatchImpl;
import org.hibernate.engine.jdbc.mutation.group.PreparedStatementGroup;

/**
 * @author Preston Crary
 */
@Aspect
@SuppressAjWarnings("adviceDidNotMatch")
public class HibernateUnexpectedRowCountAspect {

	@Before(
		"handler(java.lang.RuntimeException) &&" +
			"within(org.hibernate.engine.jdbc.batch.internal.BatchImpl) &&" +
				"args(runtimeException) && this(batchImpl)"
	)
	public void logUpdateSQL(
		BatchImpl batchImpl, RuntimeException runtimeException) {

		PreparedStatementGroup preparedStatementGroup =
			batchImpl.getStatementGroup();

		StringBuilder sb = new StringBuilder();

		preparedStatementGroup.forEachStatement(
			(sql, preparedStatementDetails) -> {
				if (sb.length() > 0) {
					sb.append(", ");
				}

				sb.append(preparedStatementDetails.getSqlString());
			});

		_log.error("Batch statements = " + sb, runtimeException);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HibernateUnexpectedRowCountAspect.class);

}