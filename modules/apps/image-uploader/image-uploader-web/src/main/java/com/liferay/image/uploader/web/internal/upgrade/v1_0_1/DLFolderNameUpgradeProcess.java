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

package com.liferay.image.uploader.web.internal.upgrade.v1_0_1;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.image.uploader.web.internal.util.UploadImageUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.TempFileEntryUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author Lily Chi
 */
public class DLFolderNameUpgradeProcess extends UpgradeProcess {

	public DLFolderNameUpgradeProcess(
		DLFolderLocalService dlFolderLocalService) {

		_dlFolderLocalService = dlFolderLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		String sql = "select folderId from DLFolder where name = ?";

		List<Object> dlFolders = _getDataList(
			sql, Arrays.asList("java.lang.Class"),
			(Long folderId) -> {
				try {
					return _dlFolderLocalService.getDLFolder(folderId);
				}
				catch (PortalException portalException) {
					_log.error(portalException);

					return null;
				}
			});

		if (dlFolders == null) {
			return;
		}

		List<Object> rootDLFolderIds = _getDataList(
			sql + "and parentFolderId = ?",
			Arrays.asList(TempFileEntryUtil.class.getName(), String.valueOf(0)),
			(Long folderId) -> folderId);

		if (rootDLFolderIds == null) {
			return;
		}

		for (Object rootDLFolderIdObject : rootDLFolderIds) {
			for (Object dlFolderObject : dlFolders) {
				DLFolder dlFolder = (DLFolder)dlFolderObject;

				String treePath = dlFolder.getTreePath();

				if (treePath.contains(String.valueOf(rootDLFolderIdObject))) {
					dlFolder.setName(UploadImageUtil.TEMP_IMAGE_FOLDER_NAME);

					_dlFolderLocalService.updateDLFolder(dlFolder);
				}
			}
		}
	}

	private List<Object> _getDataList(
		String sql, List<String> values, Function<Long, Object> function) {

		List<Object> dataList = new ArrayList<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			for (int i = 0; i < values.size(); i++) {
				preparedStatement.setString(i + 1, values.get(i));
			}

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				dataList.add(function.apply(resultSet.getLong("folderId")));
			}

			return dataList;
		}
		catch (SQLException sqlException) {
			_log.error(sqlException);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFolderNameUpgradeProcess.class);

	private final DLFolderLocalService _dlFolderLocalService;

}