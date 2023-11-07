/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membership.policy;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.interval.IntervalActionProcessor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.membershippolicy.BaseSiteMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.MembershipPolicyException;
import com.liferay.portal.kernel.security.membershippolicy.SiteMembershipPolicy;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(service = SiteMembershipPolicy.class)
public class DefaultSiteMembershipPolicy extends BaseSiteMembershipPolicy {

	@Override
	public void checkMembership(
			long[] userIds, long[] addGroupIds, long[] removeGroupIds)
		throws PortalException {

		if (addGroupIds != null) {
			checkAddUsersLimitedGroup(userIds, addGroupIds);
		}
	}

	@Override
	public boolean isMembershipAllowed(long userId, long groupId) {
		try {
			Group group = _groupLocalService.getGroup(groupId);

			if (group.isLimitedToParentSiteMembers() &&
				!_groupLocalService.hasUserGroup(
					userId, group.getParentGroupId(), false)) {

				return false;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return true;
	}

	@Override
	public void propagateMembership(
			long[] userIds, long[] addGroupIds, long[] removeGroupIds)
		throws PortalException {

		if (removeGroupIds != null) {
			for (long removeGroupId : removeGroupIds) {
				removeUsersFromLimitedChildrenGroups(userIds, removeGroupId);
			}
		}
	}

	@Override
	public void verifyPolicy(Group group) throws PortalException {
		if (group.isLimitedToParentSiteMembers()) {
			verifyLimitedParentMembership(group);
		}
	}

	@Override
	public void verifyPolicy(
			Group group, Group oldGroup, List<AssetCategory> oldAssetCategories,
			List<AssetTag> oldAssetTags,
			Map<String, Serializable> oldExpandoAttributes,
			UnicodeProperties oldTypeSettingsUnicodeProperties)
		throws PortalException {

		if (group.isLimitedToParentSiteMembers()) {
			if ((group.getParentGroupId() == oldGroup.getParentGroupId()) &&
				oldGroup.isLimitedToParentSiteMembers()) {

				verifyPolicy(group);
			}
			else {
				List<Group> childrenGroups = getLimitedChildrenGroups(group);

				for (Group childrenGroup : childrenGroups) {
					verifyPolicy(childrenGroup);
				}
			}
		}
	}

	protected void checkAddUsersLimitedGroup(long[] userIds, long[] groupIds)
		throws PortalException {

		MembershipPolicyException membershipPolicyException = null;

		for (long groupId : groupIds) {
			Group group = _groupLocalService.getGroup(groupId);

			if (!group.isLimitedToParentSiteMembers()) {
				continue;
			}

			for (long userId : userIds) {
				if (!_groupLocalService.hasUserGroup(
						userId, group.getParentGroupId(), false)) {

					if (membershipPolicyException == null) {
						membershipPolicyException =
							new MembershipPolicyException(
								MembershipPolicyException.
									SITE_MEMBERSHIP_NOT_ALLOWED);
					}

					membershipPolicyException.addUser(
						_userLocalService.getUser(userId));
				}
			}

			if (membershipPolicyException != null) {
				membershipPolicyException.addGroup(group);
			}
		}

		if (membershipPolicyException != null) {
			throw membershipPolicyException;
		}
	}

	protected List<Group> getLimitedChildrenGroups(Group group)
		throws PortalException {

		List<Group> childrenGroups = _groupLocalService.search(
			group.getCompanyId(), null, StringPool.BLANK,
			LinkedHashMapBuilder.<String, Object>put(
				"groupsTree", ListUtil.fromArray(group)
			).put(
				"membershipRestriction",
				GroupConstants.MEMBERSHIP_RESTRICTION_TO_PARENT_SITE_MEMBERS
			).put(
				"site", Boolean.TRUE
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		List<Group> filteredChildrenGroups = ListUtil.copy(childrenGroups);

		for (Group childrenGroup : childrenGroups) {
			for (Group ancestorGroup : childrenGroup.getAncestors()) {
				if ((ancestorGroup.getGroupId() != group.getGroupId()) &&
					!ancestorGroup.isLimitedToParentSiteMembers()) {

					filteredChildrenGroups.remove(childrenGroup);

					break;
				}
			}
		}

		return filteredChildrenGroups;
	}

	protected void removeUsersFromLimitedChildrenGroups(
			long[] userIds, long groupId)
		throws PortalException {

		List<Group> childrenGroups = getLimitedChildrenGroups(
			_groupLocalService.getGroup(groupId));

		for (Group childrenGroup : childrenGroups) {
			if (!childrenGroup.isLimitedToParentSiteMembers()) {
				continue;
			}

			for (long userId : userIds) {
				_userLocalService.unsetGroupUsers(
					childrenGroup.getGroupId(), new long[] {userId}, null);
			}
		}
	}

	protected void verifyLimitedParentMembership(final Group group)
		throws PortalException {

		int total = _userLocalService.getGroupUsersCount(group.getGroupId());

		final IntervalActionProcessor<Void> intervalActionProcessor =
			new IntervalActionProcessor<>(total);

		intervalActionProcessor.setPerformIntervalActionMethod(
			new IntervalActionProcessor.PerformIntervalActionMethod<Void>() {

				@Override
				public Void performIntervalAction(int start, int end)
					throws PortalException {

					List<User> users = _userLocalService.getGroupUsers(
						group.getGroupId(), start, end);

					for (User user : users) {
						if (!_userLocalService.hasGroupUser(
								group.getParentGroupId(), user.getUserId())) {

							_userLocalService.unsetGroupUsers(
								group.getGroupId(),
								new long[] {user.getUserId()}, null);
						}
						else {
							intervalActionProcessor.incrementStart();
						}
					}

					return null;
				}

			});

		intervalActionProcessor.performIntervalActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultSiteMembershipPolicy.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}