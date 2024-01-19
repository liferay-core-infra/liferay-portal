/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 */
public class PackagingUtilTest {

	@Test
	public void testGetPackagePath() {
		Assert.assertEquals(
			"com.liferay.portal.tools",
			PackagingUtil.getPackagePath(
				"/path/com/liferay/portal/tools/File.java"));
		Assert.assertEquals(
			"com.liferay.portal.tools",
			PackagingUtil.getPackagePath(
				"/com/liferay/portal/tools/File.java"));

		Assert.assertEquals(
			"com.liferay.portal.org",
			PackagingUtil.getPackagePath(
				"/path/com/liferay/portal/org/File.java"));
		Assert.assertEquals(
			"com.liferay.portal.org",
			PackagingUtil.getPackagePath("/com/liferay/portal/org/File.java"));
	}

}