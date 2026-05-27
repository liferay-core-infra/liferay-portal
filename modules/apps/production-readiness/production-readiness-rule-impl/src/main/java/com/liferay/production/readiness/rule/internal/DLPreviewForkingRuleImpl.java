/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.util.PropsKeys;
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
public class DLPreviewForkingRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		if (PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED) {
			return Collections.singletonList(
				new Result(
					Result.Status.PASS, Result.Severity.LOW, getCategory(),
					PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=" +
						PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED,
					PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED +
						"=true",
					"production-readiness-rule-dl-preview-forking-pass",
					new Object[] {
						PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED
					},
					null, getKey()));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.LOW, getCategory(),
				PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=" +
					PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED,
				PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=true",
				"production-readiness-rule-dl-preview-forking-fail",
				new Object[] {
					PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED
				},
				null, getKey()));
	}

	@Override
	public String getCategory() {
		return "portal-properties-configuration";
	}

	@Override
	public String getKey() {
		return "dl-preview-forking";
	}

}