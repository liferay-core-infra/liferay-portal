/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.shielded.container.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.URL;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Akhash Ramprakash
 */
public class ShieldedContainerClassLoaderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testLoadClassWhenClassLoadingLockIsHeld() throws Exception {
		ProtectionDomain protectionDomain =
			ShieldedContainerClassLoaderTest.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		try (TestShieldedContainerClassLoader testShieldedContainerClassLoader =
				new TestShieldedContainerClassLoader(
					new URL[] {codeSource.getLocation()},
					ShieldedContainerClassLoaderTest.class.getClassLoader())) {

			Class<?> clazz = testShieldedContainerClassLoader.loadClass(
				TestClass.class.getName());

			Assert.assertSame(
				testShieldedContainerClassLoader, clazz.getClassLoader());

			FutureTask<Class<?>> futureTask = new FutureTask<>(
				() -> testShieldedContainerClassLoader.loadClass(
					TestClass.class.getName()));

			synchronized (testShieldedContainerClassLoader.getClassLoadingLock(
				TestClass.class.getName())) {

				Thread thread = new Thread(futureTask);

				thread.start();

				Assert.assertSame(clazz, futureTask.get(10, TimeUnit.SECONDS));
			}
		}
	}

	public static class TestClass {
	}

	private static class TestShieldedContainerClassLoader
		extends ShieldedContainerClassLoader {

		@Override
		public Object getClassLoadingLock(String className) {
			return super.getClassLoadingLock(className);
		}

		private TestShieldedContainerClassLoader(
			URL[] urls, ClassLoader fallbackClassLoader) {

			super(urls, fallbackClassLoader);
		}

		static {
			ClassLoader.registerAsParallelCapable();
		}

	}

}