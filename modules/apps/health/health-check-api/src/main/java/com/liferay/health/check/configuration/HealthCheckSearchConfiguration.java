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
	id = HealthCheckSearchConfiguration.PID, localization = "content/Language",
	name = "health-check-search-configuration-name"
)
public interface HealthCheckSearchConfiguration {

	public static final String PID =
		"com.liferay.health.check.configuration.HealthCheckSearchConfiguration";

	@Meta.AD(
		deflt = "3", description = "health-check-search-timeout-description",
		min = "1", name = "health-check-search-timeout", required = false
	)
	public long timeout();

	@Meta.AD(
		deflt = "YELLOW",
		description = "health-check-search-status-up-description",
		name = "health-check-search-status-up",
		optionLabels = {"GREEN", "YELLOW"}, optionValues = {"GREEN", "YELLOW"},
		required = false
	)
	public String statusUp();

}