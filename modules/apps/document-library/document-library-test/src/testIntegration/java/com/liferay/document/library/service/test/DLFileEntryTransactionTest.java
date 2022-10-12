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

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class DLFileEntryTransactionTest {

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

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();
	}

	@Ignore
	@Test
	public void testTransactionRollbackAddFileInStore() throws Throwable {
		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_dlFileEntry = _dlFileEntryLocalService.addFileEntry(
						null, TestPropsValues.getUserId(), _group.getGroupId(),
						_group.getGroupId(),
						DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
						RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString(), StringPool.BLANK, 0,
						null, null,
						new ByteArrayInputStream(RandomTestUtil.randomBytes()),
						0, null, null,
						ServiceContextTestUtil.getServiceContext());

					Assert.assertTrue(
						_fileSystemStore.hasFile(
							TestPropsValues.getCompanyId(), _group.getGroupId(),
							_dlFileEntry.getName(), null));

					throw new Exception();
				});
		}
		catch (Exception exception) {
			Assert.assertNull(
				_dlFileEntryLocalService.fetchDLFileEntry(
					_dlFileEntry.getFileEntryId()));

			Assert.assertFalse(
				_fileSystemStore.hasFile(
					TestPropsValues.getCompanyId(), _group.getGroupId(),
					_dlFileEntry.getName(), null));
		}
	}

	@Ignore
	@Test
	public void testTransactionRollbackDeleteFileInStore() throws Throwable {
		_dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK, 0, null, null,
			new ByteArrayInputStream(RandomTestUtil.randomBytes()), 0, null,
			null, ServiceContextTestUtil.getServiceContext());

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_dlFileEntryLocalService.deleteFileEntry(
						_dlFileEntry.getFileEntryId());

					Assert.assertFalse(
						_fileSystemStore.hasFile(
							TestPropsValues.getCompanyId(), _group.getGroupId(),
							_dlFileEntry.getName(), null));

					throw new Exception();
				});
		}
		catch (Exception exception) {
			Assert.assertNotNull(
				_dlFileEntryLocalService.fetchDLFileEntry(
					_dlFileEntry.getFileEntryId()));

			Assert.assertTrue(
				_fileSystemStore.hasFile(
					TestPropsValues.getCompanyId(), _group.getGroupId(),
					_dlFileEntry.getName(), null));
		}
	}

	@Ignore
	@Test
	public void testTransactionRollbackUpdateFileInStore() throws Throwable {
		byte[] originalBytes = RandomTestUtil.randomBytes();
		byte[] updatedBytes = RandomTestUtil.randomBytes();

		_dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK, 0, null, null,
			new ByteArrayInputStream(originalBytes), 0, null, null,
			ServiceContextTestUtil.getServiceContext());

		try {
			InputStream fileStream =
				_fileSystemStore.getFileAsStream(
					TestPropsValues.getCompanyId(), _group.getGroupId(),
					_dlFileEntry.getName(), null);

			Assert.assertArrayEquals(StreamUtil.toByteArray(fileStream),
				originalBytes);

			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_dlFileEntryLocalService.updateFileEntry(
						_dlFileEntry.getUserId(), _dlFileEntry.getFileEntryId(),
						_dlFileEntry.getFileName(), _dlFileEntry.getMimeType(),
						_dlFileEntry.getTitle(), null,
						_dlFileEntry.getDescription(), StringPool.BLANK,
						DLVersionNumberIncrease.fromMajorVersion(false),
						_dlFileEntry.getFileEntryTypeId(), new HashMap<>(),
						null, new ByteArrayInputStream(updatedBytes), 0, null, null,
						ServiceContextTestUtil.getServiceContext());

					InputStream updatedFileStream =
						_fileSystemStore.getFileAsStream(
							TestPropsValues.getCompanyId(), _group.getGroupId(),
							_dlFileEntry.getName(), null);

					Assert.assertArrayEquals(StreamUtil.toByteArray(updatedFileStream),
						updatedBytes);

					throw new Exception();
				});
		}
		catch (Exception exception) {
			InputStream fileStream =
				_fileSystemStore.getFileAsStream(
					TestPropsValues.getCompanyId(), _group.getGroupId(),
					_dlFileEntry.getName(), null);

			Assert.assertArrayEquals(StreamUtil.toByteArray(fileStream),
				originalBytes);
		}
	}

	@Inject(
		filter = "store.type=com.liferay.portal.store.file.system.FileSystemStore"
	)
	private static Store _fileSystemStore;

	private static TransactionConfig _transactionConfig;

	@DeleteAfterTestRun
	private DLFileEntry _dlFileEntry;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

}