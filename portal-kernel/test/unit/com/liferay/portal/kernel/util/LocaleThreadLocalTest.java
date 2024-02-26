/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.ReflectionTestUtil;

import java.util.Locale;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 */
public class LocaleThreadLocalTest {

	@Test
	public void testRemoveDefaultLocaleWithSafeCloseable() throws Exception {
		Locale initialValue = LocaleUtil.GERMAN;

		LocaleThreadLocal.setDefaultLocale(LocaleUtil.CANADA);

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					(CentralizedThreadLocal<?>)ReflectionTestUtil.getFieldValue(
						LocaleThreadLocal.class, "_defaultLocale"),
					"_supplier", (Supplier<Locale>)() -> initialValue);
			SafeCloseable safeCloseable =
				LocaleThreadLocal.removeDefaultLocaleWithSafeCloseable()) {

			Assert.assertSame(
				initialValue, LocaleThreadLocal.getDefaultLocale());
		}

		Assert.assertSame(
			LocaleUtil.CANADA, LocaleThreadLocal.getDefaultLocale());
	}

}