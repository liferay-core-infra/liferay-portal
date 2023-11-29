/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.internal.factory;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.contributor.RoleContributor;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapperFactory;
import com.liferay.portal.security.permission.StagingPermissionChecker;
import com.liferay.portal.util.PropsValues;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Charles May
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
@Component(service = PermissionCheckerFactory.class)
public class PermissionCheckerFactoryImpl implements PermissionCheckerFactory {

	@Override
	public PermissionChecker create(User user) {
		PermissionChecker permissionChecker = _permissionChecker.clone();

		permissionChecker.init(
			user, _roleContributors.toArray(new RoleContributor[0]));

		permissionChecker = new StagingPermissionChecker(permissionChecker);

		for (PermissionCheckerWrapperFactory permissionCheckerWrapperFactory :
				_permissionCheckerWrapperFactories) {

			permissionChecker =
				permissionCheckerWrapperFactory.wrapPermissionChecker(
					permissionChecker);
		}

		return permissionChecker;
	}

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		Class<PermissionChecker> clazz =
			(Class<PermissionChecker>)Class.forName(
				PropsValues.PERMISSIONS_CHECKER);

		_permissionChecker = clazz.newInstance();

		_permissionCheckerWrapperFactories = ServiceTrackerListFactory.open(
			bundleContext, PermissionCheckerWrapperFactory.class);
		_roleContributors = ServiceTrackerListFactory.open(
			bundleContext, RoleContributor.class);
	}

	@Deactivate
	protected void deactivate() {
		_permissionCheckerWrapperFactories.close();
		_roleContributors.close();
	}

	private PermissionChecker _permissionChecker;
	private ServiceTrackerList<PermissionCheckerWrapperFactory>
		_permissionCheckerWrapperFactories;
	private ServiceTrackerList<RoleContributor> _roleContributors;

}