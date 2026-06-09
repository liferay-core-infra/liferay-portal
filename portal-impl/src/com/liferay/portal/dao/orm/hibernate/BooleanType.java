/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.EnhancedUserType;

/**
 * @author Brian Wing Shun Chan
 */
public class BooleanType implements EnhancedUserType<Boolean>, Serializable {

	public static final Boolean DEFAULT_VALUE = Boolean.FALSE;

	@Override
	public Boolean assemble(Serializable cached, Object owner) {
		return (Boolean)cached;
	}

	@Override
	public Boolean deepCopy(Boolean object) {
		return object;
	}

	@Override
	public Serializable disassemble(Boolean value) {
		return value;
	}

	@Override
	public boolean equals(Boolean x, Boolean y) {
		if (x == y) {
			return true;
		}
		else if ((x == null) || (y == null)) {
			return false;
		}

		return x.equals(y);
	}

	@Override
	public Boolean fromStringValue(CharSequence sequence) {
		return Boolean.valueOf(sequence.toString());
	}

	@Override
	public int getSqlType() {
		return Types.BOOLEAN;
	}

	@Override
	public int hashCode(Boolean x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Boolean nullSafeGet(
			ResultSet resultSet, int index, WrapperOptions wrapperOptions)
		throws SQLException {

		boolean value = resultSet.getBoolean(index);

		if (resultSet.wasNull()) {
			return DEFAULT_VALUE;
		}

		return value;
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Boolean target, int index,
			WrapperOptions wrapperOptions)
		throws SQLException {

		if (target == null) {
			target = DEFAULT_VALUE;
		}

		preparedStatement.setBoolean(index, target);
	}

	@Override
	public Boolean replace(Boolean original, Boolean target, Object owner) {
		return original;
	}

	@Override
	public Class<Boolean> returnedClass() {
		return Boolean.class;
	}

	@Override
	public String toSqlLiteral(Boolean value) {
		DB db = DBManagerUtil.getDB();

		if (Boolean.TRUE.equals(value)) {
			return db.getTemplateTrue();
		}

		return db.getTemplateFalse();
	}

	@Override
	public String toString(Boolean value) {
		return value.toString();
	}

}