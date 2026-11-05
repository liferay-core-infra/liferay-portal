/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.jdbc.util.DBInfo;
import com.liferay.portal.dao.jdbc.util.DBInfoUtil;
import com.liferay.portal.dao.orm.hibernate.DB2Dialect;
import com.liferay.portal.dao.orm.hibernate.SQLServerDialect;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

/**
 * @author Brian Wing Shun Chan
 */
public class DialectDetector {

	public static Dialect getDialect(DataSource dataSource) {
		DBInfo dbInfo = DBInfoUtil.getDBInfo(dataSource);

		return _dialects.computeIfAbsent(
			StringBundler.concat(
				dbInfo.getName(), StringPool.COLON, dbInfo.getMajorVersion(),
				StringPool.COLON, dbInfo.getMinorVersion()),
			dialectKey -> _createDialect(dataSource, dbInfo));
	}

	private static Dialect _createDialect(
		DataSource dataSource, DBInfo dbInfo) {

		int dbMajorVersion = dbInfo.getMajorVersion();
		int dbMinorVersion = dbInfo.getMinorVersion();
		String dbName = dbInfo.getName();

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Determine dialect for ", dbName, " ", dbMajorVersion, ".",
					dbMinorVersion));
		}

		Properties properties = PropsUtil.getProperties(
			"hibernate.dialect.", false);

		Map<String, Object> configurationValues = new HashMap<>();

		for (String name : properties.stringPropertyNames()) {
			configurationValues.put(name, properties.getProperty(name));
		}

		Dialect dialect = null;

		try (Connection connection = dataSource.getConnection()) {
			DialectResolutionInfo dialectResolutionInfo =
				new DatabaseMetaDataDialectResolutionInfoAdapter(
					connection.getMetaData()) {

					@Override
					public Map<String, Object> getConfigurationValues() {
						return configurationValues;
					}

				};

			if (dbName.startsWith("DB2")) {
				dialect = new DB2Dialect(dialectResolutionInfo);
			}
			else if (dbName.startsWith("Microsoft SQL Server")) {
				dialect = new SQLServerDialect(dialectResolutionInfo);
			}
			else {
				DialectResolver dialectResolver = new StandardDialectResolver();

				dialect = dialectResolver.resolveDialect(dialectResolutionInfo);

				if (dialect == null) {
					throw new RuntimeException(
						"No dialect found for " + dbName);
				}
			}

			if ((dialect instanceof HSQLDialect) && _log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Liferay is configured to use Hypersonic as its ",
						"database. Do NOT use Hypersonic in production. ",
						"Hypersonic is an embedded database useful for ",
						"development and demonstration purposes. The database ",
						"settings can be changed in portal-ext.properties."));
			}
		}
		catch (SQLException sqlException) {
			return ReflectionUtil.throwException(sqlException);
		}

		if (_log.isInfoEnabled()) {
			Class<?> clazz = dialect.getClass();

			_log.info(
				StringBundler.concat(
					"Using dialect ", clazz.getName(), " for ", dbName, " ",
					dbMajorVersion, ".", dbMinorVersion));
		}

		return dialect;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DialectDetector.class);

	private static final Map<String, Dialect> _dialects =
		new ConcurrentHashMap<>();

}