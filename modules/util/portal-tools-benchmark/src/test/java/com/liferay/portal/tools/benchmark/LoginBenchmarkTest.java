/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.benchmark;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class LoginBenchmarkTest extends BaseBenchmarkTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testLogin() throws Exception {
		StringBundler sb = new StringBundler(5);

		sb.append("select hostname, emailAddress from Company as company, ");
		sb.append("VirtualHost as virtualHost, User_ as user where ");
		sb.append(getExcludedCompaniesSQL());
		sb.append("user.type_ = 1 and user.companyId = company.companyId and ");
		sb.append("virtualHost.companyId = company.companyId;");

		runBenchmarkTest(
			sb.toString(),
			resultSet -> new String[] {
				resultSet.getString("hostname"),
				resultSet.getString("emailAddress")
			},
			testData -> new LoginBenchmarkTask(
				testData[0], 8080, testData[1], getUserPassword()));
	}

}