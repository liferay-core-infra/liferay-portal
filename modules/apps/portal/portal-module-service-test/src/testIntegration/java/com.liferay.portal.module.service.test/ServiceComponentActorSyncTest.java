/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.module.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class ServiceComponentActorSyncTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testActivate() {
		Bundle bundle = BundleUtil.getBundle(
			SystemBundleUtil.getBundleContext(), _BUNDLE_SYMBOLIC_NAME);

		Assert.assertNotNull(bundle);

		ComponentDescriptionDTO componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				bundle, _CLASS_NAME);

		Assert.assertNotNull(componentDescriptionDTO);
		Assert.assertFalse(
			_serviceComponentRuntime.isComponentEnabled(
				componentDescriptionDTO));
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		"com.liferay.portal.service.component.actor.sync";

	private static final String _CLASS_NAME =
		"com.liferay.portal.service.component.actor.sync.internal." +
			"ServiceComponentActorSync";

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

}