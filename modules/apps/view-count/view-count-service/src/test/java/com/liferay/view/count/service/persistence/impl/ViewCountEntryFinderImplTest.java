/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.view.count.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class ViewCountEntryFinderImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIncrementViewCountUsesCacheReplication() throws Exception {
		EntityCache entityCache = Mockito.mock(EntityCache.class);

		ViewCountEntryFinderImpl viewCountEntryFinderImpl = Mockito.mock(
			ViewCountEntryFinderImpl.class);

		ReflectionTestUtil.setFieldValue(
			viewCountEntryFinderImpl, "_entityCache", entityCache);

		Mockito.when(
			viewCountEntryFinderImpl.openSession()
		).thenReturn(
			Mockito.mock(Session.class)
		);

		Mockito.doCallRealMethod(
		).when(
			viewCountEntryFinderImpl
		).incrementViewCount(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.anyInt()
		);

		viewCountEntryFinderImpl.incrementViewCount(10L, 20L, 30L, 40);

		Mockito.verify(
			entityCache
		).putResult(
			Mockito.any(Class.class), Mockito.any(BaseModel.class),
			Mockito.eq(false), Mockito.eq(true)
		);
	}

}