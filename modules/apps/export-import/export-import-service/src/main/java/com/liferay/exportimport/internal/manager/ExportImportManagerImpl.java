/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.manager;

import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.controller.ExportController;
import com.liferay.exportimport.kernel.controller.ExportImportController;
import com.liferay.exportimport.kernel.controller.ImportController;
import com.liferay.exportimport.kernel.exception.ExportImportIOException;
import com.liferay.exportimport.kernel.exception.ExportImportRuntimeException;
import com.liferay.exportimport.kernel.exception.LARFileNameException;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.manager.ExportImportManager;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.osgi.framework.BundleContext;
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
			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				MapUtil.getLong(
					exportImportConfiguration.getSettingsMap(),
					"sourceGroupId"),
				ActionKeys.EXPORT_IMPORT_LAYOUTS);
		}

		try {
			ExportController layoutExportController = _getExportController(
				Layout.class.getName());

			return layoutExportController.export(exportImportConfiguration);
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportManagerImpl.class.getName());

			throw exportImportRuntimeException;
		}
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

		GroupPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			MapUtil.getLong(
				exportImportConfiguration.getSettingsMap(), "sourceGroupId"),
			ActionKeys.EXPORT_IMPORT_LAYOUTS);

		if (!DLValidatorUtil.isValidName(exportImportConfiguration.getName())) {
			throw new LARFileNameException(exportImportConfiguration.getName());
		}

		BackgroundTask backgroundTask =
			_backgroundTaskManager.addBackgroundTask(
				GuestOrUserUtil.getUserId(),
				exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					LAYOUT_EXPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		return backgroundTask.getBackgroundTaskId();
	}

	@Override
	public File exportPortletInfoAsFile(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		try {
			ExportController portletExportController = _getExportController(
				Portlet.class.getName());

			return portletExportController.export(exportImportConfiguration);
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportManagerImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@Override
	public long exportPortletInfoAsFileInBackground(
			boolean checkPermission, long userId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		if (checkPermission) {
			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				MapUtil.getLong(
					exportImportConfiguration.getSettingsMap(),
					"sourceGroupId"),
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO);
		}

		String fileName = MapUtil.getString(
			exportImportConfiguration.getSettingsMap(), "fileName");

		if (!DLValidatorUtil.isValidName(fileName)) {
			throw new LARFileNameException(fileName);
		}

		BackgroundTask backgroundTask =
			_backgroundTaskManager.addBackgroundTask(
				userId, exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		return backgroundTask.getBackgroundTaskId();
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

		return exportPortletInfoAsFileInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId));
	}

	@Override
	public void importLayouts(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		if (checkPermission) {
			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				MapUtil.getLong(
					exportImportConfiguration.getSettingsMap(),
					"targetGroupId"),
				ActionKeys.EXPORT_IMPORT_LAYOUTS);
		}

		try {
			ImportController layoutImportController = _getImportController(
				Layout.class.getName());

			layoutImportController.importFile(exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			if (throwable instanceof LocaleException) {
				throw (PortalException)throwable;
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportManagerImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@Override
	public void importLayouts(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		if (checkPermission) {
			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				MapUtil.getLong(
					exportImportConfiguration.getSettingsMap(),
					"targetGroupId"),
				ActionKeys.EXPORT_IMPORT_LAYOUTS);
		}

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			importLayouts(exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportManagerImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_IMPORT_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_IMPORT);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
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

		try {
			ImportController layoutImportController = _getImportController(
				Layout.class.getName());

			layoutImportController.importDataDeletions(
				exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			if (throwable instanceof LocaleException) {
				throw (PortalException)throwable;
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportManagerImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@Override
	public long importLayoutSetPrototypeInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException {

		BackgroundTask backgroundTask =
			_backgroundTaskManager.addBackgroundTask(
				userId, exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					LAYOUT_SET_PROTOTYPE_IMPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
				).put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		backgroundTask.addAttachment(userId, file.getName(), file);

		return backgroundTask.getBackgroundTaskId();
	}

	@Override
	public long importLayoutsInBackground(
			boolean checkPermission, long userId,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		if (checkPermission) {
			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				MapUtil.getLong(
					exportImportConfiguration.getSettingsMap(),
					"targetGroupId"),
				ActionKeys.EXPORT_IMPORT_LAYOUTS);
		}

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			return importLayoutsInBackground(
				userId, exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportManagerImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_IMPORT_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_IMPORT);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public long importLayoutsInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException {

		BackgroundTask backgroundTask =
			_backgroundTaskManager.addBackgroundTask(
				userId, exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		backgroundTask.addAttachment(userId, file.getName(), file);

		return backgroundTask.getBackgroundTaskId();
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

		return importPortletInfoInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId),
			file);
	}

	@Override
	public long importLayoutsInBackground(
			long userId, long exportImportConfigurationId,
			InputStream inputStream)
		throws PortalException {

		return importLayoutsInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId),
			inputStream);
	}

	@Override
	public void importPortletDataDeletions(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		try {
			ImportController portletImportController = _getImportController(
				Portlet.class.getName());

			portletImportController.importDataDeletions(
				exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			if (throwable instanceof LocaleException) {
				throw (PortalException)throwable;
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportManagerImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@Override
	public void importPortletInfo(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		try {
			ImportController portletImportController = _getImportController(
				Portlet.class.getName());

			portletImportController.importFile(exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			while (true) {
				if (throwable == null) {
					break;
				}

				if (throwable instanceof LocaleException) {
					throw (PortalException)throwable;
				}

				if (throwable instanceof PortletDataException) {
					throwable = throwable.getCause();
				}
				else {
					break;
				}
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportManagerImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@Override
	public void importPortletInfo(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			importPortletInfo(exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportManagerImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_IMPORT_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_IMPORT);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public long importPortletInfoInBackground(
			boolean checkPermission, long userId,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		if (checkPermission) {
			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				MapUtil.getLong(
					exportImportConfiguration.getSettingsMap(),
					"targetGroupId"),
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO);
		}

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			return importPortletInfoInBackground(
				userId, exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportManagerImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_IMPORT_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_IMPORT);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public long importPortletInfoInBackground(
			long userId, ExportImportConfiguration exportImportConfiguration,
			File file)
		throws PortalException {

		BackgroundTask backgroundTask =
			_backgroundTaskManager.addBackgroundTask(
				userId, exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		backgroundTask.addAttachment(userId, file.getName(), file);

		return backgroundTask.getBackgroundTaskId();
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

		return importPortletInfoInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId),
			file);
	}

	@Override
	public long importPortletInfoInBackground(
			long userId, long exportImportConfigurationId,
			InputStream inputStream)
		throws PortalException {

		return importPortletInfoInBackground(
			userId,
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId),
			inputStream);
	}

	@Override
	public long mergeLayoutSetPrototypeInBackground(
			long userId, long groupId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		BackgroundTask backgroundTask =
			_backgroundTaskManager.addBackgroundTask(
				userId, groupId, exportImportConfiguration.getName(),
				BackgroundTaskExecutorNames.
					LAYOUT_SET_PROTOTYPE_MERGE_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
				).put(
					"exportImportConfigurationId",
					exportImportConfiguration.getExportImportConfigurationId()
				).build(),
				new ServiceContext());

		return backgroundTask.getBackgroundTaskId();
	}

	@Override
	public MissingReferences validateImportLayoutsFile(
			boolean checkPermission,
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		if (checkPermission) {
			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				MapUtil.getLong(
					exportImportConfiguration.getSettingsMap(),
					"targetGroupId"),
				ActionKeys.EXPORT_IMPORT_LAYOUTS);
		}

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			return validateImportLayoutsFile(exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportManagerImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_VALIDATE_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.LAYOUT_VALIDATE);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public MissingReferences validateImportLayoutsFile(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		try {
			ImportController layoutImportController = _getImportController(
				Layout.class.getName());

			return layoutImportController.validateFile(
				exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			if (throwable instanceof LocaleException) {
				throw (PortalException)throwable;
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportManagerImpl.class.getName());

			throw exportImportRuntimeException;
		}
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
			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				MapUtil.getLong(
					exportImportConfiguration.getSettingsMap(),
					"targetGroupId"),
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO);
		}

		File file = null;

		try {
			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, inputStream);

			return validateImportPortletInfo(exportImportConfiguration, file);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					ExportImportManagerImpl.class.getName(), ioException);

			if (file != null) {
				exportImportIOException.setFileName(file.getName());
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_VALIDATE_FILE);
			}
			else {
				exportImportIOException.setType(
					ExportImportIOException.PORTLET_VALIDATE);
			}

			throw exportImportIOException;
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public MissingReferences validateImportPortletInfo(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		try {
			ImportController portletImportController = _getImportController(
				Portlet.class.getName());

			return portletImportController.validateFile(
				exportImportConfiguration, file);
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			if (throwable instanceof LocaleException) {
				throw (PortalException)throwable;
			}

			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			ExportImportRuntimeException exportImportRuntimeException =
				new ExportImportRuntimeException(
					exception.getLocalizedMessage(), exception);

			exportImportRuntimeException.setClassName(
				ExportImportManagerImpl.class.getName());

			throw exportImportRuntimeException;
		}
	}

	@Override
	public MissingReferences validateImportPortletInfo(
			ExportImportConfiguration exportImportConfiguration,
			InputStream inputStream)
		throws PortalException {

		return validateImportPortletInfo(
			false, exportImportConfiguration, inputStream);
	}

	private ExportController _getExportController(String className) {
		return (ExportController)_exportControllers.getService(className);
	}

	private ImportController _getImportController(String className) {
		return (ImportController)_importControllers.getService(className);
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap<String, ExportImportController>
		_exportControllers = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, ExportImportController.class, null,
			(serviceReference, emitter) -> {
				ExportImportController exportImportController =
					_bundleContext.getService(serviceReference);

				if (exportImportController instanceof ExportController) {
					for (String modelClassName :
							StringUtil.asList(
								serviceReference.getProperty(
									"model.class.name"))) {

						emitter.emit(modelClassName);
					}
				}

				_bundleContext.ungetService(serviceReference);
			});

	private static final ServiceTrackerMap<String, ExportImportController>
		_importControllers = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, ExportImportController.class, null,
			(serviceReference, emitter) -> {
				ExportImportController exportImportController =
					_bundleContext.getService(serviceReference);

				if (exportImportController instanceof ImportController) {
					for (String modelClassName :
							StringUtil.asList(
								serviceReference.getProperty(
									"model.class.name"))) {

						emitter.emit(modelClassName);
					}
				}

				_bundleContext.ungetService(serviceReference);
			});

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

}