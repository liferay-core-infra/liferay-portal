/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

/**
 * @author István András Dézsi
 */
public class DuplicateVirtualHostnameException extends RuntimeException {

	public DuplicateVirtualHostnameException(String hostname) {
		super("Duplicate virtual hostname " + hostname);
	}

}