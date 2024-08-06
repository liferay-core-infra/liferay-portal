/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.extender.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class LanguageResourcesExtenderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			LanguageResourcesExtenderTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Test
	public void testAddingBundle() throws Exception {
		Class<? extends LanguageResourcesExtenderTest> clazz = getClass();

		Bundle bundle = _bundleContext.installBundle(
			RandomTestUtil.randomString(),
			clazz.getResourceAsStream(
				"dependencies/language-client-extension-20240821163047.wab." +
					"zip"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.language.extender.internal." +
					"LanguageClientExtension",
				LoggerTestUtil.ERROR)) {

			bundle.start();

			Thread.sleep(2000);

			Assert.assertEquals(
				"My English Value",
				LanguageUtil.get(LocaleUtil.US, "my-english-key"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 3, logEntries.size());

			for (LogEntry logEntry : logEntries) {
				String message = logEntry.getMessage();

				boolean matches = false;

				for (String expectedMessage : _EXPECTED_LOG_MESSAGES) {
					if (message.matches(expectedMessage)) {
						matches = true;

						break;
					}
				}

				if (!matches) {
					Assert.fail(
						StringBundler.concat(
							"Log message \"", message,
							"\" must match one of the following: ",
							Arrays.toString(_EXPECTED_LOG_MESSAGES)));
				}
			}
		}
		finally {
			bundle.uninstall();
		}
	}

	private static final String[] _EXPECTED_LOG_MESSAGES = {
		"Unable to import \"Language_pt_BR.properties\": Key must not be null",
		"Unable to import \"Language_pt_BR.properties\": Value must not be " +
			"null",
		"Unable to import \"Language_yy_ZZ.properties\": Language ID " +
			"\"yy_ZZ\" is not one of the available language IDs: \\[(.*)\\]"
	};

	private BundleContext _bundleContext;

}