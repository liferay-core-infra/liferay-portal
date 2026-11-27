/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.instance.lifecycle;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.web.internal.helper.WorkspaceSiteHelper;
import com.liferay.portal.instance.lifecycle.InitialRequestPortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class FaroWorkspaceSitesPortalInstanceLifecycleListener
	extends InitialRequestPortalInstanceLifecycleListener {

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Override
	protected void doPortalInstanceRegistered(long companyId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-114293")) {
			return;
		}

		for (FaroProject faroProject :
				_faroProjectLocalService.getFaroProjects(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			if (faroProject.getCompanyId() != companyId) {
				continue;
			}

			try {
				if (!_workspaceSiteHelper.isInitialized(faroProject)) {
					_workspaceSiteHelper.initialize(faroProject);
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to initialize the site of workspace " +
						faroProject.getGroupId(),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroWorkspaceSitesPortalInstanceLifecycleListener.class);

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private WorkspaceSiteHelper _workspaceSiteHelper;

}