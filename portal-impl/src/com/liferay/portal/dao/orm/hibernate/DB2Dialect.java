/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

/**
 * @author Shepherd Ching
 * @author Jian Cao
 * @author László Csontos
 */
public class DB2Dialect extends org.hibernate.dialect.DB2Dialect {

	public DB2Dialect() {
		registerKeyword("for");
		registerKeyword("optimize");
	}

	@Override
	public String getForUpdateString() {
		return " for read only with rs use and keep exclusive locks";
	}

}