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

package com.liferay.portal.kernel.url;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Julius Lee
 */
public class URLBuilderTest {

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(
			_portal.stripURLAnchor(Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			input -> _testStripURLAnchor(
				(String)input.getArguments()[0],
				(String)input.getArguments()[1])
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	@Test
	public void testAddParameter() {
		String actual = URLBuilder.create(
			_TEST_URL
		).addParameter(
			_TEST_KEY, _TEST_VALUE
		).build();
		String expected = HttpComponentsUtil.addParameter(
			_TEST_URL, _TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(expected, actual);

		actual = URLBuilder.create(
			actual
		).addParameter(
			_TEST_KEY, _TEST_VALUE
		).build();
		expected = HttpComponentsUtil.addParameter(
			expected, _TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testAddParameterWithAnchor() {
		String actual = URLBuilder.create(
			_TEST_URL + _TEST_ANCHOR
		).addParameter(
			_TEST_KEY, _TEST_VALUE
		).build();
		String expected = HttpComponentsUtil.addParameter(
			_TEST_URL + _TEST_ANCHOR, _TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(expected, actual);

		actual = URLBuilder.create(
			actual
		).addParameter(
			_TEST_KEY, _TEST_VALUE
		).build();
		expected = HttpComponentsUtil.addParameter(
			expected, _TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testAddParameterWithNullInput() {
		Assert.assertNull(
			URLBuilder.create(
				null
			).addParameter(
				null, null
			).setParameter(
				null, null
			).build());
	}

	@Test
	public void testOperationalSequence() {
		Assert.assertNotEquals(
			"Builder operational sequence is incorrect",
			URLBuilder.create(
				_TEST_URL + _TEST_ANCHOR
			).removeParameter(
				_TEST_KEY
			).addParameter(
				_TEST_KEY, _TEST_VALUE
			).build(),
			URLBuilder.create(
				_TEST_URL + _TEST_ANCHOR
			).addParameter(
				_TEST_KEY, _TEST_VALUE
			).removeParameter(
				_TEST_KEY
			).build());
	}

	@Test
	public void testRemoveParameter() {
		String testURLWithParam = _TEST_URL + "?tesKey=testValue";

		String actual = URLBuilder.create(
			testURLWithParam
		).removeParameter(
			_TEST_KEY
		).build();

		String expected = HttpComponentsUtil.removeParameter(
			testURLWithParam, _TEST_KEY);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testRemoveParameterWithAnchor() {
		String testURLWithParam = StringBundler.concat(
			_TEST_URL, "?tesKey=testValue", _TEST_ANCHOR);

		String actual = URLBuilder.create(
			testURLWithParam
		).removeParameter(
			_TEST_KEY
		).build();

		String expected = HttpComponentsUtil.removeParameter(
			testURLWithParam, _TEST_KEY);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSetParameter() {
		String actual = URLBuilder.create(
			_TEST_URL
		).setParameter(
			_TEST_KEY, _TEST_VALUE
		).setParameter(
			_TEST_KEY, _TEST_VALUE
		).build();

		String expected = HttpComponentsUtil.setParameter(
			_TEST_URL, _TEST_KEY, _TEST_VALUE);

		expected = HttpComponentsUtil.setParameter(
			expected, _TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSetParameterWithAnchor() {
		String actual = URLBuilder.create(
			_TEST_URL + _TEST_ANCHOR
		).setParameter(
			_TEST_KEY, _TEST_VALUE
		).setParameter(
			_TEST_KEY, _TEST_VALUE
		).build();

		String expected = HttpComponentsUtil.setParameter(
			_TEST_URL + _TEST_ANCHOR, _TEST_KEY, _TEST_VALUE);

		expected = HttpComponentsUtil.setParameter(
			expected, _TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(expected, actual);
	}

	/**
	 * @see com.liferay.portal.util.PortalImpl
	 *
	 * _testStripURLAnchor is copied from PortalImpl for ease of testing
	 */
	private String[] _testStripURLAnchor(String url, String separator) {
		String anchor = StringPool.BLANK;

		int pos = url.indexOf(separator);

		if (pos != -1) {
			anchor = url.substring(pos);
			url = url.substring(0, pos);
		}

		return new String[] {url, anchor};
	}

	private static final String _TEST_ANCHOR = "#TestAnchor";

	private static final String _TEST_KEY = "testKey";

	private static final String _TEST_URL = "http://test.com";

	private static final String _TEST_VALUE = "testValue";

	@Mock
	private Portal _portal;

}