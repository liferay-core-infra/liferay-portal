/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal.jgroups;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Base64;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.jgroups.protocols.SYM_ENCRYPT;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolHook;

/**
 * @author Kevin Lee
 */
public class GenerateSymKeyProtocolHook implements ProtocolHook {

	@Override
	public void afterCreation(Protocol protocol) throws Exception {
		if (!(protocol instanceof SYM_ENCRYPT symEncrypt)) {
			return;
		}

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			if (!dbInspector.hasTable(_TABLE_NAME)) {
				DB db = DBManagerUtil.getDB();

				db.runSQL(
					connection,
					StringBundler.concat(
						"create table ", _TABLE_NAME,
						" (key_ VARCHAR(75) not null primary key)"));
			}

			symEncrypt.setSecretKey(_getSecretKey(connection));
		}
	}

	private SecretKey _getSecretKey(Connection connection) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select key_ from " + _TABLE_NAME)) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"Loaded shared key for cluster link encryption");
					}

					return new SecretKeySpec(
						Base64.decode(resultSet.getString("key_")),
						_KEY_ALGORITHM);
				}
			}
		}

		KeyGenerator keyGenerator = KeyGenerator.getInstance(_KEY_ALGORITHM);

		SecretKey secretKey = keyGenerator.generateKey();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into ", _TABLE_NAME, "(key_) values (?)"))) {

			preparedStatement.setString(
				1, Base64.encode(secretKey.getEncoded()));

			preparedStatement.executeUpdate();
		}

		if (_log.isInfoEnabled()) {
			_log.info("Generated a new shared key for cluster link encryption");
		}

		return secretKey;
	}

	private static final String _KEY_ALGORITHM = "AES";

	private static final String _TABLE_NAME = "ClusterSymKey";

	private static final Log _log = LogFactoryUtil.getLog(
		GenerateSymKeyProtocolHook.class);

}