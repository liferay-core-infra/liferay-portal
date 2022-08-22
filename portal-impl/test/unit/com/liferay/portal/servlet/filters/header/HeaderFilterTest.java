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

package com.liferay.portal.servlet.filters.header;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.util.PortalImpl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Binh TRan
 */
public class HeaderFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());

		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());
	}

	@Before
	public void setUp() {
		_headerFilter.init(_filterConfig);

		HttpSession httpSession = Mockito.mock(HttpSession.class);

		Mockito.when(
			httpSession.isNew()
		).thenReturn(
			false
		);

		_mockHttpServletRequest.setSession(httpSession);
	}

	@After
	public void tearDown() {
		_headerFilter.destroy();
	}

	@Test
	public void testMergeCacheControlHeader() throws Exception {
		_initParameters = HashMapBuilder.put(
			HttpHeaders.CACHE_CONTROL, "max-age=315360000, public"
		).build();

		_headerFilter.processFilter(
			_mockHttpServletRequest, _mockHttpServletResponse,
			ProxyFactory.newDummyInstance(FilterChain.class));

		_initParameters = HashMapBuilder.put(
			HttpHeaders.CACHE_CONTROL, "max-age=12800, no-cache, no-store"
		).build();

		_headerFilter.processFilter(
			_mockHttpServletRequest, _mockHttpServletResponse,
			ProxyFactory.newDummyInstance(FilterChain.class));

		List<String> cacheControlHeaders = _mockHttpServletResponse.getHeaders(
			HttpHeaders.CACHE_CONTROL);

		Assert.assertEquals(
			cacheControlHeaders.toString(), 1, cacheControlHeaders.size());

		Assert.assertEquals(
			"max-age=12800, public, no-cache, no-store",
			cacheControlHeaders.get(0));
	}

	@Test
	public void testOverrideExpiresHeader() throws Exception {
		_initParameters = HashMapBuilder.put(
			HttpHeaders.EXPIRES, "315360000"
		).build();

		_headerFilter.processFilter(
			_mockHttpServletRequest, _mockHttpServletResponse,
			ProxyFactory.newDummyInstance(FilterChain.class));

		List<String> expiresHeaders1 = _mockHttpServletResponse.getHeaders(
			HttpHeaders.EXPIRES);

		Assert.assertEquals(
			expiresHeaders1.toString(), 1, expiresHeaders1.size());

		_initParameters = HashMapBuilder.put(
			HttpHeaders.EXPIRES, "12800"
		).build();

		_headerFilter.processFilter(
			_mockHttpServletRequest, _mockHttpServletResponse,
			ProxyFactory.newDummyInstance(FilterChain.class));

		List<String> expiresHeaders2 = _mockHttpServletResponse.getHeaders(
			HttpHeaders.EXPIRES);

		Assert.assertEquals(
			expiresHeaders2.toString(), 1, expiresHeaders2.size());

		Assert.assertNotEquals(expiresHeaders1.get(0), expiresHeaders2.get(0));
	}

	private final FilterConfig _filterConfig = new FilterConfig() {

		@Override
		public String getFilterName() {
			return "HeaderFilter";
		}

		@Override
		public String getInitParameter(String s) {
			return _initParameters.get(s);
		}

		@Override
		public Enumeration<String> getInitParameterNames() {
			return Collections.enumeration(_initParameters.keySet());
		}

		@Override
		public ServletContext getServletContext() {
			return null;
		}

	};

	private final HeaderFilter _headerFilter = new HeaderFilter();
	private Map<String, String> _initParameters;
	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();

}