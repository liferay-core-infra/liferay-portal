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

package com.liferay.health.check.internal.model;

import com.liferay.health.check.model.HealthCheckResponse;
import com.liferay.health.check.model.HealthCheckStatus;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the response that is returned by the REST endpoints,
 * and aggregates every response from custom health checks.
 * It allows to build a global status (UP or DOWN) based on those responses,
 * and list every health checker response if "" is checked in the configuration.
 *
 * @author Louis-Guillaume Durand
 * @see HealthCheckApplication
 * @see HealthCheckResponse
 * @see HealthCheckStatus
 * @see HealthCheckResponseConfiguration
 */
public class GlobalHealthCheckResponse {

	public static GlobalHealthCheckResponseBuilder builder() {
		return new GlobalHealthCheckResponseBuilder();
	}

	public List<HealthCheckResponse> getChecks() {
		return _checks;
	}

	public HealthCheckStatus getStatus() {
		return _status;
	}

	public String toJSON(boolean includeDetails) {
		if (includeDetails) {
			_jsonSerializer.include("checks");
		}
		else {
			_jsonSerializer.exclude("checks");
		}

		return _jsonSerializer.serializeDeep(this);
	}

	public static class GlobalHealthCheckResponseBuilder {

		public GlobalHealthCheckResponse build() {
			return _globalHealthCheckResponse;
		}

		public GlobalHealthCheckResponseBuilder down() {
			_globalHealthCheckResponse._status = HealthCheckStatus.DOWN;

			return this;
		}

		public GlobalHealthCheckResponseBuilder up() {
			_globalHealthCheckResponse._status = HealthCheckStatus.UP;

			return this;
		}

		public GlobalHealthCheckResponseBuilder withChecks(
			List<HealthCheckResponse> checks) {

			_globalHealthCheckResponse._checks.addAll(checks);

			return this;
		}

		private final GlobalHealthCheckResponse _globalHealthCheckResponse =
			new GlobalHealthCheckResponse();

	}

	protected GlobalHealthCheckResponse() {
	}

	private final List<HealthCheckResponse> _checks = new ArrayList<>();
	private final JSONSerializer _jsonSerializer =
		JSONFactoryUtil.createJSONSerializer();
	private HealthCheckStatus _status;

}