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
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;

import com.liferay.document.library.opener.google.drive.web.internal.constants.DLOpenerGoogleDriveConstants;
import com.liferay.document.library.opener.google.drive.web.internal.oauth.OAuth2Manager;
import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.document.library.opener.service.DLOpenerFileEntryReferenceLocalService;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Joao Victor Alves
 */
public class DLOpenerGoogleDriveManagerUtil {

	public static File getContentFile(long userId, FileEntry fileEntry) {
		try {
			Credential credential = getCredential(
				fileEntry.getCompanyId(), userId);

			Drive drive = new Drive.Builder(
				_netHttpTransport, _jsonFactory, credential
			).build();

			Drive.Files driveFiles = drive.files();

			Drive.Files.Get get = driveFiles.get(
				getGoogleDriveFileId(fileEntry));

			get.setFields("exportLinks");

			com.google.api.services.drive.model.File file = get.execute();

			Map<String, String> exportLinks = file.getExportLinks();

			URL url = new URL(exportLinks.get(fileEntry.getMimeType()));

			if (!StringUtil.startsWith(url.getProtocol(), Http.HTTP)) {
				throw new SecurityException(
					"Only HTTP links are allowed: " + url);
			}

			if (InetAddressUtil.isLocalInetAddress(
					InetAddress.getByName(url.getHost()))) {

				throw new SecurityException(
					"Local links are not allowed: " + url);
			}

			URLConnection urlConnection = url.openConnection();

			urlConnection.setRequestProperty(
				"Authorization", "Bearer " + credential.getAccessToken());

			try (InputStream inputStream = urlConnection.getInputStream()) {
				return FileUtil.createTempFile(inputStream);
			}
		}
		catch (GoogleJsonResponseException googleJsonResponseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"The Google Drive file does not exist",
					googleJsonResponseException);
			}

			return null;
		}
		catch (IOException | PortalException exception) {
			throw new RuntimeException(exception);
		}
	}

	public static Credential getCredential(long companyId, long userId)
		throws PortalException {

		OAuth2Manager oAuth2Manager = _oAuth2ManagerSnapshot.get();

		Credential credential = oAuth2Manager.getCredential(companyId, userId);

		if (credential == null) {
			throw new PrincipalException(
				StringBundler.concat(
					"User ", userId,
					" does not have a valid Google credential"));
		}

		return credential;
	}

	public static DLOpenerGoogleDriveFileReference
			getDLOpenerGoogleDriveFileReference(
				long userId, FileEntry fileEntry)
		throws PortalException {

		if (Validator.isNull(getGoogleDriveFileId(fileEntry))) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"File entry ", fileEntry.getFileEntryId(),
					" is not a Google Drive file"));
		}

		_checkCredential(fileEntry.getCompanyId(), userId);

		return new DLOpenerGoogleDriveFileReference(
			fileEntry.getFileEntryId(),
			new DLOpenerGoogleDriveManagerUtil.CachingSupplier<>(
				() -> getGoogleDriveFileTitle(userId, fileEntry)),
			() -> getContentFile(userId, fileEntry), 0);
	}

	public static String getGoogleDriveFileId(FileEntry fileEntry)
		throws PortalException {

		DLOpenerFileEntryReferenceLocalService
			dlOpenerFileEntryReferenceLocalService =
				_dlOpenerFileEntryReferenceLocalServiceSnapshot.get();

		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			dlOpenerFileEntryReferenceLocalService.
				getDLOpenerFileEntryReference(
					DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
					fileEntry);

		return dlOpenerFileEntryReference.getReferenceKey();
	}

	public static String getGoogleDriveFileTitle(
		long userId, FileEntry fileEntry) {

		try {
			Drive drive = new Drive.Builder(
				_netHttpTransport, _jsonFactory,
				getCredential(fileEntry.getCompanyId(), userId)
			).build();

			Drive.Files driveFiles = drive.files();

			Drive.Files.Get driveFilesGet = driveFiles.get(
				getGoogleDriveFileId(fileEntry));

			com.google.api.services.drive.model.File file =
				driveFilesGet.execute();

			return file.getName();
		}
		catch (IOException | PortalException exception) {
			throw new RuntimeException(exception);
		}
	}

	public static JsonFactory getJsonFactory() {
		return _jsonFactory;
	}

	public static NetHttpTransport getNetHttpTransport() {
		return _netHttpTransport;
	}

	public static boolean hasGoogleDriveFile(long userId, FileEntry fileEntry) {
		try {
			Drive drive = new Drive.Builder(
				_netHttpTransport, _jsonFactory,
				getCredential(fileEntry.getCompanyId(), userId)
			).build();

			Drive.Files driveFiles = drive.files();

			Drive.Files.Get driveFilesGet = driveFiles.get(
				getGoogleDriveFileId(fileEntry));

			driveFilesGet.execute();
		}
		catch (IOException | PortalException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("The Google Drive file does not exist", exception);
			}

			return false;
		}

		return true;
	}

	public static void setJsonFactory(JsonFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	public static void setNetHttpTransport(NetHttpTransport netHttpTransport) {
		_netHttpTransport = netHttpTransport;
	}

	public static class CachingSupplier<T> implements Supplier<T> {

		public CachingSupplier(Supplier<T> supplier) {
			_supplier = supplier;
		}

		@Override
		public T get() {
			if (_value != null) {
				return _value;
			}

			_value = _supplier.get();

			return _value;
		}

		private final Supplier<T> _supplier;
		private T _value;

	}

	private static void _checkCredential(long companyId, long userId)
		throws PortalException {

		getCredential(companyId, userId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLOpenerGoogleDriveManagerUtil.class);

	private static final Snapshot<DLOpenerFileEntryReferenceLocalService>
		_dlOpenerFileEntryReferenceLocalServiceSnapshot = new Snapshot<>(
			DLOpenerGoogleDriveManagerUtil.class,
			DLOpenerFileEntryReferenceLocalService.class);
	private static JsonFactory _jsonFactory;
	private static NetHttpTransport _netHttpTransport;
	private static final Snapshot<OAuth2Manager> _oAuth2ManagerSnapshot =
		new Snapshot<>(
			DLOpenerGoogleDriveManagerUtil.class, OAuth2Manager.class);

}