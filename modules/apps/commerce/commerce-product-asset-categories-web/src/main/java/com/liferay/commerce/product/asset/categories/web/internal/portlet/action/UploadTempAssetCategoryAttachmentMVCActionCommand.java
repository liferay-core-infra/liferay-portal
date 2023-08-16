/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.portlet.action;

import com.liferay.commerce.product.asset.categories.web.internal.constants.CommerceProductAssetCategoriesPortletKeys;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"javax.portlet.name=" + CommerceProductAssetCategoriesPortletKeys.ASSET_CATEGORIES_ADMIN,
		"mvc.command.name=/commerce_product_asset_categories/upload_temp_asset_category_attachment"
	},
	service = MVCActionCommand.class
)
public class UploadTempAssetCategoryAttachmentMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_tempAssetCategoryAttachmentsUploadFileEntryHandler,
			_assetCategoryAttachmentsUploadResponseHandler, actionRequest,
			actionResponse);
	}

	@Reference(
		target = "(component.name=com.liferay.commerce.product.asset.categories.web.internal.upload.AssetCategoryAttachmentsUploadResponseHandler)"
	)
	private UploadResponseHandler
		_assetCategoryAttachmentsUploadResponseHandler;

	@Reference(
		target = "(component.name=com.liferay.commerce.product.asset.categories.web.internal.upload.TempAssetCategoryAttachmentsUploadFileEntryHandler)"
	)
	private UploadFileEntryHandler
		_tempAssetCategoryAttachmentsUploadFileEntryHandler;

	@Reference
	private UploadHandler _uploadHandler;

}