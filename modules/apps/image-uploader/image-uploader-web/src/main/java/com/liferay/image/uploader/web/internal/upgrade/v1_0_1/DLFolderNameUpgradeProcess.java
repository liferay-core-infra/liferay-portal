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

import com.liferay.image.uploader.web.internal.util.UploadImageUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;

/**
 * @author Lily Chi
 */
public class DLFolderNameUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update DLFolder set name = ? where name = ?")) {

			preparedStatement.setString(
				1, UploadImageUtil.TEMP_IMAGE_FOLDER_NAME);
			preparedStatement.setString(2, "java.lang.Class");

			preparedStatement.executeUpdate();
		}
	}

}