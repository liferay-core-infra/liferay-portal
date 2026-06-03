/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.SingleVMPool;
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
import org.osgi.service.component.runtime.dto.ReferenceDTO;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class TemplateManagerPortalCacheReferenceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testNoPortalCacheReference() {
		_testNoPortalCacheReference(
			"com.liferay.portal.template.freemarker",
			"com.liferay.portal.template.freemarker.internal." +
				"FreeMarkerManager");
		_testNoPortalCacheReference(
			"com.liferay.portal.template.velocity",
			"com.liferay.portal.template.velocity.internal.VelocityManager");
	}

	private void _testNoPortalCacheReference(
		String bundleSymbolicName, String componentName) {

		Bundle bundle = BundleUtil.getBundle(
			SystemBundleUtil.getBundleContext(), bundleSymbolicName);

		Assert.assertNotNull(bundle);

		ComponentDescriptionDTO componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				bundle, componentName);

		Assert.assertNotNull(componentDescriptionDTO);

		for (ReferenceDTO referenceDTO : componentDescriptionDTO.references) {
			Assert.assertNotEquals(
				MultiVMPool.class.getName(), referenceDTO.interfaceName);
			Assert.assertNotEquals(
				SingleVMPool.class.getName(), referenceDTO.interfaceName);
		}
	}

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

}