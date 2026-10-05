/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Debora Buriti
 */
public class GroupServiceImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testToParamsMapWhenTypeIsUnsupported() {
		Map<String, Object> paramsMap = ReflectionTestUtil.invoke(
			new GroupServiceImpl(), "_toParamsMap",
			new Class<?>[] {String[].class},
			(Object)new String[] {
				"site:true:boolean", "one:foo:" + StringBuilder.class.getName()
			});

		Assert.assertEquals(paramsMap.toString(), 1, paramsMap.size());
		Assert.assertEquals(Boolean.TRUE, paramsMap.get("site"));
		Assert.assertFalse(paramsMap.containsKey("one"));
	}

}