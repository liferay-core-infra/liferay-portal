/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class PasswordEncryptionRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		String algorithm = PropsUtil.get("passwords.encryption.algorithm");

		if (_isStrongerThanPBKDF2(algorithm)) {
			return Collections.singletonList(
				new Result(
					Result.Status.PASS, Result.Severity.LOW, getCategory(),
					algorithm, "PBKDF2WithHmacSHA1/160/1300000 (or stronger)",
					"production-readiness-rule-password-encryption-pass",
					new Object[0], null, getKey()));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
				algorithm, "PBKDF2WithHmacSHA1/160/1300000 (or stronger)",
				"production-readiness-rule-password-encryption-fail",
				new Object[0], null, getKey()));
	}

	@Override
	public String getCategory() {
		return "portal-properties-configuration";
	}

	@Override
	public String getKey() {
		return "password-encryption";
	}

	private boolean _isStrongerThanPBKDF2(String algorithm) {
		if (algorithm == null) {
			return false;
		}

		if (algorithm.equals("BCRYPT") || algorithm.startsWith("BCRYPT/") ||
			algorithm.equals("SCRYPT")) {

			return true;
		}

		if (algorithm.startsWith("PBKDF2WithHmacSHA1/")) {
			String[] parts = algorithm.split("/");

			if (parts.length >= 3) {
				int rounds = GetterUtil.getInteger(parts[2]);

				if (rounds >= 1300000) {
					return true;
				}
			}
		}

		return false;
	}

}