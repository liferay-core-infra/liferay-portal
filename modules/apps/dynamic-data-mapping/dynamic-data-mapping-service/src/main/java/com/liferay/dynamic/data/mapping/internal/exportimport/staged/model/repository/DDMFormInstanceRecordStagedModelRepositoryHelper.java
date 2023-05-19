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

package com.liferay.dynamic.data.mapping.internal.exportimport.staged.model.repository;

import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceRecordVersionException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(service = DDMFormInstanceRecordStagedModelRepositoryHelper.class)
public class DDMFormInstanceRecordStagedModelRepositoryHelper {

	public DDMFormInstanceRecord addStagedModel(
			PortletDataContext portletDataContext,
			DDMFormInstanceRecord ddmFormInstanceRecord,
			DDMFormValues ddmFormValues)
		throws PortalException {

		long userId = portletDataContext.getUserId(
			ddmFormInstanceRecord.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			ddmFormInstanceRecord);

		serviceContext.setAttribute("validateDDMFormValues", Boolean.FALSE);

		if (portletDataContext.isDataStrategyMirror()) {
			serviceContext.setUuid(ddmFormInstanceRecord.getUuid());
		}

		DDMFormInstanceRecord importedDDMFormInstanceRecord =
			_ddmFormInstanceRecordLocalService.addFormInstanceRecord(
				userId, ddmFormInstanceRecord.getGroupId(),
				ddmFormInstanceRecord.getFormInstanceId(), ddmFormValues,
				serviceContext);

		_updateVersions(
			importedDDMFormInstanceRecord, ddmFormInstanceRecord.getVersion());

		return importedDDMFormInstanceRecord;
	}

	public DDMFormInstanceRecord updateStagedModel(
			PortletDataContext portletDataContext,
			DDMFormInstanceRecord ddmFormInstanceRecord,
			DDMFormValues ddmFormValues)
		throws PortalException {

		long userId = portletDataContext.getUserId(
			ddmFormInstanceRecord.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			ddmFormInstanceRecord);

		serviceContext.setAttribute("validateDDMFormValues", Boolean.FALSE);

		DDMFormInstanceRecord importedDDMFormInstanceRecord =
			_ddmFormInstanceRecordLocalService.updateFormInstanceRecord(
				userId, ddmFormInstanceRecord.getFormInstanceRecordId(), false,
				ddmFormValues, serviceContext);

		_updateVersions(
			importedDDMFormInstanceRecord, ddmFormInstanceRecord.getVersion());

		return importedDDMFormInstanceRecord;
	}

	private void _updateVersions(
			DDMFormInstanceRecord importedDDMFormInstanceRecord, String version)
		throws PortalException {

		if (Objects.equals(
				importedDDMFormInstanceRecord.getVersion(), version)) {

			return;
		}

		try {
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
				_ddmFormInstanceRecordVersionLocalService.
					getFormInstanceRecordVersion(
						importedDDMFormInstanceRecord.getFormInstanceRecordId(),
						version);

			_ddmFormInstanceRecordVersionLocalService.
				deleteDDMFormInstanceRecordVersion(
					ddmFormInstanceRecordVersion);
		}
		catch (NoSuchFormInstanceRecordVersionException
					noSuchFormInstanceRecordVersionException) {

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFormInstanceRecordVersionException);
			}
		}

		DDMFormInstanceRecordVersion importedDDMFormInstanceRecordVersion =
			importedDDMFormInstanceRecord.getFormInstanceRecordVersion();

		importedDDMFormInstanceRecordVersion.setVersion(version);

		_ddmFormInstanceRecordVersionLocalService.
			updateDDMFormInstanceRecordVersion(
				importedDDMFormInstanceRecordVersion);

		importedDDMFormInstanceRecord.setVersion(version);

		_ddmFormInstanceRecordLocalService.updateDDMFormInstanceRecord(
			importedDDMFormInstanceRecord);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstanceRecordStagedModelRepositoryHelper.class);

	@Reference
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Reference
	private DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;

}