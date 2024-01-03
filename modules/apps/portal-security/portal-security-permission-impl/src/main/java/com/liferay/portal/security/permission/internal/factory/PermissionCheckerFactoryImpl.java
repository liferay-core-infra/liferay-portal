/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.internal.factory;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.contributor.RoleContributor;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapperFactory;
import com.liferay.portal.security.permission.StagingPermissionChecker;
import com.liferay.portal.security.permission.internal.configuration.PermissionCheckerConfiguration;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Charles May
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
@Component(
	configurationPid = "com.liferay.portal.security.permission.internal.configuration.PermissionCheckerConfiguration",
	service = PermissionCheckerFactory.class
)
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

	@Override
	public PermissionChecker getPermissionChecker() {
		return _permissionChecker.clone();
	}

	@Activate
	protected void activate(
			BundleContext bundleContext, Map<String, Object> properties)
		throws Exception {

		modified(properties);

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

	@Modified
	protected void modified(Map<String, Object> properties) throws Exception {
		PermissionCheckerConfiguration permissionCheckerConfiguration =
			ConfigurableUtil.createConfigurable(
				PermissionCheckerConfiguration.class, properties);

		Class<PermissionChecker> clazz =
			(Class<PermissionChecker>)Class.forName(
				permissionCheckerConfiguration.permissionChecker());

		_permissionChecker = clazz.newInstance();
	}

	private volatile PermissionChecker _permissionChecker;
	private ServiceTrackerList<PermissionCheckerWrapperFactory>
		_permissionCheckerWrapperFactories;
	private ServiceTrackerList<RoleContributor> _roleContributors;

}