/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membershippolicy;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.membershippolicy.UserGroupMembershipPolicy;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Roberto Díaz
 */
public class UserGroupMembershipPolicyFactoryUtil {

	public static UserGroupMembershipPolicy getUserGroupMembershipPolicy() {
		ServiceTracker<UserGroupMembershipPolicy, UserGroupMembershipPolicy>
			serviceTracker = _serviceTrackerDCLSingleton.getSingleton(
				UserGroupMembershipPolicyFactoryUtil::_createServiceTracker);

		return serviceTracker.getService();
	}

	private static ServiceTracker
		<UserGroupMembershipPolicy, UserGroupMembershipPolicy>
			_createServiceTracker() {

		ServiceTracker<UserGroupMembershipPolicy, UserGroupMembershipPolicy>
			serviceTracker = new ServiceTracker<>(
				_bundleContext, UserGroupMembershipPolicy.class,
				new UserGroupMembershipPolicyTrackerCustomizer());

		serviceTracker.open();

		return serviceTracker;
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final DCLSingleton
		<ServiceTracker<UserGroupMembershipPolicy, UserGroupMembershipPolicy>>
			_serviceTrackerDCLSingleton = new DCLSingleton<>();

	private static class UserGroupMembershipPolicyTrackerCustomizer
		implements ServiceTrackerCustomizer
			<UserGroupMembershipPolicy, UserGroupMembershipPolicy> {

		@Override
		public UserGroupMembershipPolicy addingService(
			ServiceReference<UserGroupMembershipPolicy> serviceReference) {

			return _bundleContext.getService(serviceReference);
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