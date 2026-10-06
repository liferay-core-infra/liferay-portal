/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Debora Buriti
 */
@RunWith(Arquillian.class)
public class TemplateRestrictedMethodsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetRestrictedMethods() throws Exception {
		User user = TestPropsValues.getUser();

		_testGetRestrictedMethods(user, "${object.attributeGetterFunctions}");
		_testGetRestrictedMethods(user, "${object.digest}");
		_testGetRestrictedMethods(user, "${object.modelAttributes.digest}");
		_testGetRestrictedMethods(user, "${object.modelAttributes.password}");
		_testGetRestrictedMethods(
			user, "${object.modelAttributes.reminderQueryAnswer}");
		_testGetRestrictedMethods(user, "${object.toCacheModel()}");
	}

	private void _testGetRestrictedMethods(User user, String templateContent)
		throws Exception {

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL,
			new StringTemplateResource(
				RandomTestUtil.randomString(), templateContent),
			true);

		template.put("object", user);

		try {
			template.processTemplate(new UnsyncStringWriter());

			Assert.fail(templateContent);
		}
		catch (TemplateException templateException) {
			Throwable throwable = templateException;

			while (throwable.getCause() != null) {
				throwable = throwable.getCause();
			}

			String message = throwable.getMessage();

			Assert.assertTrue(
				message, message.contains("Denied access to method or field "));
		}
	}

}