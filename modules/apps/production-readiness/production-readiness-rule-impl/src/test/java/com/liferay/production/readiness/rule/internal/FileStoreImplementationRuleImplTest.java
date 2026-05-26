/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Chi
 */
public class FileStoreImplementationRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenStoreIsDBStore() throws Exception {
		_assertCheckResult(
			"com.liferay.portal.store.db.DBStore", Result.Status.FAIL,
			_MESSAGE_KEY_FAIL, "AdvancedFileSystemStore or Cloud Store");
	}

	@Test
	public void testCheckReturnsFailWhenStoreIsFileSystemStore()
		throws Exception {

		_assertCheckResult(
			"com.liferay.portal.store.file.system.FileSystemStore",
			Result.Status.FAIL, _MESSAGE_KEY_FAIL,
			"AdvancedFileSystemStore or Cloud Store");
	}

	@Test
	public void testCheckReturnsPassWhenStoreIsAdvancedFileSystemStore()
		throws Exception {

		_assertCheckResult(
			"com.liferay.portal.store.file.system.AdvancedFileSystemStore",
			Result.Status.PASS, _MESSAGE_KEY_PASS, null);
	}

	@Test
	public void testCheckReturnsPassWhenStoreIsAzureStore() throws Exception {
		_assertCheckResult(
			"com.liferay.portal.store.azure.AzureStore", Result.Status.PASS,
			_MESSAGE_KEY_PASS, null);
	}

	@Test
	public void testCheckReturnsPassWhenStoreIsGCSStore() throws Exception {
		_assertCheckResult(
			"com.liferay.portal.store.gcs.GCSStore", Result.Status.PASS,
			_MESSAGE_KEY_PASS, null);
	}

	@Test
	public void testCheckReturnsPassWhenStoreIsIBMS3Store() throws Exception {
		_assertCheckResult(
			"com.liferay.portal.store.s3.IBMS3Store", Result.Status.PASS,
			_MESSAGE_KEY_PASS, null);
	}

	@Test
	public void testCheckReturnsPassWhenStoreIsS3Store() throws Exception {
		_assertCheckResult(
			"com.liferay.portal.store.s3.S3Store", Result.Status.PASS,
			_MESSAGE_KEY_PASS, null);
	}

	private void _assertCheckResult(
			String dlStoreImpl, Result.Status expectedStatus,
			String expectedMessageKey, String expectedRecommendedValue)
		throws Exception {

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "DL_STORE_IMPL", dlStoreImpl)) {

			Collection<Result> results = _fileStoreImplementationRuleImpl.check(
				0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(expectedStatus, result.getStatus());
			Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
			Assert.assertEquals(
				"portal-properties-configuration", result.getCategory());
			Assert.assertEquals(expectedMessageKey, result.getMessageKey());
			Assert.assertEquals(dlStoreImpl, result.getCurrentValue());
			Assert.assertEquals(
				expectedRecommendedValue, result.getRecommendedValue());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-file-store-implementation-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-file-store-implementation-pass";

	private final FileStoreImplementationRuleImpl
		_fileStoreImplementationRuleImpl =
			new FileStoreImplementationRuleImpl();

}