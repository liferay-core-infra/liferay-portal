/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.exception.WikiAttachmentMimeTypeException;
import com.liferay.wiki.exception.WikiAttachmentSizeException;
import com.liferay.wiki.web.internal.upload.PageAttachmentWikiUploadFileEntryHandler;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"javax.portlet.name=" + WikiPortletKeys.WIKI,
		"mvc.command.name=/wiki/image_editor",
		"mvc.command.name=/wiki/upload_page_attachment"
	},
	service = MVCActionCommand.class
)
public class UploadPageAttachmentMVCActionCommand extends BaseMVCActionCommand {

	public class PageAttachmentWikiUploadResponseHandler
		implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			JSONObject jsonObject =
				_itemSelectorUploadResponseHandler.onFailure(
					portletRequest, portalException);

			JSONObject errorJSONObject = null;

			if (portalException instanceof WikiAttachmentMimeTypeException) {
				errorJSONObject = JSONUtil.put(
					"errorType",
					ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION);
			}
			else if (portalException instanceof WikiAttachmentSizeException) {
				errorJSONObject = JSONUtil.put(
					"errorType",
					ServletResponseConstants.SC_FILE_SIZE_EXCEPTION);
			}

			jsonObject.put("error", errorJSONObject);

			return jsonObject;
		}

		@Override
		public JSONObject onSuccess(
				UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
			throws PortalException {

			return _itemSelectorUploadResponseHandler.onSuccess(
				uploadPortletRequest, fileEntry);
		}

	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_pageAttachmentWikiUploadFileEntryHandler,
			_pageAttachmentWikiUploadResponseHandler, actionRequest,
			actionResponse);
	}

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	@Reference
	private PageAttachmentWikiUploadFileEntryHandler
		_pageAttachmentWikiUploadFileEntryHandler;

	private final PageAttachmentWikiUploadResponseHandler
		_pageAttachmentWikiUploadResponseHandler =
			new PageAttachmentWikiUploadResponseHandler();

	@Reference
	private UploadHandler _uploadHandler;

}