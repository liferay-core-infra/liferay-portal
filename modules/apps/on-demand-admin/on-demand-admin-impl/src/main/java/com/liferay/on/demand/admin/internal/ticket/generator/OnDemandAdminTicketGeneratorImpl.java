/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.internal.ticket.generator;

import com.liferay.on.demand.admin.constants.OnDemandAdminActionKeys;
import com.liferay.on.demand.admin.constants.OnDemandAdminConstants;
import com.liferay.on.demand.admin.constants.OnDemandAdminPortletKeys;
import com.liferay.on.demand.admin.internal.configuration.OnDemandAdminConfiguration;
import com.liferay.on.demand.admin.ticket.generator.OnDemandAdminTicketGenerator;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.util.PortalInstances;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = OnDemandAdminTicketGenerator.class)
public class OnDemandAdminTicketGeneratorImpl
	implements OnDemandAdminTicketGenerator {

	@Override
	public Ticket generate(
			Company company, String justification, long requestorUserId)
		throws PortalException {

		_checkRequestAdministratorAccessPermission(
			company.getCompanyId(), requestorUserId);

		User requestorUser = _userLocalService.getUser(requestorUserId);

		User user = _addOnDemandAdminUser(company, requestorUser);

		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			OnDemandAdminConstants.
				AUDIT_EVENT_TYPE_ON_DEMAND_ADMIN_TICKET_GENERATED,
			User.class.getName(), requestorUserId, null);

		auditMessage.setAdditionalInfo(
			JSONUtil.put(
				"justification", justification
			).put(
				"requestedCompanyId", company.getCompanyId()
			).put(
				"requestedCompanyWebId", company.getWebId()
			));

		_auditRouter.route(auditMessage);

		OnDemandAdminConfiguration onDemandAdminConfiguration =
			_configurationProvider.getSystemConfiguration(
				OnDemandAdminConfiguration.class);

		int expirationTime =
			onDemandAdminConfiguration.authenticationTokenExpirationTime();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setWithSafeCloseable(
					company.getCompanyId())) {

			return _ticketLocalService.addDistinctTicket(
				user.getCompanyId(), User.class.getName(), user.getUserId(),
				TicketConstants.TYPE_ON_DEMAND_ADMIN_LOGIN, justification,
				new Date(
					System.currentTimeMillis() +
						TimeUnit.MINUTES.toMillis(expirationTime)),
				null);
		}
	}

	private User _addOnDemandAdminUser(Company company, User requestorUser)
		throws PortalException {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setWithSafeCloseable(
					company.getCompanyId())) {

			String password = PwdGenerator.getPassword(20);

			String screenName = _getScreenName(requestorUser.getUserId(), 0);

			String emailAddress = screenName + StringPool.AT + company.getMx();

			Date date = new Date();
			Role role = _roleLocalService.getRole(
				company.getCompanyId(), RoleConstants.ADMINISTRATOR);

			User user = _userLocalService.addUser(
				0, company.getCompanyId(), false, password, password, true,
				null, emailAddress, requestorUser.getLocale(),
				requestorUser.getFirstName(), requestorUser.getMiddleName(),
				requestorUser.getLastName(), 0, 0, requestorUser.isMale(),
				date.getMonth(), date.getDay(), date.getYear(), null,
				UserConstants.TYPE_REGULAR, null, null,
				new long[] {role.getRoleId()}, null, false,
				new ServiceContext());

			screenName = _getScreenName(
				requestorUser.getUserId(), user.getUserId());

			user.setScreenName(screenName);
			user.setEmailAddress(screenName + StringPool.AT + company.getMx());

			user.setEmailAddressVerified(true);

			return _userLocalService.updateUser(user);
		}
	}

	private void _checkRequestAdministratorAccessPermission(
			long companyId, long userId)
		throws PortalException {

		if (companyId == PortalInstances.getDefaultCompanyId()) {
			throw new PrincipalException(
				"Target company must not be the default company");
		}

		User user = _userLocalService.getUser(userId);

		if (user.getCompanyId() != PortalInstances.getDefaultCompanyId()) {
			throw new PrincipalException(
				"Request can only be made from the default company");
		}

		if (!PortletPermissionUtil.contains(
				PermissionCheckerFactoryUtil.create(user),
				GroupConstants.DEFAULT_LIVE_GROUP_ID,
				LayoutConstants.DEFAULT_PLID,
				OnDemandAdminPortletKeys.ON_DEMAND_ADMIN,
				OnDemandAdminActionKeys.REQUEST_ADMINISTRATOR_ACCESS, true)) {

			throw new PrincipalException.MustHavePermission(
				userId, OnDemandAdminActionKeys.REQUEST_ADMINISTRATOR_ACCESS);
		}
	}

	private String _getScreenName(long requestorUserId, long userId)
		throws PortalException {

		return StringBundler.concat(
			OnDemandAdminConstants.SCREEN_NAME_PREFIX_ON_DEMAND_ADMIN,
			StringPool.UNDERLINE, requestorUserId, StringPool.UNDERLINE,
			userId);
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserLocalService _userLocalService;

}