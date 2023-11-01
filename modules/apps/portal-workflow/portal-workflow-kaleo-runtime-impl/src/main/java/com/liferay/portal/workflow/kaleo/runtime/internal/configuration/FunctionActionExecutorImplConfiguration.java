/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Raymond Augé
 */
@Meta.OCD(
	factory = true,
	id = "com.liferay.portal.workflow.kaleo.runtime.internal.configuration.FunctionActionExecutorImplConfiguration",
	localization = "content/Language",
	name = "function-action-executor-impl-configuration-name"
)
public interface FunctionActionExecutorImplConfiguration {

	@Meta.AD(
		name = "oauth2-application-external-reference-code", required = false
	)
	public String oAuth2ApplicationExternalReferenceCode();

	@Meta.AD(name = "resource-path", required = false)
	public String resourcePath();

}