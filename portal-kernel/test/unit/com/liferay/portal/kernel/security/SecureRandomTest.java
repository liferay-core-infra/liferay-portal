/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brian Wulbern
 */
public class SecureRandomTest {

	@Test
	public void testNext() {
		SecureRandom secureRandom = new SecureRandom();

		for (int bits = 1; bits < 32; bits++) {
			for (int i = 0; i < 1000; i++) {
				int value = secureRandom.next(bits);

				Assert.assertTrue((value >= 0) && (value < (1L << bits)));
			}
		}
	}

	@Test
	public void testNextFloat() {
		SecureRandom secureRandom = new SecureRandom();

		for (int i = 0; i < 1000; i++) {
			float value = secureRandom.nextFloat();

			Assert.assertTrue((value >= 0) && (value < 1));
		}
	}

}