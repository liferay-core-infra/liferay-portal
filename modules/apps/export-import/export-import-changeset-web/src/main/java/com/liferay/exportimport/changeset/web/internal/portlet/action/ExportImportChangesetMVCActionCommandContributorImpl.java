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

package com.liferay.exportimport.changeset.web.internal.portlet.action;

import com.liferay.exportimport.changeset.Changeset;
import com.liferay.exportimport.changeset.ChangesetManager;
import com.liferay.exportimport.changeset.constants.ChangesetPortletKeys;
import com.liferay.exportimport.changeset.portlet.action.ExportImportChangesetMVCActionCommandContributor;
import com.liferay.portal.kernel.util.Constants;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(
	property = {
		"javax.portlet.name=" + ChangesetPortletKeys.CHANGESET,
		"mvc.command.name=/export_import_changeset/export_import_changeset"
	},
	service = ExportImportChangesetMVCActionCommandContributor.class
)
public class ExportImportChangesetMVCActionCommandContributorImpl
	extends ExportImportChangesetMVCActionCommand
	implements ExportImportChangesetMVCActionCommandContributor {

	@Override
	public void publish(
			ActionRequest actionRequest, ActionResponse actionResponse,
			Changeset changeset)
		throws Exception {

		_changesetManager.addChangeset(changeset);

		processExportAndPublishAction(
			actionRequest, actionResponse, Constants.PUBLISH,
			changeset.getUuid());
	}

	@Reference
	private ChangesetManager _changesetManager;

}