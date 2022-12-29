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

package com.liferay.portal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.servlet.DirectServletRegistryImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockServletConfig;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class DirectServletRegistryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_directServletRegistryImpl = new DirectServletRegistryImpl();

		_file = new File(_PATH);

		_file.createNewFile();
	}

	@After
	public void tearDown() {
		_file.delete();

		ReflectionTestUtil.setFieldValue(
			_directServletRegistryImpl, "_reloadDependants", true);
	}

	@Test
	public void testDirectServletRegistry() throws Exception {
		String pathContextPrefix =
			_portal.getPathProxy() + _portal.getPathContext();

		Servlet expectedServlet = new HttpServlet() {
		};

		expectedServlet.init(new MockServletConfig(_PATH));

		_directServletRegistryImpl.putServlet(
			pathContextPrefix + _PATH, expectedServlet);

		Servlet actualServlet = _directServletRegistryImpl.getServlet(_PATH);

		Assert.assertEquals(expectedServlet, actualServlet);
	}

	@Test
	public void testDirectServletRegistryWithPathModulePrefix()
		throws Exception {

		String pathModulePrefix = StringBundler.concat(
			_portal.getPathProxy(), _portal.getPathContext(),
			Portal.PATH_MODULE, StringPool.SLASH);

		Servlet actualServlet = _directServletRegistryImpl.getServlet(
			pathModulePrefix + _PATH);

		Assert.assertNull(actualServlet);

		Servlet expectedServlet = new HttpServlet() {
		};

		expectedServlet.init(new MockServletConfig(_PATH));

		_directServletRegistryImpl.putServlet(
			pathModulePrefix + _PATH, expectedServlet);

		actualServlet = _directServletRegistryImpl.getServlet(_PATH);

		Assert.assertNull(actualServlet);

		_directServletRegistryImpl.putServlet(_PATH, expectedServlet);

		actualServlet = _directServletRegistryImpl.getServlet(_PATH);

		Assert.assertEquals(expectedServlet, actualServlet);
	}

	private static final String _PATH = "test";

	private DirectServletRegistryImpl _directServletRegistryImpl;
	private File _file;

	@Inject
	private Portal _portal;

}