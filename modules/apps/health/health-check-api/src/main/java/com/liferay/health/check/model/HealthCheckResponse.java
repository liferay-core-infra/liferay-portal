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

package com.liferay.health.check.model;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a response body for the readiness and liveness probes and provide information about the
 * status (UP or DOWN), a name to identify the health checker and a list of issues detected, if any.
 *
 * @author Louis-Guillaume Durand
 * @see HealthCheckStatus
 */
public class HealthCheckResponse {

	public static HealthCheckResponseBuilder builder() {
		return new HealthCheckResponseBuilder();
	}

	public List<String> getIssues() {
		return _issues;
	}

	public String getName() {
		return _name;
	}

	public HealthCheckStatus getStatus() {
		return _status;
	}

	public String toJSON(boolean includeDetails) {
		if (includeDetails) {
			_jsonSerializer.include("name", "issues");
		}
		else {
			_jsonSerializer.exclude("name", "issues");
		}

		return _jsonSerializer.serializeDeep(this);
	}

	public static class HealthCheckResponseBuilder {

		public HealthCheckResponse build() {
			return _healthCheckResponse;
		}

		public HealthCheckResponseBuilder down() {
			_healthCheckResponse._status = HealthCheckStatus.DOWN;

			return this;
		}

		public HealthCheckResponseBuilder issues(List<String> issues) {
			_healthCheckResponse._issues.addAll(issues);

			return this;
		}

		public HealthCheckResponseBuilder name(String name) {
			_healthCheckResponse._name = name;

			return this;
		}

		public HealthCheckResponseBuilder up() {
			_healthCheckResponse._status = HealthCheckStatus.UP;

			return this;
		}

		private final HealthCheckResponse _healthCheckResponse =
			new HealthCheckResponse();

	}

	protected HealthCheckResponse() {
	}

	private final List<String> _issues = new ArrayList<>();
	private final JSONSerializer _jsonSerializer =
		JSONFactoryUtil.createJSONSerializer();
	private String _name;
	private HealthCheckStatus _status;

}