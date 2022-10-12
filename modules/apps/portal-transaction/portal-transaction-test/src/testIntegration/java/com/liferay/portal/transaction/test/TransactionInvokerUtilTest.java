/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.transaction.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.persistence.ClassNamePersistence;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class TransactionInvokerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.REQUIRED);
		builder.setRollbackForClasses(Exception.class);

		_transactionConfig = builder.build();
	}

	@Test
	public void testCommit() throws Throwable {
		long classNameId = _counterLocalService.increment();
		String classNameValue = PwdGenerator.getPassword();

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				(Callable<Void>)() -> {
					ClassName className = _classNamePersistence.create(
						classNameId);

					className.setValue(classNameValue);

					_classNamePersistence.update(className);

					return null;
				});

			ClassName className = _classNameLocalService.fetchClassName(
				classNameId);

			Assert.assertNotNull(className);
			Assert.assertEquals(classNameValue, className.getClassName());
		}
		finally {
			_classNameLocalService.deleteClassName(classNameId);
		}
	}

	@Test
	public void testRollback() {
		long classNameId = _counterLocalService.increment();
		Exception exception1 = new Exception();

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				(Callable<Void>)() -> {
					ClassName className = _classNamePersistence.create(
						classNameId);

					className.setValue(PwdGenerator.getPassword());

					_classNamePersistence.update(className);

					throw exception1;
				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertSame(exception1, throwable);
			Assert.assertNull(
				_classNameLocalService.fetchClassName(classNameId));
		}
		finally {
			try {
				_classNameLocalService.deleteClassName(classNameId);
			}
			catch (Exception exception2) {
			}
		}
	}

	@Ignore
	@Test
	public void testTransactionRollbackKeepsFileInStore() throws Throwable {
		byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

		InputStream inputStream = new ByteArrayInputStream(bytes);

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK, 0, null, null,
			inputStream, bytes.length, null, null,
			ServiceContextTestUtil.getServiceContext());

		try {
			Assert.assertTrue(
				_fileSystemStore.hasFile(
					TestPropsValues.getCompanyId(),
					TestPropsValues.getGroupId(), dlFileEntry.getName(), null));

			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_dlFileEntryLocalService.deleteFileEntry(
						dlFileEntry.getFileEntryId());

					Assert.assertFalse(
						_fileSystemStore.hasFile(
							TestPropsValues.getCompanyId(),
							TestPropsValues.getGroupId(), dlFileEntry.getName(),
							null));

					throw new Exception();
				});
		}
		catch (Exception exception) {
			Assert.assertNotNull(
				_dlFileEntryLocalService.fetchDLFileEntry(
					dlFileEntry.getFileEntryId()));

			Assert.assertTrue(
				_fileSystemStore.hasFile(
					TestPropsValues.getCompanyId(),
					TestPropsValues.getGroupId(), dlFileEntry.getName(), null));
		}
		finally {
			if (dlFileEntry != null) {
				_dlFileEntryLocalService.deleteFileEntry(
					dlFileEntry.getFileEntryId());
			}
		}
	}

	@Inject(
		filter = "store.type=com.liferay.portal.store.file.system.FileSystemStore"
	)
	private static Store _fileSystemStore;

	private static TransactionConfig _transactionConfig;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private ClassNamePersistence _classNamePersistence;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

}