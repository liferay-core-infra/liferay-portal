/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.test.rule.ClassTestRule;
import com.liferay.portal.kernel.test.util.DestinationFactoryTestUtil;

import org.junit.runner.Description;

/**
 * @author Dante Wang
 */
public class DestinationFactoryTestRule extends ClassTestRule<AutoCloseable> {

	public static final DestinationFactoryTestRule INSTANCE =
		new DestinationFactoryTestRule();

	@Override
	protected void afterClass(
			Description description, AutoCloseable autoCloseable)
		throws Throwable {

		autoCloseable.close();
	}

	@Override
	protected AutoCloseable beforeClass(Description description)
		throws Throwable {

		return DestinationFactoryTestUtil.swapDestinationFactory();
	}

}