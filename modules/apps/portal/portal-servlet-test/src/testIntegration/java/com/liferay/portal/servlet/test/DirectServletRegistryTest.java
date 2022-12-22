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
import com.liferay.portal.kernel.servlet.DirectServletRegistryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.core.io.FileSystemResourceLoader;
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
	public void setUp() {
		DirectServletRegistryUtil.clearServlets();
	}

	@Test
	public void testDirectServletRegistry() throws Exception {
		_pathContext = _portalUtil.getPathContext();

		String path = "test";

		File file = new File(path);

		file.createNewFile();

		Servlet expectedServlet = new HttpServlet() {
		};

		expectedServlet.init(new MockServletConfig(path));

		DirectServletRegistryUtil.putServlet(
			_pathContext + path, expectedServlet);

		Servlet actualServlet = DirectServletRegistryUtil.getServlet(path);

		Assert.assertEquals(expectedServlet, actualServlet);
	}

	private String _pathContext;

	@Inject
	private Portal _portal;

	@Inject
	private PortalUtil _portalUtil;

}