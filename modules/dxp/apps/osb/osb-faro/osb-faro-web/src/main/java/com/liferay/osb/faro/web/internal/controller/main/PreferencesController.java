/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.osb.faro.service.FaroPreferencesLocalService;
import com.liferay.osb.faro.web.internal.constants.FaroPreferencesConstants;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.contacts.FaroPreferencesDisplay;
import com.liferay.osb.faro.web.internal.model.preferences.DistributionCardTabPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.DistributionCardTabsPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.EmailReportPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.IndividualDashboardPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.IndividualSegmentPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.WorkspacePreferences;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.util.EmailReportHelper;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.osb.faro.web.internal.util.PreferencesControllerUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Map;

import javax.annotation.security.RolesAllowed;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = {FaroController.class, PreferencesController.class})
@Path("/{groupId}/preferences")
@Produces(MediaType.APPLICATION_JSON)
public class PreferencesController extends BaseFaroController {

	@Path("/distribution_tabs")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public DistributionCardTabsPreferences addDistributionTabPreferences(
			@PathParam("groupId") long groupId,
			@FormParam("distributionCardTabPreferences") FaroParam
				<Map<String, Object>> distributionCardTabPreferencesFaroParam,
			@FormParam("individualSegmentId") String individualSegmentId,
			@FormParam("distributionTabId") String distributionTabId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@FormParam("scope")
			String scope)
		throws Exception {

		long ownerId = PreferencesControllerUtil.getOwnerId(
			groupId, scope, getUserId());

		WorkspacePreferences workspacePreferences =
			PreferencesControllerUtil.getWorkspacePreferences(
				_faroPreferencesLocalService, groupId, ownerId);

		workspacePreferences.addDistributionCardTabPreferences(
			distributionTabId, individualSegmentId,
			JSONUtil.convertValue(
				distributionCardTabPreferencesFaroParam.getValue(),
				DistributionCardTabPreferences.class));

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));

		return workspacePreferences.getDistributionCardTabsPreferences(
			individualSegmentId);
	}

	@Path("/email_report")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public EmailReportPreferences addEmailReportPreference(
			@PathParam("groupId") long groupId,
			@FormParam("channelId") String channelId,
			@FormParam("enabled") Boolean enabled,
			@FormParam("frequency") String frequency,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@FormParam("scope")
			String scope)
		throws Exception {

		long ownerId = PreferencesControllerUtil.getOwnerId(
			groupId, scope, getUserId());

		WorkspacePreferences workspacePreferences =
			PreferencesControllerUtil.getWorkspacePreferences(
				_faroPreferencesLocalService, groupId, ownerId);

		Map<String, EmailReportPreferences> emailReportPreferences =
			workspacePreferences.addEmailReportPreference(
				channelId, enabled, frequency);

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));

		return emailReportPreferences.get(channelId);
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroPreferencesDisplay get(
			@PathParam("groupId") long groupId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		long ownerId = PreferencesControllerUtil.getOwnerId(
			groupId, scope, getUserId());

		FaroPreferences faroPreferences =
			_faroPreferencesLocalService.fetchFaroPreferences(groupId, ownerId);

		if (faroPreferences == null) {
			return new FaroPreferencesDisplay(groupId, ownerId);
		}

		return new FaroPreferencesDisplay(faroPreferences);
	}

	@GET
	@Path("/default_channel_id")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public Map<String, String> getDefaultChannelId(
			@PathParam("groupId") long groupId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		WorkspacePreferences workspacePreferences =
			PreferencesControllerUtil.getWorkspacePreferences(
				_faroPreferencesLocalService, groupId,
				PreferencesControllerUtil.getOwnerId(
					groupId, scope, getUserId()));

		return Collections.singletonMap(
			"defaultChannelId", workspacePreferences.getDefaultChannelId());
	}

	@GET
	@Path("/distribution_tabs")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public DistributionCardTabsPreferences getDistributionTabsPreferences(
			@PathParam("groupId") long groupId,
			@QueryParam("individualSegmentId") String individualSegmentId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		WorkspacePreferences workspacePreferences =
			PreferencesControllerUtil.getWorkspacePreferences(
				_faroPreferencesLocalService, groupId,
				PreferencesControllerUtil.getOwnerId(
					groupId, scope, getUserId()));

		if (Validator.isNull(individualSegmentId)) {
			IndividualDashboardPreferences individualDashboardPreferences =
				workspacePreferences.getIndividualDashboardPreferences();

			return individualDashboardPreferences.
				getDistributionCardTabsPreferences();
		}

		Map<String, IndividualSegmentPreferences>
			individualSegmentPreferencesMap =
				workspacePreferences.getIndividualSegmentPreferences();

		IndividualSegmentPreferences individualSegmentPreferences =
			individualSegmentPreferencesMap.getOrDefault(
				individualSegmentId, new IndividualSegmentPreferences());

		return individualSegmentPreferences.
			getDistributionCardTabsPreferences();
	}

	@GET
	@Path("/email_report")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public Map<String, EmailReportPreferences> getEmailReportPreferences(
			@PathParam("groupId") long groupId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		WorkspacePreferences workspacePreferences =
			PreferencesControllerUtil.getWorkspacePreferences(
				_faroPreferencesLocalService, groupId,
				PreferencesControllerUtil.getOwnerId(
					groupId, scope, getUserId()));

		return workspacePreferences.getEmailReportPreferences(null);
	}

	@GET
	@Path("/upgrade_modal_seen")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public boolean isUpgradeModalSeen(
			@PathParam("groupId") long groupId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		WorkspacePreferences workspacePreferences =
			PreferencesControllerUtil.getWorkspacePreferences(
				_faroPreferencesLocalService, groupId,
				PreferencesControllerUtil.getOwnerId(
					groupId, scope, getUserId()));

		return workspacePreferences.isUpgradeModalSeen();
	}

	@DELETE
	@Path("/distribution_tabs")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public DistributionCardTabsPreferences removeDistributionTabPreferences(
			@PathParam("groupId") long groupId,
			@FormParam("individualSegmentId") String individualSegmentId,
			@FormParam("distributionTabId") String distributionTabId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@FormParam("scope")
			String scope)
		throws Exception {

		long ownerId = PreferencesControllerUtil.getOwnerId(
			groupId, scope, getUserId());

		WorkspacePreferences workspacePreferences =
			PreferencesControllerUtil.getWorkspacePreferences(
				_faroPreferencesLocalService, groupId, ownerId);

		workspacePreferences.removeDistributionCardTabPreferences(
			distributionTabId, individualSegmentId);

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));

		return workspacePreferences.getDistributionCardTabsPreferences(
			individualSegmentId);
	}

	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroPreferencesDisplay save(
			@PathParam("groupId") long groupId,
			@FormParam("preferences") FaroParam<Map<String, Object>>
				preferencesFaroParam,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@FormParam("scope")
			String scope)
		throws Exception {

		return new FaroPreferencesDisplay(
			_faroPreferencesLocalService.savePreferences(
				getUserId(), groupId,
				PreferencesControllerUtil.getOwnerId(
					groupId, scope, getUserId()),
				JSONUtil.writeValueAsString(preferencesFaroParam.getValue())));
	}

	@Path("/default_channel_id")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public Map<String, String> saveDefaultChannelId(
			@PathParam("groupId") long groupId,
			@FormParam("defaultChannelId") String defaultChannelId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@FormParam("scope")
			String scope)
		throws Exception {

		long ownerId = PreferencesControllerUtil.getOwnerId(
			groupId, scope, getUserId());

		WorkspacePreferences workspacePreferences =
			PreferencesControllerUtil.getWorkspacePreferences(
				_faroPreferencesLocalService, groupId, ownerId);

		workspacePreferences.setDefaultChannelId(defaultChannelId);

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));

		return Collections.singletonMap("defaultChannelId", defaultChannelId);
	}

	@Path("/upgrade_modal_seen")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public boolean saveUpgradeModalSeen(
			@PathParam("groupId") long groupId,
			@FormParam("upgradeModalSeen") Boolean upgradeModalSeen,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		long ownerId = PreferencesControllerUtil.getOwnerId(
			groupId, scope, getUserId());

		WorkspacePreferences workspacePreferences =
			PreferencesControllerUtil.getWorkspacePreferences(
				_faroPreferencesLocalService, groupId, ownerId);

		workspacePreferences.setUpgradeModalSeen(upgradeModalSeen);

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));

		return upgradeModalSeen;
	}

	@Path("/send_email_report")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public boolean sendEmailReport(
			@PathParam("groupId") long groupId,
			@FormParam("channelId") String channelId,
			@FormParam("frequency") String frequency,
			@FormParam("userId") long userId)
		throws Exception {

		_emailReportHelper.sendEmail(channelId, frequency, groupId, userId);

		return true;
	}

	@Reference
	private EmailReportHelper _emailReportHelper;

	@Reference
	private FaroPreferencesLocalService _faroPreferencesLocalService;

}