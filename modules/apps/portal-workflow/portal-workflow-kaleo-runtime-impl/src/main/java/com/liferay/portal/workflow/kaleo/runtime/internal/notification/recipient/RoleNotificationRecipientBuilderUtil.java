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

package com.liferay.portal.workflow.kaleo.runtime.internal.notification.recipient;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationRecipient;
import com.liferay.portal.workflow.kaleo.runtime.util.validator.GroupAwareRoleValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Joao Victor Alves
 */
public class RoleNotificationRecipientBuilderUtil {

	public static void addRoleRecipientAddresses(
			Set<NotificationRecipient> notificationRecipients, Role role,
			NotificationReceptionType notificationReceptionType,
			ExecutionContext executionContext)
		throws Exception {

		List<User> users = _getRoleUsers(role, executionContext);

		for (User user : users) {
			if (user.isActive()) {
				NotificationRecipient notificationRecipient =
					new NotificationRecipient(user, notificationReceptionType);

				notificationRecipients.add(notificationRecipient);
			}
		}
	}

	public static void closeServiceTrackerList() {
		_serviceTrackerList.close();
	}

	public static void setServiceTrackerList(
		ServiceTrackerList<GroupAwareRoleValidator> serviceTrackerList) {

		_serviceTrackerList = serviceTrackerList;
	}

	private static List<Long> _getAncestorGroupIds(Group group, Role role)
		throws Exception {

		List<Long> groupIds = new ArrayList<>();

		for (Group ancestorGroup : group.getAncestors()) {
			if (_isValidGroup(group, role)) {
				groupIds.add(ancestorGroup.getGroupId());
			}
		}

		return groupIds;
	}

	private static List<Long> _getAncestorOrganizationGroupIds(
			Group group, Role role)
		throws Exception {

		List<Long> groupIds = new ArrayList<>();

		OrganizationLocalService organizationLocalService =
			_organizationLocalServiceSnapshot.get();

		Organization organization = organizationLocalService.getOrganization(
			group.getOrganizationId());

		for (Organization ancestorOrganization : organization.getAncestors()) {
			if (_isValidGroup(group, role)) {
				groupIds.add(ancestorOrganization.getGroupId());
			}
		}

		return groupIds;
	}

	private static List<Long> _getGroupIds(long groupId, Role role)
		throws Exception {

		List<Long> groupIds = new ArrayList<>();

		if (groupId != WorkflowConstants.DEFAULT_GROUP_ID) {
			GroupLocalService groupLocalService =
				_groupLocalServiceSnapshot.get();

			Group group = groupLocalService.getGroup(groupId);

			if (group.isOrganization()) {
				groupIds.addAll(_getAncestorOrganizationGroupIds(group, role));
			}

			if (group.isSite()) {
				groupIds.addAll(_getAncestorGroupIds(group, role));
			}

			if (_isValidGroup(group, role)) {
				groupIds.add(groupId);
			}
		}

		return groupIds;
	}

	private static List<User> _getRoleUsers(
			Role role, ExecutionContext executionContext)
		throws Exception {

		long roleId = role.getRoleId();

		UserLocalService userLocalService = _userLocalServiceSnapshot.get();

		if (role.getType() == RoleConstants.TYPE_REGULAR) {
			return userLocalService.getInheritedRoleUsers(
				roleId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		List<Long> groupIds = _getGroupIds(
			kaleoInstanceToken.getGroupId(), role);

		List<User> users = new ArrayList<>();

		for (Long groupId : groupIds) {
			UserGroupRoleLocalService userGroupRoleLocalService =
				_userGroupRoleLocalServiceSnapshot.get();

			List<UserGroupRole> userGroupRoles =
				userGroupRoleLocalService.getUserGroupRolesByGroupAndRole(
					groupId, roleId);

			for (UserGroupRole userGroupRole : userGroupRoles) {
				users.add(userGroupRole.getUser());
			}

			UserGroupGroupRoleLocalService userGroupGroupRoleLocalService =
				_userGroupGroupRoleLocalServiceSnapshot.get();

			List<UserGroupGroupRole> userGroupGroupRoles =
				userGroupGroupRoleLocalService.
					getUserGroupGroupRolesByGroupAndRole(groupId, roleId);

			for (UserGroupGroupRole userGroupGroupRole : userGroupGroupRoles) {
				users.addAll(
					userLocalService.getUserGroupUsers(
						userGroupGroupRole.getUserGroupId()));
			}

			if (Objects.equals(
					role.getName(), DepotRolesConstants.ASSET_LIBRARY_MEMBER) ||
				Objects.equals(role.getName(), RoleConstants.SITE_MEMBER)) {

				users.addAll(
					userLocalService.getGroupUsers(
						groupId, WorkflowConstants.STATUS_APPROVED, null));
			}

			if (Objects.equals(
					role.getName(), RoleConstants.ORGANIZATION_USER)) {

				GroupLocalService groupLocalService =
					_groupLocalServiceSnapshot.get();

				Group group = groupLocalService.getGroup(groupId);

				if (group.isOrganization()) {
					long organizationId = group.getClassPK();

					users.addAll(
						userLocalService.getOrganizationUsers(organizationId));
				}
			}
		}

		return users;
	}

	private static boolean _isValidGroup(Group group, Role role)
		throws Exception {

		if ((group != null) && group.isDepot() &&
			(role.getType() == RoleConstants.TYPE_DEPOT)) {

			return true;
		}
		else if ((group != null) && group.isOrganization() &&
				 (role.getType() == RoleConstants.TYPE_ORGANIZATION)) {

			return true;
		}
		else if ((group != null) && group.isSite() &&
				 (role.getType() == RoleConstants.TYPE_SITE)) {

			return true;
		}

		for (GroupAwareRoleValidator groupAwareRoleValidator :
				_serviceTrackerList) {

			if (groupAwareRoleValidator.isValidGroup(group, role)) {
				return true;
			}
		}

		return false;
	}

	private static final Snapshot<GroupLocalService>
		_groupLocalServiceSnapshot = new Snapshot<>(
			RoleNotificationRecipientBuilderUtil.class,
			GroupLocalService.class);
	private static final Snapshot<OrganizationLocalService>
		_organizationLocalServiceSnapshot = new Snapshot<>(
			RoleNotificationRecipientBuilderUtil.class,
			OrganizationLocalService.class);
	private static ServiceTrackerList<GroupAwareRoleValidator>
		_serviceTrackerList;
	private static final Snapshot<UserGroupGroupRoleLocalService>
		_userGroupGroupRoleLocalServiceSnapshot = new Snapshot<>(
			RoleNotificationRecipientBuilderUtil.class,
			UserGroupGroupRoleLocalService.class);
	private static final Snapshot<UserGroupRoleLocalService>
		_userGroupRoleLocalServiceSnapshot = new Snapshot<>(
			RoleNotificationRecipientBuilderUtil.class,
			UserGroupRoleLocalService.class);
	private static final Snapshot<UserLocalService> _userLocalServiceSnapshot =
		new Snapshot<>(
			RoleNotificationRecipientBuilderUtil.class, UserLocalService.class);

}