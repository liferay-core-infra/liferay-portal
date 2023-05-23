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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

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
		List<Long> userIds = _getUserIds();

		if (ListUtil.isEmpty(userIds)) {
			return;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select folderId from DLFolder where name = ?")) {

			preparedStatement.setString(1, _FOLDER_NAME);

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				DLFolder dlFolder = _dlFolderLocalService.getDLFolder(
					resultSet.getLong("folderId"));

				DLFolder parentFolder = dlFolder.getParentFolder();

				if (!userIds.contains(Long.valueOf(parentFolder.getName()))) {
					continue;
				}

				List<DLFolder> ancestors = dlFolder.getAncestors();

				DLFolder rootFolder = ancestors.get(ancestors.size() - 1);

				if (!rootFolder.isInHiddenFolder()) {
					continue;
				}

				dlFolder.setName(UploadImageUtil.TEMP_IMAGE_FOLDER_NAME);

				_dlFolderLocalService.updateDLFolder(dlFolder);
			}
		}
		catch (SQLException sqlException) {
			_log.error(sqlException);
		}
	}

	private List<Long> _getUserIds() {
		List<Long> userIds = new ArrayList<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select userId from User_ where portraitId != 0")) {

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				userIds.add(resultSet.getLong("userId"));
			}

			return userIds;
		}
		catch (SQLException sqlException) {
			_log.error(sqlException);

			return null;
		}
	}

	private static final String _FOLDER_NAME = "java.lang.Class";

	private static final Log _log = LogFactoryUtil.getLog(
		DLFolderNameUpgradeProcess.class);

	private final DLFolderLocalService _dlFolderLocalService;

}