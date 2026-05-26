/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class FileStoreImplementationRuleImpl
	implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		String dlStoreImpl = PropsValues.DL_STORE_IMPL;

		boolean pass = false;

		if (dlStoreImpl.equals(
				"com.liferay.portal.store.file.system." +
					"AdvancedFileSystemStore") ||
			dlStoreImpl.equals("com.liferay.portal.store.s3.S3Store") ||
			dlStoreImpl.equals("com.liferay.portal.store.s3.IBMS3Store") ||
			dlStoreImpl.equals("com.liferay.portal.store.gcs.GCSStore") ||
			dlStoreImpl.equals("com.liferay.portal.store.azure.AzureStore")) {

			pass = true;
		}

		if (pass) {
			return Collections.singletonList(
				new Result(
					Result.Status.PASS, Result.Severity.LOW, getCategory(),
					dlStoreImpl, null,
					"production-readiness-rule-file-store-implementation-pass",
					new Object[0], null));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.LOW, getCategory(),
				dlStoreImpl, "AdvancedFileSystemStore or Cloud Store",
				"production-readiness-rule-file-store-implementation-fail",
				new Object[0], null));
	}

	@Override
	public String getCategory() {
		return "portal-properties-configuration";
	}

	@Override
	public String getKey() {
		return "file-store-implementation";
	}

}