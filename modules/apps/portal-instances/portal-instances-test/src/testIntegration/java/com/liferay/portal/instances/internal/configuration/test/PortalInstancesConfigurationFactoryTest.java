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

package com.liferay.portal.instances.internal.configuration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class PortalInstancesConfigurationFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Test
	public void test() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			PortalInstancesConfigurationFactoryTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		CountDownLatch countDownLatch = new CountDownLatch(1);

		String webId = RandomTestUtil.randomString();

		ServiceRegistration<PortalInstanceLifecycleListener>
			portalInstanceLifecycleListenerServiceRegistration =
				bundleContext.registerService(
					PortalInstanceLifecycleListener.class,
					new PortalInstanceLifecycleListener() {

						@Override
						public void portalInstanceRegistered(Company company)
							throws Exception {

							if (webId.equals(company.getWebId())) {
								countDownLatch.countDown();
							}
						}

						@Override
						public void portalInstanceUnregistered(Company company)
							throws Exception {
						}

					},
					null);

		Configuration configuration =
			_configurationAdmin.getFactoryConfiguration(
				"com.liferay.portal.instances.internal.configuration." +
					"PortalInstancesConfiguration",
				webId, StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"mx", webId.concat(".foo.bar")
			).put(
				"virtualHostname", webId.concat(".foo.bar")
			).build());

		// Wait for company to be created

		countDownLatch.await();

		_company = _companyLocalService.getCompanyByWebId(webId);

		Assert.assertNotNull(_company);

		ConfigurationTestUtil.deleteConfiguration(configuration);

		portalInstanceLifecycleListenerServiceRegistration.unregister();
	}

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

}