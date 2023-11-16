/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.quartz.internal.upgrade.v2_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.ObjectInputStream;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDataMap;

/**
 * @author Kevin Lee
 */
public class QuartzUpgradeProcess extends UpgradeProcess {

	public QuartzUpgradeProcess(
		CompanyLocalService companyLocalService, JSONFactory jsonFactory) {

		_companyLocalService = companyLocalService;
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Map<String, Long> jobCompanyIds = new HashMap<>();

		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select job_name, job_data from QUARTZ_JOB_DETAILS where " +
					"job_name not like '%@%'");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				_findJobCompanyId(
					jobCompanyIds, resultSet.getString("job_name"),
					_deserializeJobData(resultSet.getBlob("job_data")));
			}
		}

		if (jobCompanyIds.isEmpty()) {
			return;
		}

		_updateJobNames(
			"job_name", jobCompanyIds,
			new String[] {
				"QUARTZ_FIRED_TRIGGERS", "QUARTZ_JOB_DETAILS", "QUARTZ_TRIGGERS"
			});

		_updateJobNames(
			"trigger_name", jobCompanyIds,
			new String[] {
				"QUARTZ_BLOB_TRIGGERS", "QUARTZ_CRON_TRIGGERS",
				"QUARTZ_FIRED_TRIGGERS", "QUARTZ_SIMPLE_TRIGGERS",
				"QUARTZ_SIMPROP_TRIGGERS", "QUARTZ_TRIGGERS"
			});
	}

	private boolean _containsColumnId(
			String tableName, String columnId, long columnValue)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select companyId from ", tableName, " where ", columnId,
					" = ", columnValue));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			return resultSet.next();
		}
	}

	private JobDataMap _deserializeJobData(Blob blob) throws Exception {
		ObjectInputStream objectInputStream = new ObjectInputStream(
			blob.getBinaryStream());

		return (JobDataMap)objectInputStream.readObject();
	}

	private void _findJobCompanyId(
			Map<String, Long> jobCompanyIds, String jobName,
			JobDataMap jobDataMap)
		throws Exception {

		Message message = (Message)_jsonFactory.deserialize(
			jobDataMap.getString(SchedulerEngine.MESSAGE));

		if (message.contains("companyId")) {
			jobCompanyIds.putIfAbsent(jobName, message.getLong("companyId"));

			return;
		}

		String destinationName = jobDataMap.getString(
			SchedulerEngine.DESTINATION_NAME);

		_companyLocalService.forEachCompanyId(
			companyId -> {
				if (jobCompanyIds.containsKey(jobName)) {
					return;
				}

				if (destinationName.equals(_CT_COLLECTION_SCHEDULED_PUBLISH)) {
					long ctCollectionId = message.getLong("ctCollectionId");

					if (_containsColumnId(
							"CTCollection", "ctCollectionId", ctCollectionId)) {

						jobCompanyIds.put(jobName, companyId);
					}
				}
				else if (destinationName.equals(
							_DISPATCH_EXECUTOR_DESTINATION_NAME)) {

					JSONObject jsonObject = _jsonFactory.createJSONObject(
						(String)message.getPayload());

					long dispatchTriggerId = jsonObject.getLong(
						"dispatchTriggerId");

					if (_containsColumnId(
							"DispatchTrigger", "dispatchTriggerId",
							dispatchTriggerId)) {

						jobCompanyIds.put(jobName, companyId);
					}
				}
				else if (destinationName.equals(
							DestinationNames.LAYOUTS_LOCAL_PUBLISHER) ||
						 destinationName.equals(
							 DestinationNames.LAYOUTS_REMOTE_PUBLISHER)) {

					long exportImportConfigurationId = GetterUtil.getLong(
						message.getPayload());

					if (_containsColumnId(
							"ExportImportConfiguration",
							"exportImportConfigurationId",
							exportImportConfigurationId)) {

						jobCompanyIds.put(jobName, companyId);
					}
				}
			});

		jobCompanyIds.putIfAbsent(jobName, PortalUtil.getDefaultCompanyId());
	}

	private void _updateJobNames(
			String columnName, Map<String, Long> jobCompanyIds,
			String[] tableNames)
		throws Exception {

		for (String tableName : tableNames) {
			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						StringBundler.concat(
							"update ", tableName, " set ", columnName,
							" = ? where ", columnName, " = ?"))) {

				for (Map.Entry<String, Long> entry : jobCompanyIds.entrySet()) {
					preparedStatement.setString(
						1,
						SchedulerEngine.getPartitionedName(
							entry.getValue(), entry.getKey()));

					preparedStatement.setString(2, entry.getKey());

					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}
		}
	}

	private static final String _CT_COLLECTION_SCHEDULED_PUBLISH =
		"liferay/ct_collection_scheduled_publish";

	private static final String _DISPATCH_EXECUTOR_DESTINATION_NAME =
		"liferay/dispatch/executor";

	private final CompanyLocalService _companyLocalService;
	private final JSONFactory _jsonFactory;

}