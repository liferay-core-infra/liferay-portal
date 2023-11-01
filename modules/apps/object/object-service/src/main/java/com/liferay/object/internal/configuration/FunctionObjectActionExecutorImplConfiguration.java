/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Raymond Augé
 */
@Meta.OCD(
	factory = true,
	id = "com.liferay.object.internal.configuration.FunctionObjectActionExecutorImplConfiguration",
	localization = "content/Language",
	name = "function-object-action-executor-impl-configuration-name"
)
public interface FunctionObjectActionExecutorImplConfiguration {

	@Meta.AD(
		name = "oauth2-application-external-reference-code", required = false,
		type = Meta.Type.String
	)
	public String oAuth2ApplicationExternalReferenceCode();

	@Meta.AD(name = "resource-path", required = false, type = Meta.Type.String)
	public String resourcePath();

}