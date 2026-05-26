/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness;

import java.util.Collection;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Lily Chi
 */
@ProviderType
public interface ProductionReadinessRule {

	public Collection<Result> check(long companyId);

	public String getCategory();

	public String getKey();

}