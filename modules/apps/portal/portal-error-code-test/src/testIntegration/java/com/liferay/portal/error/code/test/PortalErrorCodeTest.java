/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.error.code.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.URL;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class PortalErrorCodeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAccessErrorsCodeJsp() throws Exception {
		URLUtil.toString(new URL("http://localhost:8080/errors/code.jsp"));
	}

}