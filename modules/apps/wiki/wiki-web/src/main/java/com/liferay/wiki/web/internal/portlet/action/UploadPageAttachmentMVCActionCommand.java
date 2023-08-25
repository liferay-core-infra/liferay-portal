/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.upload.UploadHandler;
import com.liferay.wiki.configuration.WikiFileUploadConfiguration;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.web.internal.upload.PageAttachmentWikiUploadFileEntryHandler;
import com.liferay.wiki.web.internal.upload.PageAttachmentWikiUploadResponseHandler;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.wiki.configuration.WikiFileUploadConfiguration",
	property = {
		"javax.portlet.name=" + WikiPortletKeys.WIKI,
		"mvc.command.name=/wiki/image_editor",
		"mvc.command.name=/wiki/upload_page_attachment"
	},
	service = MVCActionCommand.class
)
public class UploadPageAttachmentMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_pageAttachmentWikiUploadFileEntryHandler =
			new PageAttachmentWikiUploadFileEntryHandler(
				_wikiFileUploadConfiguration, _wikiNodeModelResourcePermission);

		_wikiFileUploadConfiguration = ConfigurableUtil.createConfigurable(
			WikiFileUploadConfiguration.class, properties);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_pageAttachmentWikiUploadFileEntryHandler,
			new PageAttachmentWikiUploadResponseHandler(
				_itemSelectorUploadResponseHandler),
			actionRequest, actionResponse);
	}

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	private volatile PageAttachmentWikiUploadFileEntryHandler
		_pageAttachmentWikiUploadFileEntryHandler;

	@Reference
	private UploadHandler _uploadHandler;

	private volatile WikiFileUploadConfiguration _wikiFileUploadConfiguration;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiNode)")
	private ModelResourcePermission<WikiNode> _wikiNodeModelResourcePermission;

}