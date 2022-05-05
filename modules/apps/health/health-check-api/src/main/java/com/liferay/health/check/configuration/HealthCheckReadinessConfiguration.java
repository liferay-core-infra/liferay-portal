/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.health.check.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Louis-Guillaume Durand
 */
@ExtendedObjectClassDefinition(
	category = "health-check",
	scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	description = "health-check-readiness-configuration-description",
	id = HealthCheckReadinessConfiguration.PID,
	localization = "content/Language",
	name = "health-check-readiness-configuration-name"
)
public interface HealthCheckReadinessConfiguration {

	public static final String PID =
		"com.liferay.health.check.configuration." +
			"HealthCheckReadinessConfiguration";

	@Meta.AD(
		deflt = "false",
		description = "health-check-readiness-ignore-osgi-states-description",
		name = "health-check-readiness-ignore-osgi-states", required = false
	)
	public boolean ignoreOSGiStatesForReadiness();

	@Meta.AD(
		deflt = "",
		description = "health-check-readiness-bundle-symbolic-names-description",
		name = "health-check-readiness-bundle-symbolic-names", required = false
	)
	public String[] bundleSymbolicNamesForReadiness();

}