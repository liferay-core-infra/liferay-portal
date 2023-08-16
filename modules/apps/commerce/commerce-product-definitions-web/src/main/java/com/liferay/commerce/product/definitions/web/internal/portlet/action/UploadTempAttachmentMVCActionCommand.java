/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"javax.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/upload_temp_attachment"
	},
	service = MVCActionCommand.class
)
public class UploadTempAttachmentMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_tempAttachmentsUploadFileEntryHandler,
			_attachmentsUploadResponseHandler, actionRequest, actionResponse);
	}

	@Reference(
		target = "(component.name=com.liferay.commerce.product.definitions.web.internal.upload.AttachmentsUploadResponseHandler)"
	)
	private UploadResponseHandler _attachmentsUploadResponseHandler;

	@Reference(
		target = "(component.name=com.liferay.commerce.product.definitions.web.internal.upload.TempAttachmentsUploadFileEntryHandler)"
	)
	private UploadFileEntryHandler _tempAttachmentsUploadFileEntryHandler;

	@Reference
	private UploadHandler _uploadHandler;

}