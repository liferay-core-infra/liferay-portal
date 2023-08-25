/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.upload;

import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.upload.UniqueFileNameProvider;
import com.liferay.upload.UploadFileEntryHandler;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Roberto Díaz
 */
public class KBArticleAttachmentKBUploadFileEntryHandler
	implements UploadFileEntryHandler {

	public KBArticleAttachmentKBUploadFileEntryHandler(
		ModelResourcePermission<KBArticle> kbArticleModelResourcePermission,
		UniqueFileNameProvider uniqueFileNameProvider) {

		_kbArticleModelResourcePermission = kbArticleModelResourcePermission;
		_uniqueFileNameProvider = uniqueFileNameProvider;
	}

	@Override
	public FileEntry upload(UploadPortletRequest uploadPortletRequest)
		throws IOException, PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)uploadPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long resourcePrimKey = ParamUtil.getLong(
			uploadPortletRequest, "resourcePrimKey");

		KBArticle kbArticle = KBArticleLocalServiceUtil.getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_APPROVED);

		_kbArticleModelResourcePermission.check(
			themeDisplay.getPermissionChecker(), kbArticle,
			KBActionKeys.UPDATE);

		String fileName = uploadPortletRequest.getFileName(
			"imageSelectorFileName");

		if (Validator.isNotNull(fileName)) {
			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					"imageSelectorFileName")) {

				return _addKBAttachment(
					fileName, inputStream, kbArticle, "imageSelectorFileName",
					resourcePrimKey, uploadPortletRequest, themeDisplay);
			}
		}

		return _addKBAttachment(
			kbArticle, resourcePrimKey, uploadPortletRequest, themeDisplay);
	}

	private FileEntry _addKBAttachment(
			KBArticle kbArticle, long resourcePrimKey,
			UploadPortletRequest uploadPortletRequest,
			ThemeDisplay themeDisplay)
		throws IOException, PortalException {

		try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
				"imageBlob")) {

			long fileEntryId = ParamUtil.getLong(
				uploadPortletRequest, "fileEntryId");

			FileEntry fileEntry = DLAppServiceUtil.getFileEntry(fileEntryId);

			return _addKBAttachment(
				fileEntry.getFileName(), inputStream, kbArticle, "imageBlob",
				resourcePrimKey, uploadPortletRequest, themeDisplay);
		}
	}

	private FileEntry _addKBAttachment(
			String fileName, InputStream inputStream, KBArticle kbArticle,
			String parameterName, long resourcePrimKey,
			UploadPortletRequest uploadPortletRequest,
			ThemeDisplay themeDisplay)
		throws PortalException {

		String uniqueFileName = _uniqueFileNameProvider.provide(
			fileName,
			curFileName -> _exists(themeDisplay, kbArticle, curFileName));

		return KBArticleLocalServiceUtil.addAttachment(
			themeDisplay.getUserId(), resourcePrimKey, uniqueFileName,
			inputStream, uploadPortletRequest.getContentType(parameterName));
	}

	private boolean _exists(
		ThemeDisplay themeDisplay, KBArticle kbArticle, String fileName) {

		try {
			FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
				themeDisplay.getScopeGroupId(),
				kbArticle.getAttachmentsFolderId(), fileName);

			if (fileEntry != null) {
				return true;
			}

			return false;
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBArticleAttachmentKBUploadFileEntryHandler.class);

	private final ModelResourcePermission<KBArticle>
		_kbArticleModelResourcePermission;
	private final UniqueFileNameProvider _uniqueFileNameProvider;

}