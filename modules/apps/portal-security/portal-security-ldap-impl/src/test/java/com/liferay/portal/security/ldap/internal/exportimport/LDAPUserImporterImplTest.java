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

package com.liferay.portal.security.ldap.internal.exportimport;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.CalendarFactoryImpl;

import javax.naming.InvalidNameException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jorge Díaz
 */
public class LDAPUserImporterImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_ldapUserImporterImpl, "_calendarFactory",
			new CalendarFactoryImpl());
	}

	@Test
	public void testBindingInNamespaceEscape() throws InvalidNameException {
		Assert.assertEquals(
			"cn=User\\\\,with\\\\,commas,ou=users,dc=example,dc=com",
			escapeLDAPName(
				"cn=User\\,with\\,commas,ou=users,dc=example,dc=com"));
		Assert.assertEquals(
			"cn=User\\\\2cwith\\\\2ccommas,ou=users,dc=example,dc=com",
			escapeLDAPName(
				"cn=User\\2cwith\\2ccommas,ou=users,dc=example,dc=com"));
	}

	protected String escapeLDAPName(String query) {
		return _ldapUserImporterImpl.escapeLDAPName(query);
	}

	private static final LDAPUserImporterImpl _ldapUserImporterImpl =
		new LDAPUserImporterImpl();

}