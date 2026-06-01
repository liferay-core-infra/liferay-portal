/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * Backs the production readiness ignore status. The ignored rules are persisted
 * to a configuration file in <code>${liferay.home}/osgi/configs</code> rather
 * than to the database.
 *
 * @author Lily Chi
 */
@ExtendedObjectClassDefinition(
	category = "server-administration", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.server.admin.web.internal.configuration.ProductionReadinessConfiguration",
	localization = "content/Language",
	name = "production-readiness-configuration-name"
)
public interface ProductionReadinessConfiguration {

	/**
	 * Each entry is a JSON object describing an ignored rule, with the keys
	 * <code>ignoredAt</code>, <code>ignoredBy</code>, <code>reason</code>, and
	 * <code>ruleKey</code>.
	 */
	@Meta.AD(name = "ignored-rules", required = false)
	public String[] ignoredRules();

}