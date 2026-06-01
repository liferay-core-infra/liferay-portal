/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.production.readiness;

/**
 * @author Lily Chi
 */
public class IgnoredRule {

	public IgnoredRule(
		long ignoredAt, String ignoredBy, String reason, String ruleKey) {

		_ignoredAt = ignoredAt;
		_ignoredBy = ignoredBy;
		_reason = reason;
		_ruleKey = ruleKey;
	}

	public long getIgnoredAt() {
		return _ignoredAt;
	}

	public String getIgnoredBy() {
		return _ignoredBy;
	}

	public String getReason() {
		return _reason;
	}

	public String getRuleKey() {
		return _ruleKey;
	}

	private final long _ignoredAt;
	private final String _ignoredBy;
	private final String _reason;
	private final String _ruleKey;

}