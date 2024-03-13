/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.feature.flag;

import com.liferay.change.tracking.internal.dispatch.executor.CTConflictCheckerDispatchTaskExecutor;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "featureFlagKey=LPD-11018", service = FeatureFlagListener.class
)
public class CTFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.fetchDispatchTrigger(
				companyId, CTConflictCheckerDispatchTaskExecutor.KEY, false);

		if (dispatchTrigger == null) {
			return;
		}

		try {
			if (dispatchTrigger.isActive() != enabled) {
				_dispatchTriggerLocalService.updateActive(
					dispatchTrigger.getDispatchTriggerId(), enabled);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTFeatureFlagListener.class);

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

}