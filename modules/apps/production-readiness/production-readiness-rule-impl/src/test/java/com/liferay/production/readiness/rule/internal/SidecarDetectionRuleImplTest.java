/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class SidecarDetectionRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenConfigFileDoesNotExist()
		throws Exception {

		_assertCheckResult(false, null, Result.Status.FAIL, _MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenProductionModeDisabled()
		throws Exception {

		_assertCheckResult(
			true, "productionModeEnabled=B\"false\"", Result.Status.FAIL,
			_MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenProductionModeNotConfigured()
		throws Exception {

		_assertCheckResult(
			true, "remoteClusterConnectionId=sidecar", Result.Status.FAIL,
			_MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsPassWhenProductionModeEnabled()
		throws Exception {

		_assertCheckResult(
			true, "productionModeEnabled=B\"true\"", Result.Status.PASS,
			_MESSAGE_KEY_PASS);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _assertCheckResult(
			boolean createConfigFile, String content,
			Result.Status expectedStatus, String expectedMessageKey)
		throws Exception {

		if (createConfigFile) {
			_createConfigFile();
		}

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LIFERAY_HOME",
					temporaryFolder.getRoot(
					).getAbsolutePath());
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class)) {

			fileUtilMockedStatic.when(
				() -> FileUtil.read(Mockito.any(File.class))
			).thenReturn(
				content
			);

			_assertResult(expectedStatus, expectedMessageKey);
		}
	}

	private void _assertResult(
		Result.Status expectedStatus, String expectedMessageKey) {

		Collection<Result> results = _sidecarDetectionRuleImpl.check(0L);

		Assert.assertEquals(results.toString(), 1, results.size());

		Result result = results.iterator(
		).next();

		Assert.assertEquals(expectedStatus, result.getStatus());
		Assert.assertEquals(Result.Severity.HIGH, result.getSeverity());
		Assert.assertEquals(
			"search-engine-connectivity-validation", result.getCategory());
		Assert.assertEquals(expectedMessageKey, result.getMessageKey());
		Assert.assertNull(result.getCurrentValue());
		Assert.assertNull(result.getRecommendedValue());
		Assert.assertEquals(0, result.getMessageParameters().length);
	}

	private void _createConfigFile() throws Exception {
		File configFile = new File(
			temporaryFolder.getRoot(),
			"osgi/configs/com.liferay.portal.search.elasticsearch8." +
				"configuration.ElasticsearchConfiguration.config");

		File parentDir = configFile.getParentFile();

		parentDir.mkdirs();

		configFile.createNewFile();
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-sidecar-detection-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-sidecar-detection-pass";

	private final SidecarDetectionRuleImpl _sidecarDetectionRuleImpl =
		new SidecarDetectionRuleImpl();

}