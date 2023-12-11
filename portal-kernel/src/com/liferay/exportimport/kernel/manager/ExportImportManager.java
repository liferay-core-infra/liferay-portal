/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.manager;

import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.File;
import java.io.InputStream;

/**
 * @author Gabriel Santos
 */
public interface ExportImportManager {

	public File exportLayoutsAsFile(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public File exportLayoutsAsFile(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public long exportLayoutsAsFileInBackground(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public File exportPortletInfoAsFile(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public long exportPortletInfoAsFileInBackground(
			boolean checkPermission, long userId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public long exportPortletInfoAsFileInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public long exportPortletInfoAsFileInBackground(
			long userId, long exportImportConfigurationId)
		throws PortalException;

	public void importLayouts(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException;

	public void importLayouts(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

	public void importLayouts(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException;

	public void importLayouts(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

	public void importLayoutsDataDeletions(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException;

	public long importLayoutSetPrototypeInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException;

	public long importLayoutsInBackground(
			boolean checkPermission, long userId,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

	public long importLayoutsInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException;

	public long importLayoutsInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

	public long importLayoutsInBackground(
			long userId, long exportImportConfigurationId, File file)
		throws PortalException;

	public long importLayoutsInBackground(
			long userId, long exportImportConfigurationId,
			InputStream inputStream)
		throws PortalException;

	public void importPortletDataDeletions(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException;

	public void importPortletInfo(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException;

	public void importPortletInfo(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

	public long importPortletInfoInBackground(
			boolean checkPermission, long userId,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

	public long importPortletInfoInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException;

	public long importPortletInfoInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

	public long importPortletInfoInBackground(
			long userId, long exportImportConfigurationId, File file)
		throws PortalException;

	public long importPortletInfoInBackground(
			long userId, long exportImportConfigurationId,
			InputStream inputStream)
		throws PortalException;

	public long mergeLayoutSetPrototypeInBackground(
			long userId, long groupId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public MissingReferences validateImportLayoutsFile(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

	public MissingReferences validateImportLayoutsFile(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException;

	public MissingReferences validateImportLayoutsFile(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

	public MissingReferences validateImportPortletInfo(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

	public MissingReferences validateImportPortletInfo(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException;

	public MissingReferences validateImportPortletInfo(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException;

}