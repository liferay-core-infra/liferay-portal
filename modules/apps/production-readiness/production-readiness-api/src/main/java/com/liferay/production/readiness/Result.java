/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness;

/**
 * @author Lily Chi
 */
public class Result {

	public Result(
		String category, String currentValue, String docsLink, String key,
		String messageKey, Object[] messageParameters, String recommendedValue,
		Severity severity, Status status) {

		_category = category;
		_currentValue = currentValue;
		_docsLink = docsLink;
		_key = key;
		_messageKey = messageKey;
		_messageParameters = messageParameters;
		_recommendedValue = recommendedValue;
		_severity = severity;
		_status = status;
	}

	public String getCategory() {
		return _category;
	}

	public String getCurrentValue() {
		return _currentValue;
	}

	public String getDocsLink() {
		if (_docsLink == null) {
			return _DEFAULT_DOCS_LINK;
		}

		return _docsLink;
	}

	public String getKey() {
		return _key;
	}

	public String getMessageKey() {
		return _messageKey;
	}

	public Object[] getMessageParameters() {
		return _messageParameters;
	}

	public String getRecommendedValue() {
		return _recommendedValue;
	}

	public Severity getSeverity() {
		return _severity;
	}

	public Status getStatus() {
		return _status;
	}

	public enum Severity {

		CRITICAL, HIGH, LOW, MEDIUM

	}

	public enum Status {

		FAIL, PASS

	}

	private static final String _DEFAULT_DOCS_LINK =
		"https://www.liferay.com/documents/10182/3292406/Liferay+DXP+7." +
			"4+Deployment+Checklist.pdf/f3464a36-c0f0-6708-37dd-efe7b8270403?" +
				"t=1643744619710";

	private final String _category;
	private final String _currentValue;
	private final String _docsLink;
	private final String _key;
	private final String _messageKey;
	private final Object[] _messageParameters;
	private final String _recommendedValue;
	private final Severity _severity;
	private final Status _status;

}