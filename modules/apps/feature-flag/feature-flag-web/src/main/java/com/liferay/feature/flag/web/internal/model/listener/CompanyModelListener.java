/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.model.listener;

import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBagProvider;
import com.liferay.feature.flag.web.internal.util.DeprecationFeatureFlagsUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 * @author Thiago Buarque
 */
@Component(service = ModelListener.class)
public class CompanyModelListener extends BaseModelListener<Company> {

	@Override
	public void onAfterCreate(Company company) throws ModelListenerException {
		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				FeatureFlagsBagProvider featureFlagsBagProvider =
					_featureFlagsBagProviderSnapshot.get();

				if (featureFlagsBagProvider == null) {
					return null;
				}

				DeprecationFeatureFlagsUtil.processDeprecationFeatureFlags(
					company.getCompanyId(), featureFlagsBagProvider,
					_portalPreferencesLocalService);

				return null;
			});
	}

	private static final Snapshot<FeatureFlagsBagProvider>
		_featureFlagsBagProviderSnapshot = new Snapshot<>(
			CompanyModelListener.class, FeatureFlagsBagProvider.class);

	@Reference
	private PortalPreferencesLocalService _portalPreferencesLocalService;

}