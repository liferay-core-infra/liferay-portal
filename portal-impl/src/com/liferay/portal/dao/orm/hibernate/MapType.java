/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;

/**
 * @author Cristina González
 */
@SuppressWarnings("rawtypes")
public class MapType implements Serializable, UserType<Map> {

	@Override
	public Map assemble(Serializable cached, Object owner) {
		return (Map)cached;
	}

	@Override
	public Map deepCopy(Map object) {
		return object;
	}

	@Override
	public Serializable disassemble(Map value) {
		return (Serializable)value;
	}

	@Override
	public boolean equals(Map x, Map y) {
		return Objects.equals(x, y);
	}

	@Override
	public int getSqlType() {
		return Types.VARCHAR;
	}

	@Override
	public int hashCode(Map x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Map nullSafeGet(
			ResultSet resultSet, int index, WrapperOptions wrapperOptions)
		throws SQLException {

		String json = resultSet.getString(index);

		if (resultSet.wasNull()) {
			return null;
		}

		try {
			return (Map<?, ?>)_jsonFactory.deserialize(json);
		}
		catch (Exception exception) {
			_log.error("Unable to process JSON " + json, exception);

			return Collections.emptyMap();
		}
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Map target, int index,
			WrapperOptions wrapperOptions)
		throws SQLException {

		String json = _jsonFactory.serialize(target);

		preparedStatement.setString(index, json);
	}

	@Override
	public Map replace(Map original, Map target, Object owner) {
		return original;
	}

	@Override
	public Class<Map> returnedClass() {
		return Map.class;
	}

	private static final Log _log = LogFactoryUtil.getLog(MapType.class);

	private static final JSONFactory _jsonFactory = new JSONFactoryImpl();

}