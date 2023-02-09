/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.product.navigation.personal.menu.web.internal;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jiaxu Wei
 */
public class PersonalMenuEntryRegistryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testPersonalMenuEntrysSequence() {
		PersonalMenuEntryRegistryImpl personalMenuEntryRegistryImpl =
			new PersonalMenuEntryRegistryImpl();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		personalMenuEntryRegistryImpl.activate(bundleContext);

		Assert.assertTrue(
			ListUtil.isEmpty(
				personalMenuEntryRegistryImpl.getPersonalMenuEntries()));

		PersonalMenuEntry personalMenuEntry11 = new TestPersonalMenuEntry();
		PersonalMenuEntry personalMenuEntry12 = new TestPersonalMenuEntry();
		PersonalMenuEntry personalMenuEntry21 = new TestPersonalMenuEntry();
		PersonalMenuEntry personalMenuEntry22 = new TestPersonalMenuEntry();
		PersonalMenuEntry personalMenuEntry31 = new TestPersonalMenuEntry();
		PersonalMenuEntry personalMenuEntry32 = new TestPersonalMenuEntry();

		ServiceRegistration<PersonalMenuEntry> serviceRegistration11 =
			bundleContext.registerService(
				PersonalMenuEntry.class, personalMenuEntry11,
				HashMapDictionaryBuilder.put(
					"product.navigation.personal.menu.entry.order", 1
				).put(
					"product.navigation.personal.menu.group", 1
				).build());
		ServiceRegistration<PersonalMenuEntry> serviceRegistration32 =
			bundleContext.registerService(
				PersonalMenuEntry.class, personalMenuEntry32,
				HashMapDictionaryBuilder.put(
					"product.navigation.personal.menu.entry.order", 2
				).put(
					"product.navigation.personal.menu.group", 3
				).build());
		ServiceRegistration<PersonalMenuEntry> serviceRegistration22 =
			bundleContext.registerService(
				PersonalMenuEntry.class, personalMenuEntry22,
				HashMapDictionaryBuilder.put(
					"product.navigation.personal.menu.entry.order", 2
				).put(
					"product.navigation.personal.menu.group", 2
				).build());
		ServiceRegistration<PersonalMenuEntry> serviceRegistration21 =
			bundleContext.registerService(
				PersonalMenuEntry.class, personalMenuEntry21,
				HashMapDictionaryBuilder.put(
					"product.navigation.personal.menu.entry.order", 1
				).put(
					"product.navigation.personal.menu.group", 2
				).build());
		ServiceRegistration<PersonalMenuEntry> serviceRegistration31 =
			bundleContext.registerService(
				PersonalMenuEntry.class, personalMenuEntry31,
				HashMapDictionaryBuilder.put(
					"product.navigation.personal.menu.entry.order", 1
				).put(
					"product.navigation.personal.menu.group", 3
				).build());
		ServiceRegistration<PersonalMenuEntry> serviceRegistration12 =
			bundleContext.registerService(
				PersonalMenuEntry.class, personalMenuEntry12,
				HashMapDictionaryBuilder.put(
					"product.navigation.personal.menu.entry.order", 2
				).put(
					"product.navigation.personal.menu.group", 1
				).build());

		List<PersonalMenuEntry> personalMenuEntries =
			personalMenuEntryRegistryImpl.getPersonalMenuEntries();

		PersonalMenuEntry[] actualSequence = personalMenuEntries.toArray(
			new PersonalMenuEntry[0]);

		PersonalMenuEntry[] expectedSequence = {
			personalMenuEntry11, personalMenuEntry12, personalMenuEntry21,
			personalMenuEntry22, personalMenuEntry31, personalMenuEntry32
		};

		Assert.assertArrayEquals(expectedSequence, actualSequence);

		serviceRegistration11.unregister();
		serviceRegistration12.unregister();
		serviceRegistration21.unregister();
		serviceRegistration22.unregister();
		serviceRegistration31.unregister();
		serviceRegistration32.unregister();

		personalMenuEntryRegistryImpl.deactivate();
	}

	private class TestPersonalMenuEntry implements PersonalMenuEntry {

		@Override
		public String getLabel(Locale locale) {
			return null;
		}

		@Override
		public String getPortletURL(HttpServletRequest httpServletRequest)
			throws PortalException {

			return null;
		}

	}

}