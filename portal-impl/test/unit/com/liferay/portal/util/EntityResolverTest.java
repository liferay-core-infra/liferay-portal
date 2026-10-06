/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.apache.xerces.xni.XNIException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jiefeng Wu
 */
public class EntityResolverTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test(expected = XNIException.class)
	public void testResolveEntitySecureRejectsExternalSchemaLocation()
		throws Exception {

		EntityResolver entityResolver = new EntityResolver(true);

		entityResolver.resolveEntity(null, "http://127.0.0.1/ssrf-probe.xsd");
	}

	@Test
	public void testResolveEntityUnsecureDefersExternalSchemaLocation()
		throws Exception {

		EntityResolver entityResolver = new EntityResolver();

		Assert.assertNull(
			entityResolver.resolveEntity(
				null, "http://127.0.0.1/ssrf-probe.xsd"));
	}

}