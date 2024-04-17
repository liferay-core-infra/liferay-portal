/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.test.rule.NewEnv;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Julius Lee
 */
@NewEnv(type = NewEnv.Type.JVM)
public class NewEnvJVMTestRuleTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		System.setProperty(
			_PARENT_JAVA_HOME_KEY, System.getProperty("java.home"));
	}

	@NewEnv.JVMArgsLine(
		"-D" + _PARENT_JAVA_HOME_KEY + "=${" + _PARENT_JAVA_HOME_KEY + "}"
	)
	@Test
	public void testJavaHome() throws IOException {
		Assert.assertEquals(
			_getJavaHomePath(System.getProperty(_PARENT_JAVA_HOME_KEY)),
			_getJavaHomePath(System.getProperty("java.home")));
	}

	private String _getJavaHomePath(String path) throws IOException {
		File file = new File(path);

		path = file.getCanonicalPath();

		int jrePos = path.lastIndexOf("/jre");

		if (jrePos == -1) {
			jrePos = path.lastIndexOf("\\jre");
		}

		if (jrePos != -1) {
			path = path.substring(0, jrePos);
		}

		return path;
	}

	private static final String _PARENT_JAVA_HOME_KEY =
		"_PARENT_RUNTIME_JAVA_HOME_KEY_";

}