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

package com.liferay.health.check.internal.service;

import com.liferay.health.check.model.HealthCheckResponse;
import com.liferay.health.check.service.HealthCheckService;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Louis-Guillaume Durand
 */
@Component(immediate = true, service = HealthCheckService.class)
public class StartupHealthCheckService implements HealthCheckService {

	@Override
	public HealthCheckResponse isLive() {
		return HealthCheckResponse.builder(
		).name(
			StartupHealthCheckService.class.getName()
		).up(
		).build();
	}

	@Override
	public HealthCheckResponse isReady() {
		return HealthCheckResponse.builder(
		).name(
			StartupHealthCheckService.class.getName()
		).up(
		).build();
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}