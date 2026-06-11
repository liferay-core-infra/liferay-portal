/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.function.Function;

/**
 * @author Manuel de la Peña
 * @author Brian Wing Shun Chan
 */
public class DefaultSQLTransformer implements SQLTransformer {

	public DefaultSQLTransformer(Function<String, String>[] functions) {
		_functions = functions;
	}

	@Override
	public String transform(String sql) {
		if ((_functions == null) || (sql == null)) {
			return sql;
		}

		String transformedSQL = sql;

		for (Function<String, String> function : _functions) {
			transformedSQL = function.apply(transformedSQL);
		}

		if (transformedSQL.contains("?")) {
			StringBundler sb = new StringBundler();

			int counter = 1;
			boolean quoted = false;

			for (int i = 0; i < transformedSQL.length(); i++) {
				char c = transformedSQL.charAt(i);

				if (c == '\'') {
					quoted = !quoted;
				}

				if ((c == '?') && !quoted) {
					sb.append('?');
					sb.append(counter++);
				}
				else {
					sb.append(c);
				}
			}

			transformedSQL = sb.toString();
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Original SQL: " + sql);
			_log.debug("Transformed SQL: " + transformedSQL);
		}

		return transformedSQL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultSQLTransformer.class);

	private final Function<String, String>[] _functions;

}