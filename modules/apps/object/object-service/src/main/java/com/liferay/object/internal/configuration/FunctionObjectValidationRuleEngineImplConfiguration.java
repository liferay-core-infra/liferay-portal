/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Mateus Santana
 */
@Meta.OCD(
	factory = true,
	id = "com.liferay.object.internal.configuration.FunctionObjectValidationRuleEngineImplConfiguration",
	localization = "content/Language",
	name = "function-object-validation-rule-engine-impl-configuration-name"
)
public interface FunctionObjectValidationRuleEngineImplConfiguration {

	@Meta.AD(name = "oauth2-application-external-reference-code")
	public String oAuth2ApplicationExternalReferenceCode();

	@Meta.AD(name = "resource-path")
	public String resourcePath();

}