/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class UpgradeExternalReferenceCodeColumnSize extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		for (String[] tableColumnName : _getTableColumnNames()) {
			try {
				alterColumnType(
					tableColumnName[0], tableColumnName[1],
					"VARCHAR(" + _MAX_LENGTH + ") null");
			}
			catch (SQLException sqlException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to widen column \"", tableColumnName[1],
							"\" in table \"", tableColumnName[0], "\""),
						sqlException);
				}
			}
		}
	}

	private List<String[]> _getTableColumnNames() throws Exception {
		List<String[]> tableColumnNames = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DBInspector dbInspector = new DBInspector(connection);

		String catalog = dbInspector.getCatalog();
		String schema = dbInspector.getSchema();

		try (LoggingTimer loggingTimer = new LoggingTimer();
			ResultSet tableResultSet = databaseMetaData.getTables(
				catalog, schema, null, new String[] {"TABLE"})) {

			while (tableResultSet.next()) {
				String tableName = tableResultSet.getString("TABLE_NAME");

				try (ResultSet columnResultSet = databaseMetaData.getColumns(
						catalog, schema, tableName, null)) {

					while (columnResultSet.next()) {
						String columnName = columnResultSet.getString(
							"COLUMN_NAME");

						if (!_isExternalReferenceCode(columnName)) {
							continue;
						}

						int dataType = columnResultSet.getInt("DATA_TYPE");

						if (((dataType != Types.NVARCHAR) &&
							 (dataType != Types.VARCHAR)) ||
							(columnResultSet.getInt("COLUMN_SIZE") >=
								_MAX_LENGTH)) {

							continue;
						}

						tableColumnNames.add(
							new String[] {tableName, columnName});
					}
				}
			}
		}

		return tableColumnNames;
	}

	/**
	 * @see com.liferay.portal.tools.service.builder.ServiceBuilder#_isExternalReferenceCode(
	 *      String)
	 */
	private boolean _isExternalReferenceCode(String columnName) {
		if (columnName == null) {
			return false;
		}

		if (columnName.equals("externalReferenceCode") ||
			columnName.endsWith("ERC") ||
			columnName.endsWith("ExternalReferenceCode")) {

			return true;
		}

		return false;
	}

	private static final int _MAX_LENGTH = 500;

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeExternalReferenceCodeColumnSize.class);

}