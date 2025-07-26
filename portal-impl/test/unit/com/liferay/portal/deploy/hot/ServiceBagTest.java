/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.portal.kernel.bean.ClassLoaderBeanHandler;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceWrapper;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.service.impl.LayoutLocalServiceImpl;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Kevin Lee
 */
public class ServiceBagTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testReplaceWithDifferentProxies() {
		LayoutLocalService service = new LayoutLocalServiceImpl();

		TestAopInvocationHandler invocationHandler =
			new TestAopInvocationHandler();

		ServiceBag<LayoutLocalService> serviceBag = new ServiceBag<>(
			invocationHandler, LayoutLocalService.class,
			new LayoutLocalServiceWrapper(service),
			Mockito.mock(BundleContext.class),
			Mockito.mock(ServiceReference.class));

		invocationHandler.setTarget(
			ProxyUtil.newProxyInstance(
				ServiceBagTest.class.getClassLoader(),
				new Class<?>[] {LayoutLocalService.class},
				new ClassLoaderBeanHandler(
					invocationHandler.getTarget(),
					ServiceBagTest.class.getClassLoader())));

		serviceBag.replace();

		Assert.assertSame(service, invocationHandler.getTarget());
	}

	private static class TestAopInvocationHandler extends AopInvocationHandler {

		public TestAopInvocationHandler() {
			super(null, null, null);
		}

	}

}