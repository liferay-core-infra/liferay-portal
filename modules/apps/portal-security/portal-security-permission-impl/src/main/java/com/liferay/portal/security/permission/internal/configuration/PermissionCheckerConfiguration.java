/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Jiaxu Wei
 */
@ExtendedObjectClassDefinition(category = "security-tools")
@Meta.OCD(
	id = "com.liferay.portal.security.permission.internal.configuration.PermissionCheckerConfiguration",
	localization = "content/Language",
	name = "permission-checker-configuration-name"
)
public interface PermissionCheckerConfiguration {

	/**
	 # Set the default permission checker class used by
	 # com.liferay.portal.security.permission.PermissionCheckerFactory to check
	 # permissions for actions on objects. This class can be overriden with a
	 # custom class that implements
	 # com.liferay.portal.security.permission.PermissionChecker.
	 */
	@Meta.AD(
		deflt = "com.liferay.portal.security.permission.internal.AdvancedPermissionChecker",
		name = "permission-checker", required = false
	)
	public String permissionChecker();

}