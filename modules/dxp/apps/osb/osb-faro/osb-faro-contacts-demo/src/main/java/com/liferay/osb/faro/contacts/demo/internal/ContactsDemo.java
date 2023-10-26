/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal;

import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.contacts.demo.internal.util.HeadersUtil;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Channel;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.LCPProject;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.concurrent.FutureTask;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(service = {})
public class ContactsDemo {

	@Activate
	protected void activate() {
		if (Validator.isBlank(FaroPropsValues.FARO_DEMO_CREATOR_METHOD) ||
			StringUtil.equals(
				FaroPropsValues.FARO_DEMO_CREATOR_METHOD, "none")) {

			if (_log.isDebugEnabled()) {
				_log.debug("Skip demo data creation");
			}

			return;
		}

		_initializeDemoCreatorServices();

		_futureTask = new FutureTask<>(
			() -> {
				long startTime = System.currentTimeMillis();

				while ((System.currentTimeMillis() - startTime) <
							(Time.MINUTE * 5)) {

					try {
						FaroProject faroProject =
							_faroProjectLocalService.createFaroProject(0);

						faroProject.setWeDeployKey(
							FaroPropsValues.FARO_DEFAULT_WE_DEPLOY_KEY);

						break;
					}
					catch (Exception exception) {
						_log.error(exception);

						Thread.sleep(Time.SECOND * 30);
					}
				}

				_createDemo();

				if (_log.isInfoEnabled()) {
					_log.info("Completed demo data creation");
				}

				return null;
			});

		Thread thread = new Thread(
			_futureTask, "Contacts Demo Creation Thread");

		thread.setDaemon(true);

		thread.start();
	}

	@Deactivate
	protected void deactivate() {
		if (_futureTask != null) {
			_futureTask.cancel(true);
		}
	}

	private void _createDemo() throws Exception {
		try {
			_createUsers(_faroProject);
		}
		catch (Exception exception) {
			_log.error("Unable to add users", exception);
		}

		if (_hasExistingData()) {
			if (_log.isInfoEnabled()) {
				_log.info("Skipping demo creation because of existing data");
			}

			return;
		}

		if (StringUtil.equals(
				FaroPropsValues.FARO_DEMO_CREATOR_METHOD, "nanite")) {

			_naniteDemoCreatorService.createData();
		}
		else {
			_snapshotDemoCreatorService.createData();
		}

		_createFaroChannels();
	}

	private void _createFaroChannels() throws Exception {
		Results<Channel> results = _contactsEngineClient.getChannels(
			_faroProject, 0, 10000, null, null);

		User user = _userLocalService.getUserByEmailAddress(
			_portal.getDefaultCompanyId(), "test@liferay.com");

		for (Channel channel : results.getItems()) {
			_faroChannelLocalService.addFaroChannel(
				user.getUserId(), channel.getName(), channel.getId(),
				_faroProject.getGroupId());
		}
	}

	private FaroProject _createFaroProject() throws Exception {
		Http.Options options = new Http.Options();

		options.addPart("corpProjectUuid", FaroPropsValues.FARO_PROJECT_ID);
		options.addPart("name", FaroPropsValues.FARO_PROJECT_ID);
		options.addPart("ownerEmailAddress", "test@liferay.com");
		options.addPart("serverLocation", LCPProject.Cluster.US.toString());
		options.addPart("timeZoneId", "UTC");
		options.setHeaders(HeadersUtil.getHeaders());
		options.setLocation(
			"http://localhost:8080/o/faro/main/project/provisioned");
		options.setPost(true);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			_http.URLtoString(options));

		FaroProject faroProject =
			_faroProjectLocalService.getFaroProjectByGroupId(
				jsonObject.getLong("groupId"));

		faroProject.setState(FaroProjectConstants.STATE_NOT_READY);

		return _faroProjectLocalService.updateFaroProject(faroProject);
	}

	private void _createUsers(FaroProject faroProject) throws Exception {
		for (String[] userInfo : _USER_INFO) {
			String screenName = userInfo[0];

			String emailAddress = screenName.concat("@faro.io");

			User user = _userLocalService.fetchUserByEmailAddress(
				_portal.getDefaultCompanyId(), emailAddress);

			if (user != null) {
				continue;
			}

			String[] screenNameParts = StringUtil.split(
				screenName, StringPool.PERIOD);

			String firstName = screenNameParts[0];

			String lastName = null;

			if (screenNameParts.length > 1) {
				lastName = screenNameParts[1];
			}
			else {
				lastName = screenNameParts[0];
			}

			user = _userLocalService.addUserWithWorkflow(
				UserConstants.USER_ID_DEFAULT, _portal.getDefaultCompanyId(),
				false, "test", "test", true, screenName, emailAddress,
				LocaleUtil.US, firstName, null, lastName, 0, 0, true, 1, 1,
				1970, null, UserConstants.TYPE_GUEST, null, null, null, null,
				false, null);

			user.setPasswordReset(false);
			user.setPasswordModifiedDate(new Date());
			user.setLastLoginDate(new Date());
			user.setAgreedToTermsOfUse(true);

			user = _userLocalService.updateUser(user);

			Role role = _roleLocalService.getRole(
				_portal.getDefaultCompanyId(), userInfo[1]);

			_faroUserLocalService.addFaroUser(
				user.getUserId(), faroProject.getGroupId(), user.getUserId(),
				role.getRoleId(), emailAddress,
				FaroUserConstants.STATUS_APPROVED, false);
		}
	}

	private boolean _hasExistingData() {
		Results<Individual> individuals = _contactsEngineClient.getIndividuals(
			_faroProject, (String)null, false, 1, 0, null);

		if (individuals.getTotal() > 0) {
			return true;
		}

		return false;
	}

	private void _initializeDemoCreatorServices() {
		_faroProject =
			_faroProjectLocalService.fetchFaroProjectByCorpProjectUuid(
				FaroPropsValues.FARO_PROJECT_ID);

		if (_faroProject == null) {
			try {
				_faroProject = _createFaroProject();

				_naniteDemoCreatorService = new NaniteDemoCreatorService(
					_contactsEngineClient, _faroProject);

				_snapshotDemoCreatorService = new SnapshotDemoCreatorService(
					_contactsEngineClient, _faroProject);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to get Faro project" + exception);
				}
			}
		}
	}

	private static final String[][] _USER_INFO = {
		{"bryan.cheung", RoleConstants.SITE_OWNER},
		{"corbin.murakami", RoleConstants.SITE_MEMBER},
		{"michelle.hoshi", RoleConstants.SITE_ADMINISTRATOR},
		{"test", RoleConstants.SITE_OWNER}
	};

	private static final Log _log = LogFactoryUtil.getLog(ContactsDemo.class);

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

	private FaroProject _faroProject;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private FaroUserLocalService _faroUserLocalService;

	private FutureTask<Void> _futureTask;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	private NaniteDemoCreatorService _naniteDemoCreatorService;

	@Reference
	private Portal _portal;

	@Reference(target = "(javax.portlet.name=faro_portlet)")
	private Portlet _portlet;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SnapshotDemoCreatorService _snapshotDemoCreatorService;

	@Reference
	private UserLocalService _userLocalService;

}