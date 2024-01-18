/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membershippolicy;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.RoleMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.SiteMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.UserGroupMembershipPolicy;
import com.liferay.portal.util.PropsValues;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Janis Zhang
 */
public class MembershipPolicyUtil {

	public static OrganizationMembershipPolicy
		getOrganizationMembershipPolicy() {

		return _organizationMembershipPolicySnapshot.get();
	}

	public static RoleMembershipPolicy getRoleMembershipPolicy() {
		ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>
			serviceTracker =
				_roleMembershipPolicyTrackerDCLSingleton.getSingleton(
					MembershipPolicyUtil::
						_createRoleMembershipPolicyServiceTracker);

		return serviceTracker.getService();
	}

	public static SiteMembershipPolicy getSiteMembershipPolicy() {
		return _siteMembershipPolicySnapshot.get();
	}

	public static UserGroupMembershipPolicy getUserGroupMembershipPolicy() {
		ServiceTracker<UserGroupMembershipPolicy, UserGroupMembershipPolicy>
			serviceTracker =
				_userGroupMembershipPolicyTrackerDCLSingleton.getSingleton(
					MembershipPolicyUtil::
						_createUserGroupMembershipPolicyTracker);

		return serviceTracker.getService();
	}

	private static ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>
		_createRoleMembershipPolicyServiceTracker() {

		ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>
			serviceTracker = new ServiceTracker<>(
				_bundleContext, RoleMembershipPolicy.class,
				new RoleMembershipPolicyTrackerCustomizer());

		serviceTracker.open();

		return serviceTracker;
	}

	private static ServiceTracker
		<UserGroupMembershipPolicy, UserGroupMembershipPolicy>
			_createUserGroupMembershipPolicyTracker() {

		ServiceTracker<UserGroupMembershipPolicy, UserGroupMembershipPolicy>
			serviceTracker = new ServiceTracker<>(
				_bundleContext, UserGroupMembershipPolicy.class,
				new UserGroupMembershipPolicyTrackerCustomizer());

		serviceTracker.open();

		return serviceTracker;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MembershipPolicyUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final Snapshot<OrganizationMembershipPolicy>
		_organizationMembershipPolicySnapshot = new Snapshot<>(
			MembershipPolicyUtil.class, OrganizationMembershipPolicy.class,
			null, true);
	private static final DCLSingleton
		<ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>>
			_roleMembershipPolicyTrackerDCLSingleton = new DCLSingleton<>();
	private static final Snapshot<SiteMembershipPolicy>
		_siteMembershipPolicySnapshot = new Snapshot<>(
			MembershipPolicyUtil.class, SiteMembershipPolicy.class, null, true);
	private static final DCLSingleton
		<ServiceTracker<UserGroupMembershipPolicy, UserGroupMembershipPolicy>>
			_userGroupMembershipPolicyTrackerDCLSingleton =
				new DCLSingleton<>();

	private static class RoleMembershipPolicyTrackerCustomizer
		implements ServiceTrackerCustomizer
			<RoleMembershipPolicy, RoleMembershipPolicy> {

		@Override
		public RoleMembershipPolicy addingService(
			ServiceReference<RoleMembershipPolicy> serviceReference) {

			RoleMembershipPolicy roleMembershipPolicy =
				_bundleContext.getService(serviceReference);

			if (PropsValues.MEMBERSHIP_POLICY_AUTO_VERIFY) {
				try {
					roleMembershipPolicy.verifyPolicy();
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}
			}

			return roleMembershipPolicy;
		}

		@Override
		public void modifiedService(
			ServiceReference<RoleMembershipPolicy> serviceReference,
			RoleMembershipPolicy roleMembershipPolicy) {
		}

		@Override
		public void removedService(
			ServiceReference<RoleMembershipPolicy> serviceReference,
			RoleMembershipPolicy roleMembershipPolicy) {

			_bundleContext.ungetService(serviceReference);
		}

	}

	private static class UserGroupMembershipPolicyTrackerCustomizer
		implements ServiceTrackerCustomizer
			<UserGroupMembershipPolicy, UserGroupMembershipPolicy> {

		@Override
		public UserGroupMembershipPolicy addingService(
			ServiceReference<UserGroupMembershipPolicy> serviceReference) {

			UserGroupMembershipPolicy userGroupMembershipPolicy =
				_bundleContext.getService(serviceReference);

			if (PropsValues.MEMBERSHIP_POLICY_AUTO_VERIFY) {
				try {
					userGroupMembershipPolicy.verifyPolicy();
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}
			}

			return userGroupMembershipPolicy;
		}

		@Override
		public void modifiedService(
			ServiceReference<UserGroupMembershipPolicy> serviceReference,
			UserGroupMembershipPolicy userGroupMembershipPolicy) {
		}

		@Override
		public void removedService(
			ServiceReference<UserGroupMembershipPolicy> serviceReference,
			UserGroupMembershipPolicy userGroupMembershipPolicy) {

			_bundleContext.ungetService(serviceReference);
		}

	}

}