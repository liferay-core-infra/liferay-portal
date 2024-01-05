/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 */
public class ToolsUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetPackagePath() {
		Assert.assertEquals(
			"com.liferay.portal.tools",
			ToolsUtil.getPackagePath(
				"/path/com/liferay/portal/tools/File.java"));
		Assert.assertEquals(
			"com.liferay.portal.tools",
			ToolsUtil.getPackagePath("/com/liferay/portal/tools/File.java"));

		Assert.assertEquals(
			"com.liferay.portal.org",
			ToolsUtil.getPackagePath("/path/com/liferay/portal/org/File.java"));
		Assert.assertEquals(
			"com.liferay.portal.org",
			ToolsUtil.getPackagePath("/com/liferay/portal/org/File.java"));
	}

}