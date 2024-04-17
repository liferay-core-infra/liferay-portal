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
			_PARENT_RUNTIME_JAVA_HOME_KEY, System.getProperty("java.home"));

		System.setProperty(
			_PARENT_ENVIRONMENT_JAVA_HOME_KEY, System.getenv("JAVA_HOME"));
	}

	@NewEnv.JVMArgsLine(
		"-D" + _PARENT_ENVIRONMENT_JAVA_HOME_KEY + "=${" +
			_PARENT_ENVIRONMENT_JAVA_HOME_KEY + "} -D" +
				_PARENT_RUNTIME_JAVA_HOME_KEY + "=${" +
					_PARENT_RUNTIME_JAVA_HOME_KEY + "}"
	)
	@Test
	public void testJavaHome() throws IOException {
		String parentJavaHome = System.getProperty(
			_PARENT_ENVIRONMENT_JAVA_HOME_KEY);

		String parentRunTimeJavaHome = _getJavaHomePath(
			System.getProperty(_PARENT_RUNTIME_JAVA_HOME_KEY));

		String currentJavaHome = System.getenv("JAVA_HOME");

		String currentRunTimeJavaHome = _getJavaHomePath(
			System.getProperty("java.home"));

		Assert.assertEquals(parentRunTimeJavaHome, parentJavaHome);

		Assert.assertEquals(parentRunTimeJavaHome, currentJavaHome);

		Assert.assertEquals(parentRunTimeJavaHome, currentRunTimeJavaHome);
	}

	private String _getJavaHomePath(String path) throws IOException {
		int jrePos = path.lastIndexOf("/jre");

		if (jrePos == -1) {
			jrePos = path.lastIndexOf("\\jre");
		}

		if (jrePos != -1) {
			path = path.substring(0, jrePos);
		}

		File file = new File(path);

		return file.getCanonicalPath();
	}

	private static final String _PARENT_ENVIRONMENT_JAVA_HOME_KEY =
		"_PARENT_ENVIRONMENT_JAVA_HOME_KEY_";

	private static final String _PARENT_RUNTIME_JAVA_HOME_KEY =
		"_PARENT_RUNTIME_JAVA_HOME_KEY_";

}