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
public class DLImagePreviewDPIRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		int dpi = PropsValues.DL_FILE_ENTRY_PREVIEW_DOCUMENT_DPI;

		if (dpi > 75) {
			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.LOW, getCategory(),
					String.valueOf(dpi), null,
					"production-readiness-rule-dl-image-preview-dpi-fail",
					new Object[0], null));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.PASS, Result.Severity.LOW, getCategory(),
				String.valueOf(dpi), null,
				"production-readiness-rule-dl-image-preview-dpi-pass",
				new Object[0], null));
	}

	@Override
	public String getCategory() {
		return "portal-properties-configuration";
	}

	@Override
	public String getKey() {
		return "dl-image-preview-dpi";
	}

}