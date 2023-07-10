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

package com.liferay.document.library.opener.google.drive.web.internal;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import com.liferay.document.library.opener.constants.DLOpenerFileEntryReferenceConstants;
import com.liferay.document.library.opener.google.drive.DLOpenerGoogleDriveManager;
import com.liferay.document.library.opener.google.drive.web.internal.background.task.UploadGoogleDriveDocumentBackgroundTaskExecutor;
import com.liferay.document.library.opener.google.drive.web.internal.constants.DLOpenerGoogleDriveConstants;
import com.liferay.document.library.opener.google.drive.web.internal.constants.GoogleDriveBackgroundTaskConstants;
import com.liferay.document.library.opener.google.drive.web.internal.oauth.OAuth2Manager;
import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.document.library.opener.service.DLOpenerFileEntryReferenceLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.IOException;
import java.io.Serializable;

import java.security.GeneralSecurityException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = DLOpenerGoogleDriveManager.class)
public class DLOpenerGoogleDriveManagerImpl
	implements DLOpenerGoogleDriveManager {

	@Override
	public DLOpenerGoogleDriveFileReference checkOut(
			long userId, FileEntry fileEntry)
		throws PortalException {

		_dlOpenerFileEntryReferenceLocalService.
			addPlaceholderDLOpenerFileEntryReference(
				userId,
				DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
				fileEntry, DLOpenerFileEntryReferenceConstants.TYPE_EDIT);

		BackgroundTask backgroundTask = _addBackgroundTask(
			GoogleDriveBackgroundTaskConstants.CHECKOUT, fileEntry, userId);

		return new DLOpenerGoogleDriveFileReference(
			fileEntry.getFileEntryId(),
			new DLOpenerGoogleDriveManagerUtil.CachingSupplier<>(
				() -> DLOpenerGoogleDriveManagerUtil.getGoogleDriveFileTitle(
					userId, fileEntry)),
			() -> DLOpenerGoogleDriveManagerUtil.getContentFile(
				userId, fileEntry),
			backgroundTask.getBackgroundTaskId());
	}

	@Override
	public DLOpenerGoogleDriveFileReference create(
			long userId, FileEntry fileEntry)
		throws PortalException {

		_dlOpenerFileEntryReferenceLocalService.
			addPlaceholderDLOpenerFileEntryReference(
				userId,
				DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
				fileEntry, DLOpenerFileEntryReferenceConstants.TYPE_NEW);

		BackgroundTask backgroundTask = _addBackgroundTask(
			GoogleDriveBackgroundTaskConstants.CREATE, fileEntry, userId);

		return new DLOpenerGoogleDriveFileReference(
			fileEntry.getFileEntryId(),
			new DLOpenerGoogleDriveManagerUtil.CachingSupplier<>(
				() -> DLOpenerGoogleDriveManagerUtil.getGoogleDriveFileTitle(
					userId, fileEntry)),
			() -> DLOpenerGoogleDriveManagerUtil.getContentFile(
				userId, fileEntry),
			backgroundTask.getBackgroundTaskId());
	}

	@Override
	public void delete(long userId, FileEntry fileEntry)
		throws PortalException {

		try {
			Drive drive = new Drive.Builder(
				DLOpenerGoogleDriveManagerUtil.getNetHttpTransport(),
				DLOpenerGoogleDriveManagerUtil.getJsonFactory(),
				DLOpenerGoogleDriveManagerUtil.getCredential(
					fileEntry.getCompanyId(), userId)
			).build();

			Drive.Files driveFiles = drive.files();

			Drive.Files.Delete driveFilesDelete = driveFiles.delete(
				DLOpenerGoogleDriveManagerUtil.getGoogleDriveFileId(fileEntry));

			driveFilesDelete.execute();

			_dlOpenerFileEntryReferenceLocalService.
				deleteDLOpenerFileEntryReference(
					DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
					fileEntry);
		}
		catch (GoogleJsonResponseException googleJsonResponseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"The Google Drive file does not exist",
					googleJsonResponseException);
			}

			_dlOpenerFileEntryReferenceLocalService.
				deleteDLOpenerFileEntryReference(
					DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
					fileEntry);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	@Override
	public String getAuthorizationURL(
			long companyId, String state, String redirectUri)
		throws PortalException {

		return _oAuth2Manager.getAuthorizationURL(
			companyId, state, redirectUri);
	}

	@Override
	public boolean hasValidCredential(long companyId, long userId)
		throws IOException, PortalException {

		Credential credential = _oAuth2Manager.getCredential(companyId, userId);

		if ((credential == null) ||
			((credential.getExpiresInSeconds() <= 0) &&
			 !credential.refreshToken())) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isConfigured(long companyId) {
		return _oAuth2Manager.isConfigured(companyId);
	}

	@Override
	public boolean isGoogleDriveFile(FileEntry fileEntry) {
		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			_dlOpenerFileEntryReferenceLocalService.
				fetchDLOpenerFileEntryReference(
					DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
					fileEntry);

		if (dlOpenerFileEntryReference != null) {
			return true;
		}

		return false;
	}

	@Override
	public void requestAuthorizationToken(
			long companyId, long userId, String code, String redirectUri)
		throws IOException, PortalException {

		_oAuth2Manager.requestAuthorizationToken(
			companyId, userId, code, redirectUri);
	}

	@Override
	public DLOpenerGoogleDriveFileReference requestEditAccess(
			long userId, FileEntry fileEntry)
		throws PortalException {

		if (DLOpenerGoogleDriveManagerUtil.hasGoogleDriveFile(
				userId, fileEntry)) {

			return DLOpenerGoogleDriveManagerUtil.
				getDLOpenerGoogleDriveFileReference(userId, fileEntry);
		}

		_dlOpenerFileEntryReferenceLocalService.
			deleteDLOpenerFileEntryReference(
				DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
				fileEntry);

		return checkOut(userId, fileEntry);
	}

	@Override
	public void setAuthorizationToken(
			long companyId, long userId, String authorizationToken)
		throws IOException, PortalException {

		_oAuth2Manager.setAccessToken(companyId, userId, authorizationToken);
	}

	@Activate
	protected void activate() throws GeneralSecurityException, IOException {
		DLOpenerGoogleDriveManagerUtil.setJsonFactory(
			JacksonFactory.getDefaultInstance());
		DLOpenerGoogleDriveManagerUtil.setNetHttpTransport(
			GoogleNetHttpTransport.newTrustedTransport());
	}

	private BackgroundTask _addBackgroundTask(
			String cmd, FileEntry fileEntry, long userId)
		throws PortalException {

		Map<String, Serializable> taskContextMap =
			HashMapBuilder.<String, Serializable>put(
				BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
			).put(
				GoogleDriveBackgroundTaskConstants.CMD, cmd
			).put(
				GoogleDriveBackgroundTaskConstants.COMPANY_ID,
				fileEntry.getCompanyId()
			).put(
				GoogleDriveBackgroundTaskConstants.FILE_ENTRY_ID,
				fileEntry.getFileEntryId()
			).put(
				GoogleDriveBackgroundTaskConstants.USER_ID, userId
			).build();

		return _backgroundTaskManager.addBackgroundTask(
			userId, CompanyConstants.SYSTEM,
			StringBundler.concat(
				DLOpenerGoogleDriveManagerImpl.class.getSimpleName(),
				StringPool.POUND, fileEntry.getFileEntryId()),
			UploadGoogleDriveDocumentBackgroundTaskExecutor.class.getName(),
			taskContextMap, new ServiceContext());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLOpenerGoogleDriveManagerImpl.class);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private DLOpenerFileEntryReferenceLocalService
		_dlOpenerFileEntryReferenceLocalService;

	@Reference
	private OAuth2Manager _oAuth2Manager;

}