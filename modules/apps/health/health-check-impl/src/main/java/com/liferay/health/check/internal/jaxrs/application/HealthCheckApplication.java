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

package com.liferay.health.check.internal.jaxrs.application;

import com.liferay.health.check.configuration.HealthCheckLivenessConfiguration;
import com.liferay.health.check.configuration.HealthCheckReadinessConfiguration;
import com.liferay.health.check.configuration.HealthCheckResponseConfiguration;
import com.liferay.health.check.internal.model.GlobalHealthCheckResponse;
import com.liferay.health.check.model.HealthCheckProbeType;
import com.liferay.health.check.model.HealthCheckResponse;
import com.liferay.health.check.model.HealthCheckStatus;
import com.liferay.health.check.service.HealthCheckService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Louis-Guillaume Durand
 */
@Component(
	configurationPid = {
		HealthCheckLivenessConfiguration.PID,
		HealthCheckReadinessConfiguration.PID,
		HealthCheckResponseConfiguration.PID
	},
	immediate = true,
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/health",
		JaxrsWhiteboardConstants.JAX_RS_EXTENSION_SELECT + "=(osgi.jaxrs.name=Liferay.Vulcan)",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=Liferay.Health.Check"
	},
	service = Application.class
)
public class HealthCheckApplication extends Application {

	public Set<Object> getSingletons() {
		return Collections.singleton(this);
	}

	@GET
	@Path("/live")
	@Produces(MediaType.APPLICATION_JSON)
	public Response liveness() {
		GlobalHealthCheckResponse globalHealthCheckResponse =
			_aggregateHealthChecks(HealthCheckProbeType.LIVENESS);

		if (HealthCheckStatus.DOWN.equals(
				globalHealthCheckResponse.getStatus())) {

			return Response.serverError(
			).status(
				Response.Status.SERVICE_UNAVAILABLE
			).entity(
				globalHealthCheckResponse.toJSON(
					_healthCheckResponseConfiguration.includeDetails())
			).build();
		}

		return Response.ok(
			globalHealthCheckResponse.toJSON(
				_healthCheckResponseConfiguration.includeDetails())
		).build();
	}

	@GET
	@Path("/ready")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readiness() {
		GlobalHealthCheckResponse globalHealthCheckResponse =
			_aggregateHealthChecks(HealthCheckProbeType.READINESS);

		if (HealthCheckStatus.DOWN.equals(
				globalHealthCheckResponse.getStatus())) {

			return Response.serverError(
			).status(
				Response.Status.SERVICE_UNAVAILABLE
			).entity(
				globalHealthCheckResponse.toJSON(
					_healthCheckResponseConfiguration.includeDetails())
			).build();
		}

		return Response.ok(
			globalHealthCheckResponse.toJSON(
				_healthCheckResponseConfiguration.includeDetails())
		).build();
	}

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, String> properties) {

		_healthCheckResponseConfiguration = ConfigurableUtil.createConfigurable(
			HealthCheckResponseConfiguration.class, properties);

		_healthCheckServiceTracker = new ServiceTracker<>(
			bundleContext, HealthCheckService.class, null);

		_healthCheckServiceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_healthCheckServiceTracker.close();
	}

	/**
	 * Aggregate individual health checks from all components implementing
	 * <code>HealthCheckService</code>, allowing them to provide
	 * their own definition of the readiness and liveness
	 * which is independent of the bundle state itself.
	 *
	 * @param probeType type of probe we're looking for (e.g. readiness or liveness)
	 * @return a response entity to be sent in the HTTP response body as JSON
	 * @see HealthCheckService
	 * @see HealthCheckProbeType
	 * @see GlobalHealthCheckResponse
	 */
	private GlobalHealthCheckResponse _aggregateHealthChecks(
		HealthCheckProbeType probeType) {

		List<HealthCheckResponse> ups = new ArrayList<>();
		List<HealthCheckResponse> downs = new ArrayList<>();

		Stream<ServiceReference<HealthCheckService>> serviceReferenceStream =
			Arrays.stream(_healthCheckServiceTracker.getServiceReferences());

		serviceReferenceStream.forEach(
			serviceReference -> {
				HealthCheckService healthCheckService =
					_healthCheckServiceTracker.getService(serviceReference);

				String serviceName = GetterUtil.getString(
					serviceReference.getProperty("component.name"));

				HealthCheckResponse healthCheckResponse =
					_getHealthCheckServiceResponse(
						probeType, healthCheckService);

				if (HealthCheckStatus.DOWN.equals(
						healthCheckResponse.getStatus())) {

					downs.add(healthCheckResponse);

					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Service [", serviceName,
								"] is DOWN with the following issues:"));

						List<String> healthCheckResponseIssues =
							healthCheckResponse.getIssues();

						healthCheckResponseIssues.forEach(_log::warn);
					}
				}
				else {
					ups.add(healthCheckResponse);

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Service [", serviceName, "] is UP"));
					}
				}
			});

		if (downs.isEmpty()) {
			return GlobalHealthCheckResponse.builder(
			).up(
			).withChecks(
				ups
			).build();
		}

		return GlobalHealthCheckResponse.builder(
		).down(
		).withChecks(
			ups
		).withChecks(
			downs
		).build();
	}

	private HealthCheckResponse _getHealthCheckServiceResponse(
		HealthCheckProbeType probeType, HealthCheckService healthCheckService) {

		if (HealthCheckProbeType.READINESS.equals(probeType)) {
			return healthCheckService.isReady();
		}

		return healthCheckService.isLive();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HealthCheckApplication.class);

	private volatile HealthCheckResponseConfiguration
		_healthCheckResponseConfiguration;
	private volatile ServiceTracker<HealthCheckService, HealthCheckService>
		_healthCheckServiceTracker;

}