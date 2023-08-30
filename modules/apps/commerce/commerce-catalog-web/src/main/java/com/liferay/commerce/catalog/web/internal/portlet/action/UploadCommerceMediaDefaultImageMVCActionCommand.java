/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.catalog.web.internal.portlet.action;

import com.liferay.commerce.catalog.web.internal.upload.CommerceMediaDefaultImageUploadFileEntryHandler;
import com.liferay.commerce.product.configuration.AttachmentsConfiguration;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPAttachmentFileEntryNameException;
import com.liferay.commerce.product.exception.CPAttachmentFileEntrySizeException;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(
	configurationPid = "com.liferay.commerce.product.configuration.AttachmentsConfiguration",
	property = {
		"javax.portlet.name=" + CPPortletKeys.COMMERCE_CATALOGS,
		"mvc.command.name=/commerce_catalogs/upload_commerce_media_default_image"
	},
	service = MVCActionCommand.class
)
public class UploadCommerceMediaDefaultImageMVCActionCommand
	extends BaseMVCActionCommand {

	public class AttachmentsUploadResponseHandler
		implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			JSONObject jsonObject =
				_itemSelectorUploadResponseHandler.onFailure(
					portletRequest, portalException);

			if (portalException instanceof CPAttachmentFileEntryNameException ||
				portalException instanceof CPAttachmentFileEntrySizeException) {

				String errorMessage = StringPool.BLANK;
				int errorType = 0;

				if (portalException instanceof
						CPAttachmentFileEntryNameException) {

					errorMessage = StringUtil.merge(
						_attachmentsConfiguration.imageExtensions());

					errorType =
						ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION;
				}
				else if (portalException instanceof
							CPAttachmentFileEntrySizeException) {

					errorType = ServletResponseConstants.SC_FILE_SIZE_EXCEPTION;
				}

				jsonObject.put(
					"error",
					JSONUtil.put(
						"errorType", errorType
					).put(
						"message", errorMessage
					));
			}
			else {
				throw portalException;
			}

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

	@Activate
	protected void activate(Map<String, Object> properties) {
		_attachmentsConfiguration = ConfigurableUtil.createConfigurable(
			AttachmentsConfiguration.class, properties);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_commerceMediaDefaultImageUploadFileEntryHandler,
			new AttachmentsUploadResponseHandler(), actionRequest,
			actionResponse);
	}

	private volatile AttachmentsConfiguration _attachmentsConfiguration;

	@Reference
	private CommerceMediaDefaultImageUploadFileEntryHandler
		_commerceMediaDefaultImageUploadFileEntryHandler;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	@Reference
	private UploadHandler _uploadHandler;

}