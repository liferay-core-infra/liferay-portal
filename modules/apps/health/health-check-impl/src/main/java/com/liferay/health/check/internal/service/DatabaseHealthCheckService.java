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
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Collections;
import java.util.Properties;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Component;

/**
 * @author Louis-Guillaume Durand
 */
@Component(immediate = true, service = HealthCheckService.class)
public class DatabaseHealthCheckService implements HealthCheckService {

	@Override
	public HealthCheckResponse isLive() {
		return _checkDatabaseConnection();
	}

	@Override
	public HealthCheckResponse isReady() {
		return _checkDatabaseConnection();
	}

	private HealthCheckResponse _checkDatabaseConnection() {
		Properties jdbcProperties = PropsUtil.getProperties(
			"jdbc.default.", true);
		Properties jdbcReadProperties = PropsUtil.getProperties(
			"jdbc.read.", true);

		if ((jdbcReadProperties != null) && (jdbcReadProperties.size() > 0)) {
			jdbcProperties = jdbcReadProperties;
		}

		try {
			DataSource dataSource = DataSourceFactoryUtil.initDataSource(
				jdbcProperties);

			if (dataSource != null) {
				dataSource.getConnection();
			}
			else {
				return HealthCheckResponse.builder(
				).name(
					DatabaseHealthCheckService.class.getName()
				).down(
				).withData(
					"datasource", "datasource is null"
				).build();
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			return HealthCheckResponse.builder(
			).name(
				DatabaseHealthCheckService.class.getName()
			).down(
			).withData(
				"database connection failed", exception.getMessage()
			).build();
		}

		return HealthCheckResponse.builder(
		).name(
			DatabaseHealthCheckService.class.getName()
		).up(
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DatabaseHealthCheckService.class);

}