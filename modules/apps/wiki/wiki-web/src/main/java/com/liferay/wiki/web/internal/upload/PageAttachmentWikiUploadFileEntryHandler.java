/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.upload;

import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.wiki.configuration.WikiFileUploadConfiguration;
import com.liferay.wiki.exception.WikiAttachmentMimeTypeException;
import com.liferay.wiki.exception.WikiAttachmentSizeException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageServiceUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roberto Díaz
 * @author Alejandro Tardín
 */
public class PageAttachmentWikiUploadFileEntryHandler
	implements UploadFileEntryHandler {

	public PageAttachmentWikiUploadFileEntryHandler(
		WikiFileUploadConfiguration wikiFileUploadConfiguration,
		ModelResourcePermission<WikiNode> wikiNodeModelResourcePermission) {

		_wikiFileUploadConfiguration = wikiFileUploadConfiguration;
		_wikiNodeModelResourcePermission = wikiNodeModelResourcePermission;
	}

	@Override
	public FileEntry upload(UploadPortletRequest uploadPortletRequest)
		throws IOException, PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)uploadPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (Validator.isNotNull(
				uploadPortletRequest.getFileName("imageSelectorFileName"))) {

			return _addPageAttachment(
				uploadPortletRequest, themeDisplay,
				uploadPortletRequest.getFileName("imageSelectorFileName"),
				"imageSelectorFileName");
		}

		return _editImageFileEntry(uploadPortletRequest, themeDisplay);
	}

	private FileEntry _addPageAttachment(
			UploadPortletRequest uploadPortletRequest,
			ThemeDisplay themeDisplay, String fileName, String parameterName)
		throws IOException, PortalException {

		DLValidatorUtil.validateFileSize(
			themeDisplay.getScopeGroupId(), fileName,
			uploadPortletRequest.getContentType(parameterName),
			uploadPortletRequest.getSize(parameterName));

		long resourcePrimKey = ParamUtil.getLong(
			uploadPortletRequest, "resourcePrimKey");

		WikiPage page = WikiPageServiceUtil.getPage(resourcePrimKey);

		_wikiNodeModelResourcePermission.check(
			themeDisplay.getPermissionChecker(), page.getNodeId(),
			ActionKeys.ADD_ATTACHMENT);

		String contentType = uploadPortletRequest.getContentType(parameterName);

		String[] mimeTypes = ParamUtil.getParameterValues(
			uploadPortletRequest, "mimeTypes");

		_validateFile(
			fileName, contentType, mimeTypes,
			uploadPortletRequest.getSize(parameterName));

		try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
				parameterName)) {

			return WikiPageServiceUtil.addPageAttachment(
				page.getNodeId(), page.getTitle(), fileName, inputStream,
				contentType);
		}
	}

	private FileEntry _editImageFileEntry(
			UploadPortletRequest uploadPortletRequest,
			ThemeDisplay themeDisplay)
		throws IOException, PortalException {

		long fileEntryId = ParamUtil.getLong(
			uploadPortletRequest, "fileEntryId");

		FileEntry fileEntry = DLAppServiceUtil.getFileEntry(fileEntryId);

		return _addPageAttachment(
			uploadPortletRequest, themeDisplay, fileEntry.getFileName(),
			"imageBlob");
	}

	private String[] _getValidMimeTypes(
		String[] mimeTypes, List<String> wikiAttachmentMimeTypes) {

		if (wikiAttachmentMimeTypes.contains(StringPool.STAR)) {
			return mimeTypes;
		}

		List<String> validMimeTypes = new ArrayList<>();

		for (String mimeType : mimeTypes) {
			if (wikiAttachmentMimeTypes.contains(mimeType)) {
				validMimeTypes.add(mimeType);
			}
		}

		return validMimeTypes.toArray(new String[0]);
	}

	private void _validateFile(
			String fileName, String contentType, String[] mimeTypes, long size)
		throws PortalException {

		long wikiAttachmentMaxSize =
			_wikiFileUploadConfiguration.attachmentMaxSize();

		if ((wikiAttachmentMaxSize > 0) && (size > wikiAttachmentMaxSize)) {
			throw new WikiAttachmentSizeException();
		}

		List<String> wikiAttachmentMimeTypes = ListUtil.fromArray(
			_wikiFileUploadConfiguration.attachmentMimeTypes());

		if (ArrayUtil.isEmpty(mimeTypes) &&
			ListUtil.isNull(wikiAttachmentMimeTypes)) {

			return;
		}

		for (String mimeType :
				_getValidMimeTypes(mimeTypes, wikiAttachmentMimeTypes)) {

			if (mimeType.equals(contentType)) {
				return;
			}
		}

		throw new WikiAttachmentMimeTypeException(
			StringBundler.concat(
				"Invalid MIME type ", contentType, " for file name ",
				fileName));
	}

	private volatile WikiFileUploadConfiguration _wikiFileUploadConfiguration;
	private final ModelResourcePermission<WikiNode>
		_wikiNodeModelResourcePermission;

}