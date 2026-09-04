/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;

/**
 * @author Shuyang Zhou
 */
public class StringClobType implements Serializable, UserType<String> {

	@Override
	public String assemble(Serializable cached, Object owner)
		throws HibernateException {

		return (String)cached;
	}

	@Override
	public String deepCopy(String value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(String value) throws HibernateException {
		return value;
	}

	@Override
	public boolean equals(String object1, String object2) {
		if (Objects.equals(object1, object2)) {
			return true;
		}
		else if (((object1 == null) || object1.equals(StringPool.BLANK)) &&
				 ((object2 == null) || object2.equals(StringPool.BLANK))) {

			return true;
		}

		return false;
	}

	@Override
	public int getSqlType() {
		return Types.CLOB;
	}

	@Override
	public int hashCode(String object) throws HibernateException {
		return object.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public String nullSafeGet(
			ResultSet resultSet, int index, WrapperOptions wrapperOptions)
		throws SQLException {

		Reader reader = resultSet.getCharacterStream(index);

		if (reader == null) {
			return null;
		}

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		try {
			reader.transferTo(unsyncStringWriter);
		}
		catch (IOException ioException) {
			throw new SQLException(ioException.getMessage());
		}

		return unsyncStringWriter.toString();
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, String target, int index,
			WrapperOptions wrapperOptions)
		throws SQLException {

		if (target != null) {
			StringReader stringReader = new StringReader(target);

			preparedStatement.setCharacterStream(
				index, stringReader, target.length());
		}
		else {
			preparedStatement.setNull(index, Types.CLOB);
		}
	}

	@Override
	public String replace(String original, String target, Object owner)
		throws HibernateException {

		return original;
	}

	@Override
	public Class<String> returnedClass() {
		return String.class;
	}

}