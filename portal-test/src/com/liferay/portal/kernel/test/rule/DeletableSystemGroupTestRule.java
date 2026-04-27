/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.rule;

import com.liferay.petra.lang.SafeCloseable;

import org.junit.runner.Description;

/**
 * @author Mikel Lorza
 */
public class DeletableSystemGroupTestRule extends AbstractTestRule<Void, Void> {

	public static final DeletableSystemGroupTestRule INSTANCE =
		new DeletableSystemGroupTestRule();

	@Override
	protected void afterClass(Description description, Void previousValue) {
		if (_safeCloseable != null) {
			_safeCloseable.close();

			_safeCloseable = null;
		}
	}

	@Override
	protected void afterMethod(
		Description description, Void previousValue, Object target) {
	}

	@Override
	protected Void beforeClass(Description description) {
		DeletableSystemGroup deletableSystemGroup = description.getAnnotation(
			DeletableSystemGroup.class);

		if (deletableSystemGroup != null) {
			_safeCloseable =
				DeletableSystemGroupTestUtil.setEnabledWithSafeCloseable(
					deletableSystemGroup.enabled());
		}

		return null;
	}

	@Override
	protected Void beforeMethod(Description description, Object target) {
		return null;
	}

	private SafeCloseable _safeCloseable;

}