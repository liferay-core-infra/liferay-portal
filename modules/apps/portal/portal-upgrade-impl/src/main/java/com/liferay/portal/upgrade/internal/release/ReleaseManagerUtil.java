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

package com.liferay.portal.upgrade.internal.release;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.upgrade.internal.graph.ReleaseGraphManager;
import com.liferay.portal.upgrade.internal.registry.UpgradeInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Joao Victor Alves
 */
public class ReleaseManagerUtil {

	public static void closeServiceTrackerMap() {
		_serviceTrackerMap.close();
	}

	public static Set<String> getBundleSymbolicNames() {
		return new HashSet<>(_serviceTrackerMap.keySet());
	}

	public static String getSchemaVersionString(String bundleSymbolicName) {
		ReleaseLocalService releaseLocalService =
			_releaseLocalServiceSnapshot.get();

		Release release = releaseLocalService.fetchRelease(bundleSymbolicName);

		if ((release != null) &&
			Validator.isNotNull(release.getSchemaVersion())) {

			return release.getSchemaVersion();
		}

		return "0.0.0";
	}

	public static Set<String> getUpgradableBundleSymbolicNames() {
		Set<String> upgradableBundleSymbolicNames = new HashSet<>();

		for (String bundleSymbolicName : getBundleSymbolicNames()) {
			if (isUpgradable(bundleSymbolicName)) {
				upgradableBundleSymbolicNames.add(bundleSymbolicName);
			}
		}

		return upgradableBundleSymbolicNames;
	}

	public static List<UpgradeInfo> getUpgradeInfos(String bundleSymbolicName) {
		return _serviceTrackerMap.getService(bundleSymbolicName);
	}

	public static boolean isUpgradable(String bundleSymbolicName) {
		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			getUpgradeInfos(bundleSymbolicName));

		List<List<UpgradeInfo>> upgradeInfosList =
			releaseGraphManager.getUpgradeInfosList(
				getSchemaVersionString(bundleSymbolicName));

		if (upgradeInfosList.size() == 1) {
			return true;
		}

		return false;
	}

	public static void setServiceTrackerMap(
		ServiceTrackerMap<String, List<UpgradeInfo>> serviceTrackerMap) {

		_serviceTrackerMap = serviceTrackerMap;
	}

	private static final Snapshot<ReleaseLocalService>
		_releaseLocalServiceSnapshot = new Snapshot<>(
			ReleaseManagerUtil.class, ReleaseLocalService.class);
	private static ServiceTrackerMap<String, List<UpgradeInfo>>
		_serviceTrackerMap;

}