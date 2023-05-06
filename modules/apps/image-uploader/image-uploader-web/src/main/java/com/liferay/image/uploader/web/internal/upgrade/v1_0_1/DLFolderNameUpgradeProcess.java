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
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
		long folderId = 0;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select folderId from DLFolder where name = ?")) {

			preparedStatement.setString(1, "java.lang.Class");

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				folderId = resultSet.getLong("folderId");
			}
		}

		DLFolder dlFolder = _dlFolderLocalService.getDLFolder(folderId);

		dlFolder.setName(UploadImageUtil.TEMP_IMAGE_FOLDER_NAME);

		_dlFolderLocalService.updateDLFolder(dlFolder);
	}

	private final DLFolderLocalService _dlFolderLocalService;

}