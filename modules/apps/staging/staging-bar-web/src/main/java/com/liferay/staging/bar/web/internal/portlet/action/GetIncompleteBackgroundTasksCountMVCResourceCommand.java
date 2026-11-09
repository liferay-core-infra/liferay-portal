/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.portlet.action;

import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.bar.web.internal.portlet.constants.StagingBarPortletKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Debora Buriti
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StagingBarPortletKeys.STAGING_BAR,
		"mvc.command.name=/staging_bar/get_incomplete_background_tasks_count"
	},
	service = MVCResourceCommand.class
)
public class GetIncompleteBackgroundTasksCountMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isShowStagingIcon()) {
			throw new PrincipalException();
		}

		Layout layout = themeDisplay.getLayout();

		Group liveGroup = _staging.getLiveGroup(layout.getGroupId());

		int incompleteBackgroundTasksCount = 0;

		if (liveGroup != null) {
			incompleteBackgroundTasksCount =
				_backgroundTaskManager.getBackgroundTasksCount(
					liveGroup.getGroupId(),
					BackgroundTaskExecutorNames.
						LAYOUT_STAGING_BACKGROUND_TASK_EXECUTOR,
					false);
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, incompleteBackgroundTasksCount);
	}

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private Staging _staging;

}