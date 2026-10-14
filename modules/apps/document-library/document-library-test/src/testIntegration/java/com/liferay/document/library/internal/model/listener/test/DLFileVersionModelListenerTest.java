/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shakir Shamim
 */
@RunWith(Arquillian.class)
public class DLFileVersionModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		_testOnAfterRemove();
		_testOnAfterRemoveWithRollback();
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		_testOnAfterUpdate();
		_testOnAfterUpdateWithRollback();
	}

	private DLFileVersion _getDLFileVersion(FileEntry fileEntry)
		throws Exception {

		return _dlFileVersionLocalService.getFileVersion(
			fileEntry.getFileEntryId(), fileEntry.getVersion());
	}

	private boolean _hasFile(FileEntry fileEntry, String storeFileName)
		throws Exception {

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		return DLStoreUtil.hasFile(
			dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
			dlFileEntry.getName(), storeFileName);
	}

	private void _invokeWithRollback(UnsafeRunnable<Exception> unsafeRunnable) {
		Assert.assertThrows(
			PortalException.class,
			() -> TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					unsafeRunnable.run();

					throw new PortalException();
				}));
	}

	private void _testOnAfterRemove() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		_dlAppLocalService.updateFileEntry(
			TestPropsValues.getUserId(), fileEntry.getFileEntryId(),
			fileEntry.getFileName(), ContentTypes.TEXT_PLAIN,
			fileEntry.getTitle(), null, StringPool.BLANK, StringPool.BLANK,
			DLVersionNumberIncrease.MAJOR, RandomTestUtil.randomBytes(), null,
			null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		DLFileVersion dlFileVersion = _dlFileVersionLocalService.getFileVersion(
			fileEntry.getFileEntryId(), "1.0");

		_dlFileVersionLocalService.deleteDLFileVersion(dlFileVersion);

		Assert.assertFalse(
			_hasFile(fileEntry, dlFileVersion.getStoreFileName()));
	}

	private void _testOnAfterRemoveWithRollback() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		DLFileVersion dlFileVersion = _getDLFileVersion(fileEntry);

		_invokeWithRollback(
			() -> _dlFileVersionLocalService.deleteDLFileVersion(
				dlFileVersion));

		Assert.assertTrue(
			_hasFile(fileEntry, dlFileVersion.getStoreFileName()));
	}

	private void _testOnAfterUpdate() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(
			DLAppTestUtil.addFolder(_group.getGroupId()));

		DLFileVersion dlFileVersion = _getDLFileVersion(fileEntry);

		String storeFileName = dlFileVersion.getStoreFileName();

		dlFileVersion.setStoreUUID(PortalUUIDUtil.generate());

		_dlFileVersionLocalService.updateDLFileVersion(dlFileVersion);

		Assert.assertFalse(_hasFile(fileEntry, storeFileName));
	}

	private void _testOnAfterUpdateWithRollback() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		DLFileVersion dlFileVersion = _getDLFileVersion(fileEntry);

		String storeFileName = dlFileVersion.getStoreFileName();

		_invokeWithRollback(
			() -> {
				dlFileVersion.setStoreUUID(PortalUUIDUtil.generate());

				_dlFileVersionLocalService.updateDLFileVersion(dlFileVersion);
			});

		Assert.assertTrue(_hasFile(fileEntry, storeFileName));
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@DeleteAfterTestRun
	private Group _group;

}