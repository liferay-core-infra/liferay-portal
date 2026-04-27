/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.rule;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.DeletableSystemGroupThreadLocal;

import java.util.function.Supplier;

/**
 * @author Mikel Lorza
 */
public class DeletableSystemGroupTestUtil {

	public static SafeCloseable setEnabledWithSafeCloseable(boolean enabled) {
		CentralizedThreadLocal<Boolean> originalCentralizedThreadLocal =
			ReflectionTestUtil.getFieldValue(
				DeletableSystemGroupThreadLocal.class, "_enabled");

		Boolean originalValue = originalCentralizedThreadLocal.get();

		originalCentralizedThreadLocal.set(enabled);

		Supplier<Boolean> originalSupplier =
			ReflectionTestUtil.getAndSetFieldValue(
				originalCentralizedThreadLocal, "_supplier", () -> enabled);

		return () -> {
			originalCentralizedThreadLocal.set(originalValue);

			ReflectionTestUtil.setFieldValue(
				originalCentralizedThreadLocal, "_supplier", originalSupplier);
		};
	}

}