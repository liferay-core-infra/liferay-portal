/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.repository;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileShortcutException;
import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryServiceUtil;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileVersionLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchRepositoryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryEntryLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Iván Zaera
 */
public class RepositoryProviderUtil {

	public static LocalRepository fetchFileEntryLocalRepository(
			long fileEntryId)
		throws PortalException {

		long repositoryId = fetchFileEntryRepositoryId(fileEntryId);

		if (repositoryId != -1) {
			try {
				return getLocalRepository(repositoryId);
			}
			catch (InvalidRepositoryIdException invalidRepositoryIdException) {
				throw new NoSuchFileEntryException(
					StringBundler.concat(
						"No FileEntry exists with the key {fileEntryId=",
						fileEntryId, "}"),
					invalidRepositoryIdException);
			}
		}

		return null;
	}

	public static LocalRepository getFileEntryLocalRepository(long fileEntryId)
		throws PortalException {

		try {
			return getLocalRepository(getFileEntryRepositoryId(fileEntryId));
		}
		catch (InvalidRepositoryIdException invalidRepositoryIdException) {
			throw new NoSuchFileEntryException(
				StringBundler.concat(
					"No FileEntry exists with the key {fileEntryId=",
					fileEntryId, "}"),
				invalidRepositoryIdException);
		}
	}

	public static Repository getFileEntryRepository(long fileEntryId)
		throws PortalException {

		try {
			checkFileEntryPermissions(fileEntryId);

			return getRepository(getFileEntryRepositoryId(fileEntryId));
		}
		catch (InvalidRepositoryIdException invalidRepositoryIdException) {
			throw new NoSuchFileEntryException(
				StringBundler.concat(
					"No FileEntry exists with the key {fileEntryId=",
					fileEntryId, "}"),
				invalidRepositoryIdException);
		}
	}

	public static LocalRepository getFileShortcutLocalRepository(
			long fileShortcutId)
		throws PortalException {

		try {
			return getLocalRepository(
				getFileShortcutRepositoryId(fileShortcutId));
		}
		catch (InvalidRepositoryIdException invalidRepositoryIdException) {
			throw new NoSuchFileShortcutException(
				StringBundler.concat(
					"No FileShortcut exists with the key {fileShortcutId=",
					fileShortcutId, "}"),
				invalidRepositoryIdException);
		}
	}

	public static Repository getFileShortcutRepository(long fileShortcutId)
		throws PortalException {

		try {
			checkFileShortcutPermissions(fileShortcutId);

			return getRepository(getFileShortcutRepositoryId(fileShortcutId));
		}
		catch (InvalidRepositoryIdException invalidRepositoryIdException) {
			throw new NoSuchFileShortcutException(
				StringBundler.concat(
					"No FileShortcut exists with the key {fileShortcutId=",
					fileShortcutId, "}"),
				invalidRepositoryIdException);
		}
	}

	public static LocalRepository getFileVersionLocalRepository(
			long fileVersionId)
		throws PortalException {

		try {
			return getLocalRepository(
				getFileVersionRepositoryId(fileVersionId));
		}
		catch (InvalidRepositoryIdException invalidRepositoryIdException) {
			throw new NoSuchFileVersionException(
				StringBundler.concat(
					"No FileVersion exists with the key {fileVersionId=",
					fileVersionId, "}"),
				invalidRepositoryIdException);
		}
	}

	public static Repository getFileVersionRepository(long fileVersionId)
		throws PortalException {

		try {
			checkFileVersionPermissions(fileVersionId);

			return getRepository(getFileVersionRepositoryId(fileVersionId));
		}
		catch (InvalidRepositoryIdException invalidRepositoryIdException) {
			throw new NoSuchFileVersionException(
				StringBundler.concat(
					"No FileVersion exists with the key {fileVersionId=",
					fileVersionId, "}"),
				invalidRepositoryIdException);
		}
	}

	public static LocalRepository getFolderLocalRepository(long folderId)
		throws PortalException {

		try {
			return getLocalRepository(getFolderRepositoryId(folderId));
		}
		catch (InvalidRepositoryIdException invalidRepositoryIdException) {
			throw new NoSuchFolderException(
				StringBundler.concat(
					"No Folder exists with the key {folderId=", folderId, "}"),
				invalidRepositoryIdException);
		}
	}

	public static Repository getFolderRepository(long folderId)
		throws PortalException {

		try {
			checkFolderPermissions(folderId);

			return getRepository(getFolderRepositoryId(folderId));
		}
		catch (InvalidRepositoryIdException invalidRepositoryIdException) {
			throw new NoSuchFolderException(
				StringBundler.concat(
					"No Folder exists with the key {folderId=", folderId, "}"),
				invalidRepositoryIdException);
		}
	}

	public static List<LocalRepository> getGroupLocalRepositories(long groupId)
		throws PortalException {

		List<LocalRepository> localRepositories = new ArrayList<>();

		List<Long> repositoryIds = getGroupRepositoryIds(groupId);

		for (long repositoryId : repositoryIds) {
			localRepositories.add(getLocalRepository(repositoryId));
		}

		return localRepositories;
	}

	public static List<Repository> getGroupRepositories(long groupId)
		throws PortalException {

		List<Repository> repositories = new ArrayList<>();

		List<Long> repositoryIds = getGroupRepositoryIds(groupId);

		for (long repositoryId : repositoryIds) {
			repositories.add(getRepository(repositoryId));
		}

		return repositories;
	}

	public static LocalRepository getImageLocalRepository(long imageId)
		throws PortalException {

		return getLocalRepository(getImageRepositoryId(imageId));
	}

	public static Repository getImageRepository(long imageId)
		throws PortalException {

		return getRepository(getImageRepositoryId(imageId));
	}

	public static LocalRepository getLocalRepository(long repositoryId)
		throws PortalException {

		LocalRepository localRepository =
			RepositoryFactoryUtil.createLocalRepository(repositoryId);

		checkRepository(repositoryId);
		checkRepositoryAccess(repositoryId);

		return localRepository;
	}

	public static Repository getRepository(long repositoryId)
		throws PortalException {

		Repository repository = RepositoryFactoryUtil.createRepository(
			repositoryId);

		checkRepository(repositoryId);
		checkRepositoryAccess(repositoryId);

		return repository;
	}

	protected static void checkFileEntryPermissions(long fileEntryId)
		throws PortalException {

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.fetchDLFileEntry(
			fileEntryId);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((dlFileEntry != null) && (permissionChecker != null)) {
			ModelResourcePermission<DLFileEntry>
				dlFileEntryModelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(DLFileEntry.class.getName());

			dlFileEntryModelResourcePermission.check(
				permissionChecker, dlFileEntry, ActionKeys.VIEW);
		}
	}

	protected static void checkFileShortcutPermissions(long fileShortcutId)
		throws PortalException {

		DLFileShortcut dlFileShortcut =
			DLFileShortcutLocalServiceUtil.fetchDLFileShortcut(fileShortcutId);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((dlFileShortcut != null) && (permissionChecker != null)) {
			ModelResourcePermission<FileEntry>
				fileEntryModelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(FileEntry.class.getName());

			fileEntryModelResourcePermission.check(
				permissionChecker, dlFileShortcut.getToFileEntryId(),
				ActionKeys.VIEW);
		}
	}

	protected static void checkFileVersionPermissions(long fileVersionId)
		throws PortalException {

		DLFileVersion dlFileVersion =
			DLFileVersionLocalServiceUtil.fetchDLFileVersion(fileVersionId);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((dlFileVersion != null) && (permissionChecker != null)) {
			ModelResourcePermission<FileEntry>
				fileEntryModelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(FileEntry.class.getName());

			fileEntryModelResourcePermission.check(
				permissionChecker, dlFileVersion.getFileEntryId(),
				ActionKeys.VIEW);
		}
	}

	protected static void checkFolderPermissions(long folderId)
		throws PortalException {

		DLFolder dlFolder = DLFolderLocalServiceUtil.fetchDLFolder(folderId);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((dlFolder != null) && (permissionChecker != null)) {
			ModelResourcePermission<DLFolder> dlFolderModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFolder.class.getName());

			dlFolderModelResourcePermission.check(
				permissionChecker, dlFolder, ActionKeys.VIEW);
		}
	}

	protected static void checkRepository(long repositoryId)
		throws PortalException {

		Group group = GroupLocalServiceUtil.fetchGroup(repositoryId);

		if (group != null) {
			return;
		}

		try {
			RepositoryLocalServiceUtil.getRepository(repositoryId);
		}
		catch (NoSuchRepositoryException noSuchRepositoryException) {
			throw new InvalidRepositoryIdException(
				noSuchRepositoryException.getMessage());
		}
	}

	protected static void checkRepositoryAccess(long repositoryId)
		throws PortalException {

		Group group = GroupLocalServiceUtil.fetchGroup(repositoryId);

		if (group != null) {
			return;
		}

		try {
			com.liferay.portal.kernel.model.Repository repository =
				RepositoryLocalServiceUtil.fetchRepository(repositoryId);

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if ((repository != null) && (permissionChecker != null)) {
				try {
					ModelResourcePermission<Folder>
						folderModelResourcePermission =
							ModelResourcePermissionRegistryUtil.
								getModelResourcePermission(
									Folder.class.getName());

					ModelResourcePermissionUtil.check(
						folderModelResourcePermission, permissionChecker,
						repository.getGroupId(), repository.getDlFolderId(),
						ActionKeys.VIEW);
				}
				catch (NoSuchFolderException noSuchFolderException) {

					// LPS-52675

					if (_log.isDebugEnabled()) {
						_log.debug(noSuchFolderException);
					}
				}
			}
		}
		catch (NoSuchRepositoryException noSuchRepositoryException) {
			throw new InvalidRepositoryIdException(
				noSuchRepositoryException.getMessage());
		}
	}

	protected static long fetchFileEntryRepositoryId(long fileEntryId) {
		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.fetchDLFileEntry(
			fileEntryId);

		if (dlFileEntry != null) {
			return dlFileEntry.getRepositoryId();
		}

		RepositoryEntry repositoryEntry =
			RepositoryEntryLocalServiceUtil.fetchRepositoryEntry(fileEntryId);

		if (repositoryEntry != null) {
			return repositoryEntry.getRepositoryId();
		}

		return -1;
	}

	protected static long getFileEntryRepositoryId(long fileEntryId) {
		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.fetchDLFileEntry(
			fileEntryId);

		if (dlFileEntry != null) {
			return dlFileEntry.getRepositoryId();
		}

		RepositoryEntry repositoryEntry =
			RepositoryEntryLocalServiceUtil.fetchRepositoryEntry(fileEntryId);

		if (repositoryEntry != null) {
			return repositoryEntry.getRepositoryId();
		}

		throw new InvalidRepositoryIdException(
			"No repository associated with file entry " + fileEntryId);
	}

	protected static long getFileShortcutRepositoryId(long fileShortcutId) {
		DLFileShortcut dlFileShortcut =
			DLFileShortcutLocalServiceUtil.fetchDLFileShortcut(fileShortcutId);

		if (dlFileShortcut != null) {
			return dlFileShortcut.getRepositoryId();
		}

		throw new InvalidRepositoryIdException(
			"No repository associated with file shortcut " + fileShortcutId);
	}

	protected static long getFileVersionRepositoryId(long fileVersionId) {
		DLFileVersion dlFileVersion =
			DLFileVersionLocalServiceUtil.fetchDLFileVersion(fileVersionId);

		if (dlFileVersion != null) {
			return dlFileVersion.getRepositoryId();
		}

		RepositoryEntry repositoryEntry =
			RepositoryEntryLocalServiceUtil.fetchRepositoryEntry(fileVersionId);

		if (repositoryEntry != null) {
			return repositoryEntry.getRepositoryId();
		}

		throw new InvalidRepositoryIdException(
			"No repository associated with file version " + fileVersionId);
	}

	protected static long getFolderRepositoryId(long folderId) {
		DLFolder dlFolder = DLFolderLocalServiceUtil.fetchDLFolder(folderId);

		if (dlFolder != null) {
			if (dlFolder.isMountPoint()) {
				return dlFolder.getGroupId();
			}

			return dlFolder.getRepositoryId();
		}

		RepositoryEntry repositoryEntry =
			RepositoryEntryLocalServiceUtil.fetchRepositoryEntry(folderId);

		if (repositoryEntry != null) {
			return repositoryEntry.getRepositoryId();
		}

		throw new InvalidRepositoryIdException(
			"No repository associated with folder " + folderId);
	}

	protected static List<Long> getGroupRepositoryIds(long groupId) {
		List<com.liferay.portal.kernel.model.Repository> repositories =
			RepositoryLocalServiceUtil.getGroupRepositories(groupId);

		List<Long> repositoryIds = new ArrayList<>(repositories.size() + 1);

		for (com.liferay.portal.kernel.model.Repository repository :
				repositories) {

			repositoryIds.add(repository.getRepositoryId());
		}

		repositoryIds.add(groupId);

		return repositoryIds;
	}

	protected static long getImageRepositoryId(long imageId)
		throws PortalException {

		DLFileEntry dlFileEntry =
			DLFileEntryServiceUtil.fetchFileEntryByImageId(imageId);

		if (dlFileEntry != null) {
			return dlFileEntry.getRepositoryId();
		}

		throw new InvalidRepositoryIdException(
			"No repository associated with image " + imageId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RepositoryProviderUtil.class);

}