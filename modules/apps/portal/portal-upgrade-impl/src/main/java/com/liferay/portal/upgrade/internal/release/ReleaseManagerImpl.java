/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.release;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.ReleaseManager;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.osgi.debug.SystemChecker;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.internal.executor.UpgradeExecutor;
import com.liferay.portal.upgrade.internal.graph.ReleaseGraphManager;
import com.liferay.portal.upgrade.internal.registry.UpgradeInfo;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Chaparro
 * @author Samuel Ziemer
 */
@Component(service = {ReleaseManager.class, ReleaseManagerImpl.class})
public class ReleaseManagerImpl implements ReleaseManager {

	@Override
	public String getShortStatusMessage(boolean onlyRequiredUpgrades) {
		String message =
			"%s upgrades in %s are pending. Run the upgrade process or type " +
				"upgrade:checkAll in the Gogo shell to get more information.";

		if (onlyRequiredUpgrades) {
			if (_isPendingRequiredModuleUpgrades()) {
				return String.format(message, "Required", "modules");
			}

			return StringPool.BLANK;
		}

		String where = StringPool.BLANK;

		try (Connection connection = DataAccess.getConnection()) {
			if (!PortalUpgradeProcess.isInLatestSchemaVersion(connection)) {
				where = "portal";
			}
		}
		catch (SQLException sqlException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get pending upgrade information for the portal",
					sqlException);
			}
		}

		Set<String> upgradableBundleSymbolicNames =
			_upgradeStepRegistry.getUpgradableBundleSymbolicNames();

		if (!upgradableBundleSymbolicNames.isEmpty()) {
			if (Validator.isNotNull(where)) {
				where = "and " + where;
			}
			else {
				where = "modules";
			}
		}

		if (Validator.isNotNull(where)) {
			return String.format(message, "Optional", where);
		}

		return StringPool.BLANK;
	}

	@Override
	public String getStatusMessage(boolean showUpgradeSteps) {
		StringBundler sb = new StringBundler(6);

		sb.append(_checkPortal(showUpgradeSteps));

		if (sb.length() > 0) {
			sb.append(StringPool.NEW_LINE);
		}

		sb.append(_checkModules(showUpgradeSteps));

		if (!_hasUnsatisfiedUpgradeComponents()) {
			sb.append("Unsatisfied components prevent upgrade processes to ");
			sb.append("be registered");

			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	@Override
	public boolean isUpgraded() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			if (!PortalUpgradeProcess.isInLatestSchemaVersion(connection)) {
				return false;
			}

			Set<String> upgradableBundleSymbolicNames =
				_upgradeStepRegistry.getUpgradableBundleSymbolicNames();

			if (!upgradableBundleSymbolicNames.isEmpty()) {
				return false;
			}
		}

		return _hasUnsatisfiedUpgradeComponents();
	}

	private String _checkModules(boolean showUpgradeSteps) {
		StringBundler sb = new StringBundler();

		for (String bundleSymbolicName :
				_upgradeStepRegistry.getBundleSymbolicNames()) {

			String schemaVersionString =
				_upgradeStepRegistry.getSchemaVersionString(bundleSymbolicName);

			ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
				_upgradeStepRegistry.getUpgradeInfos(bundleSymbolicName));

			List<List<UpgradeInfo>> upgradeInfosList =
				releaseGraphManager.getUpgradeInfosList(schemaVersionString);

			int size = upgradeInfosList.size();

			if (size > 1) {
				sb.append("There are ");
				sb.append(size);
				sb.append(" possible end nodes for ");
				sb.append(schemaVersionString);
				sb.append(StringPool.NEW_LINE);
			}

			if (size == 0) {
				continue;
			}

			List<UpgradeInfo> upgradeInfos = upgradeInfosList.get(0);

			UpgradeInfo lastUpgradeInfo = upgradeInfos.get(
				upgradeInfos.size() - 1);

			sb.append(
				_getModulePendingUpgradeMessage(
					bundleSymbolicName, schemaVersionString,
					lastUpgradeInfo.getToSchemaVersionString()));

			if (showUpgradeSteps) {
				sb.append(StringPool.COLON);

				for (UpgradeInfo upgradeInfo : upgradeInfos) {
					UpgradeStep upgradeStep = upgradeInfo.getUpgradeStep();

					sb.append(StringPool.NEW_LINE);
					sb.append(StringPool.TAB);
					sb.append(
						_getPendingUpgradeProcessMessage(
							upgradeStep.getClass(),
							upgradeInfo.getFromSchemaVersionString(),
							upgradeInfo.getToSchemaVersionString()));
				}
			}

			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private String _checkPortal(boolean showUpgradeSteps) {
		try (Connection connection = DataAccess.getConnection()) {
			Version currentSchemaVersion =
				PortalUpgradeProcess.getCurrentSchemaVersion(connection);

			SortedMap<Version, UpgradeProcess> pendingUpgradeProcesses =
				PortalUpgradeProcess.getPendingUpgradeProcesses(
					currentSchemaVersion);

			if (!pendingUpgradeProcesses.isEmpty()) {
				Version latestSchemaVersion =
					PortalUpgradeProcess.getLatestSchemaVersion();

				StringBundler sb = new StringBundler();

				sb.append(
					_getModulePendingUpgradeMessage(
						"Portal", currentSchemaVersion.toString(),
						latestSchemaVersion.toString()));

				sb.append(" (requires upgrade tool or auto upgrade)");

				if (showUpgradeSteps) {
					sb.append(StringPool.COLON);

					for (SortedMap.Entry<Version, UpgradeProcess> entry :
							pendingUpgradeProcesses.entrySet()) {

						sb.append(StringPool.NEW_LINE);
						sb.append(StringPool.TAB);

						UpgradeProcess upgradeProcess = entry.getValue();
						Version version = entry.getKey();

						sb.append(
							_getPendingUpgradeProcessMessage(
								upgradeProcess.getClass(),
								currentSchemaVersion.toString(),
								version.toString()));

						sb.append(StringPool.NEW_LINE);

						currentSchemaVersion = version;
					}
				}

				return sb.toString();
			}
		}
		catch (SQLException sqlException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get pending upgrade information for the portal",
					sqlException);
			}
		}

		return StringPool.BLANK;
	}

	private String _getModulePendingUpgradeMessage(
		String moduleName, String currentSchemaVersion,
		String finalSchemaVersion) {

		return StringBundler.concat(
			"There are upgrade processes available for ", moduleName, " from ",
			currentSchemaVersion, " to ", finalSchemaVersion);
	}

	private String _getPendingUpgradeProcessMessage(
		Class<?> upgradeClass, String fromSchemaVersion,
		String toSchemaVersion) {

		StringBundler sb = new StringBundler(6);

		String toMessage = toSchemaVersion;

		if (UpgradeProcessUtil.isRequiredSchemaVersion(
				Version.parseVersion(fromSchemaVersion),
				Version.parseVersion(toSchemaVersion))) {

			toMessage += " (REQUIRED)";
		}

		sb.append(fromSchemaVersion);
		sb.append(" to ");
		sb.append(toMessage);
		sb.append(StringPool.COLON);
		sb.append(StringPool.SPACE);
		sb.append(upgradeClass.getName());

		return sb.toString();
	}

	private boolean _hasUnsatisfiedUpgradeComponents() {
		String result = _systemChecker.check();

		return !result.contains("UpgradeStepRegistrator");
	}

	private boolean _isPendingRequiredModuleUpgrades() {
		Set<String> upgradableBundleSymbolicNames =
			_upgradeStepRegistry.getUpgradableBundleSymbolicNames();

		for (String bundleSymbolicName : upgradableBundleSymbolicNames) {
			ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
				_upgradeStepRegistry.getUpgradeInfos(bundleSymbolicName));

			List<List<UpgradeInfo>> upgradeInfosList =
				releaseGraphManager.getUpgradeInfosList(
					_upgradeStepRegistry.getSchemaVersionString(
						bundleSymbolicName));

			List<UpgradeInfo> upgradeInfos = upgradeInfosList.get(0);

			for (UpgradeInfo upgradeInfo : upgradeInfos) {
				if (UpgradeProcessUtil.isRequiredSchemaVersion(
						Version.parseVersion(
							upgradeInfo.getFromSchemaVersionString()),
						Version.parseVersion(
							upgradeInfo.getToSchemaVersionString()))) {

					return true;
				}
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReleaseManagerImpl.class);

	@Reference
	private ReleaseLocalService _releaseLocalService;

	@Reference(
		target = "(component.name=com.liferay.portal.osgi.debug.declarative.service.internal.DeclarativeServiceUnsatisfiedComponentSystemChecker)"
	)
	private volatile SystemChecker _systemChecker;

	@Reference
	private UpgradeExecutor _upgradeExecutor;

	@Reference
	private UpgradeStepRegistry _upgradeStepRegistry;

}