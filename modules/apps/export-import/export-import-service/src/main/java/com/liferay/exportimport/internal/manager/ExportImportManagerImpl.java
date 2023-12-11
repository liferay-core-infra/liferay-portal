/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.manager;

import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.manager.ExportImportManager;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.service.ExportImportService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.File;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Santos
 */
@Component(service = ExportImportManager.class)
@CTAware
public class ExportImportManagerImpl implements ExportImportManager {

	@Override
	public File exportLayoutsAsFile(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		if (checkPermission) {
			return _exportImportService.exportLayoutsAsFile(
				exportImportConfiguration);
		}

		return _exportImportLocalService.exportLayoutsAsFile(
			exportImportConfiguration);
	}

	@Override
	public File exportLayoutsAsFile(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		return exportLayoutsAsFile(false, exportImportConfiguration);
	}

	@Override
	public long exportLayoutsAsFileInBackground(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		return _exportImportService.exportLayoutsAsFileInBackground(
			exportImportConfiguration);
	}

	@Override
	public File exportPortletInfoAsFile(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		return _exportImportLocalService.exportPortletInfoAsFile(
			exportImportConfiguration);
	}

	@Override
	public long exportPortletInfoAsFileInBackground(
			boolean checkPermission, long userId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		if (checkPermission) {
			return _exportImportService.exportPortletInfoAsFileInBackground(
				exportImportConfiguration);
		}

		return _exportImportLocalService.exportPortletInfoAsFileInBackground(
			userId, exportImportConfiguration);
	}

	@Override
	public long exportPortletInfoAsFileInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		return exportPortletInfoAsFileInBackground(
			false, userId, exportImportConfiguration);
	}

	@Override
	public long exportPortletInfoAsFileInBackground(
			long userId, long exportImportConfigurationId)
		throws PortalException {

		return _exportImportLocalService.exportPortletInfoAsFileInBackground(
			userId, exportImportConfigurationId);
	}

	@Override
	public void importLayouts(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		if (checkPermission) {
			_exportImportService.importLayouts(exportImportConfiguration, file);
		}

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, file);
	}

	@Override
	public void importLayouts(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		if (checkPermission) {
			_exportImportService.importLayouts(
				exportImportConfiguration, inputStream);
		}

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, inputStream);
	}

	@Override
	public void importLayouts(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		importLayouts(false, exportImportConfiguration, file);
	}

	@Override
	public void importLayouts(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		importLayouts(false, exportImportConfiguration, inputStream);
	}

	@Override
	public void importLayoutsDataDeletions(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		_exportImportLocalService.importLayoutsDataDeletions(
			exportImportConfiguration, file);
	}

	@Override
	public long importLayoutSetPrototypeInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException {

		return _exportImportLocalService.importLayoutSetPrototypeInBackground(
			userId, exportImportConfiguration, file);
	}

	@Override
	public long importLayoutsInBackground(
			boolean checkPermission, long userId,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		if (checkPermission) {
			return _exportImportService.importLayoutsInBackground(
				exportImportConfiguration, inputStream);
		}

		return _exportImportLocalService.importLayoutsInBackground(
			userId, exportImportConfiguration, inputStream);
	}

	@Override
	public long importLayoutsInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException {

		return _exportImportLocalService.importLayoutsInBackground(
			userId, exportImportConfiguration, file);
	}

	@Override
	public long importLayoutsInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		return importLayoutsInBackground(
			false, userId, exportImportConfiguration, inputStream);
	}

	@Override
	public long importLayoutsInBackground(
			long userId, long exportImportConfigurationId, File file)
		throws PortalException {

		return _exportImportLocalService.importLayoutsInBackground(
			userId, exportImportConfigurationId, file);
	}

	@Override
	public long importLayoutsInBackground(
			long userId, long exportImportConfigurationId,
			InputStream inputStream)
		throws PortalException {

		return _exportImportLocalService.importLayoutsInBackground(
			userId, exportImportConfigurationId, inputStream);
	}

	@Override
	public void importPortletDataDeletions(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		_exportImportLocalService.importPortletDataDeletions(
			exportImportConfiguration, file);
	}

	@Override
	public void importPortletInfo(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		_exportImportLocalService.importPortletInfo(
			exportImportConfiguration, file);
	}

	@Override
	public void importPortletInfo(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		_exportImportLocalService.importPortletInfo(
			exportImportConfiguration, inputStream);
	}

	@Override
	public long importPortletInfoInBackground(
			boolean checkPermission, long userId,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		if (checkPermission) {
			return _exportImportService.importPortletInfoInBackground(
				exportImportConfiguration, inputStream);
		}

		return _exportImportLocalService.importPortletInfoInBackground(
			userId, exportImportConfiguration, inputStream);
	}

	@Override
	public long importPortletInfoInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException {

		return _exportImportLocalService.importPortletInfoInBackground(
			userId, exportImportConfiguration, file);
	}

	@Override
	public long importPortletInfoInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		return importPortletInfoInBackground(
			false, userId, exportImportConfiguration, inputStream);
	}

	@Override
	public long importPortletInfoInBackground(
			long userId, long exportImportConfigurationId, File file)
		throws PortalException {

		return _exportImportLocalService.importPortletInfoInBackground(
			userId, exportImportConfigurationId, file);
	}

	@Override
	public long importPortletInfoInBackground(
			long userId, long exportImportConfigurationId,
			InputStream inputStream)
		throws PortalException {

		return _exportImportLocalService.importPortletInfoInBackground(
			userId, exportImportConfigurationId, inputStream);
	}

	@Override
	public long mergeLayoutSetPrototypeInBackground(
			long userId, long groupId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		return _exportImportLocalService.mergeLayoutSetPrototypeInBackground(
			userId, groupId, exportImportConfiguration);
	}

	@Override
	public MissingReferences validateImportLayoutsFile(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		if (checkPermission) {
			return _exportImportService.validateImportLayoutsFile(
				exportImportConfiguration, inputStream);
		}

		return _exportImportLocalService.validateImportLayoutsFile(
			exportImportConfiguration, inputStream);
	}

	@Override
	public MissingReferences validateImportLayoutsFile(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		return _exportImportLocalService.validateImportLayoutsFile(
			exportImportConfiguration, file);
	}

	@Override
	public MissingReferences validateImportLayoutsFile(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		return validateImportLayoutsFile(
			false, exportImportConfiguration, inputStream);
	}

	@Override
	public MissingReferences validateImportPortletInfo(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		if (checkPermission) {
			return _exportImportService.validateImportPortletInfo(
				exportImportConfiguration, inputStream);
		}

		return _exportImportLocalService.validateImportPortletInfo(
			exportImportConfiguration, inputStream);
	}

	@Override
	public MissingReferences validateImportPortletInfo(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		return _exportImportLocalService.validateImportPortletInfo(
			exportImportConfiguration, file);
	}

	@Override
	public MissingReferences validateImportPortletInfo(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		return validateImportPortletInfo(
			false, exportImportConfiguration, inputStream);
	}

	@Reference
	private ExportImportLocalService _exportImportLocalService;

	@Reference
	private ExportImportService _exportImportService;

}