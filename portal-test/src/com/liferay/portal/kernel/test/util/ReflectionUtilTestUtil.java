/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.reflect.ReflectionUtil;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Janis Zhang
 */
public class ReflectionUtilTestUtil {

	public static MockedStatic<ReflectionUtil> throwForDefineClass(
		Exception exception) {

		MockedStatic<ReflectionUtil> mockedStatic = Mockito.mockStatic(
			ReflectionUtil.class);

		mockedStatic.when(
			() -> ReflectionUtil.getDeclaredMethod(
				ClassLoader.class, "defineClass", String.class, byte[].class,
				int.class, int.class)
		).thenThrow(
			exception
		);

		return mockedStatic;
	}

}