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
		URLBuilder urlBuilder = URLBuilder.create(
			_TEST_URL
		).addParameter(
			_TEST_KEY, _TEST_VALUE
		);

		Assert.assertEquals(_TEST_URL_WITH_PARAM, urlBuilder.build());

		urlBuilder.addParameter(_TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(
			_TEST_URL_WITH_PARAM + "&testKey=testValue", urlBuilder.build());
	}

	@Test
	public void testAddParameterWithAnchor() {
		URLBuilder urlBuilder = URLBuilder.create(
			_TEST_URL + _TEST_ANCHOR
		).addParameter(
			_TEST_KEY, _TEST_VALUE
		);

		Assert.assertEquals(
			_TEST_URL_WITH_PARAM + _TEST_ANCHOR, urlBuilder.build());

		urlBuilder.addParameter(
			_TEST_KEY, _TEST_VALUE
		).build();

		Assert.assertEquals(
			_TEST_URL_WITH_PARAM + "&testKey=testValue#TestAnchor",
			urlBuilder.build());
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
	public void testInOrderMultipleOperations() {
		URLBuilder urlBuilder = URLBuilder.create(_TEST_URL);

		StringBundler sb = new StringBundler(22);

		sb.append(_TEST_URL);
		sb.append(StringPool.QUESTION);

		for (int i = 1; i <= 5; i++) {
			urlBuilder.addParameter(_TEST_KEY + i, _TEST_VALUE + i);

			sb.append(_TEST_KEY + i);
			sb.append(StringPool.EQUAL);
			sb.append(_TEST_VALUE + i);
			sb.append(StringPool.AMPERSAND);
		}

		sb.setIndex(sb.index() - 1);

		Assert.assertEquals(sb.toString(), urlBuilder.build());
	}

	@Test
	public void testRemoveParameter() {
		Assert.assertEquals(
			_TEST_URL,
			URLBuilder.create(
				_TEST_URL_WITH_PARAM
			).removeParameter(
				_TEST_KEY
			).build());
	}

	@Test
	public void testRemoveParameterWithAnchor() {
		Assert.assertEquals(
			_TEST_URL + _TEST_ANCHOR,
			URLBuilder.create(
				_TEST_URL_WITH_PARAM + _TEST_ANCHOR
			).removeParameter(
				_TEST_KEY
			).build());
	}

	@Test
	public void testSetParameter() {
		Assert.assertEquals(
			_TEST_URL_WITH_PARAM,
			URLBuilder.create(
				_TEST_URL
			).setParameter(
				_TEST_KEY, _TEST_VALUE
			).build());

		Assert.assertEquals(
			_TEST_URL_WITH_PARAM + "Replaced",
			URLBuilder.create(
				_TEST_URL
			).setParameter(
				_TEST_KEY, _TEST_VALUE
			).setParameter(
				_TEST_KEY, _TEST_VALUE + "Replaced"
			).build());
	}

	@Test
	public void testSetParameterWithAnchor() {
		Assert.assertEquals(
			_TEST_URL_WITH_PARAM + _TEST_ANCHOR,
			URLBuilder.create(
				_TEST_URL + _TEST_ANCHOR
			).setParameter(
				_TEST_KEY, _TEST_VALUE
			).build());

		Assert.assertEquals(
			_TEST_URL_WITH_PARAM + "Replaced#TestAnchor",
			URLBuilder.create(
				_TEST_URL + _TEST_ANCHOR
			).setParameter(
				_TEST_KEY, _TEST_VALUE
			).setParameter(
				_TEST_KEY, _TEST_VALUE + "Replaced"
			).build());
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

	private static final String _TEST_URL_WITH_PARAM =
		"http://test.com?testKey=testValue";

	private static final String _TEST_VALUE = "testValue";

	@Mock
	private Portal _portal;

}