/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.exception;

import com.liferay.portal.kernel.exception.NoSuchModelException;

/**
 * @author Lily Chi
 */
public class NoSuchIgnoredRuleException extends NoSuchModelException {

	public NoSuchIgnoredRuleException() {
	}

	public NoSuchIgnoredRuleException(String msg) {
		super(msg);
	}

	public NoSuchIgnoredRuleException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public NoSuchIgnoredRuleException(Throwable throwable) {
		super(throwable);
	}

}