/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.rule;

import com.liferay.portal.kernel.util.DeletableSystemGroupThreadLocal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Mikel Lorza
 */
public class DeletableSystemGroupTestRuleTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAnnotateClassWithDeletableSystemGroup() throws Throwable {
		_evaluate(
			AnnotatedClass.class,
			() -> {
				Assert.assertTrue(DeletableSystemGroupThreadLocal.isEnabled());
				Assert.assertTrue(
					_callInNewThread(
						DeletableSystemGroupThreadLocal::isEnabled));
			});
	}

	@Test
	public void testAnnotateClassWithDeletableSystemGroupDisabled()
		throws Throwable {

		_evaluate(
			AnnotatedDisabledClass.class,
			() -> {
				Assert.assertFalse(DeletableSystemGroupThreadLocal.isEnabled());
				Assert.assertFalse(
					_callInNewThread(
						DeletableSystemGroupThreadLocal::isEnabled));
			});
	}

	@Test
	public void testWithoutAnnotation() throws Throwable {
		_evaluate(
			UnannotatedClass.class,
			() -> Assert.assertFalse(
				DeletableSystemGroupThreadLocal.isEnabled()));
	}

	private Boolean _callInNewThread(Supplier<Boolean> supplier)
		throws Exception {

		AtomicReference<Boolean> atomicReference = new AtomicReference<>();

		Thread thread = new Thread(() -> atomicReference.set(supplier.get()));

		thread.start();

		thread.join();

		return atomicReference.get();
	}

	private void _evaluate(Class<?> testClass, ThrowingRunnable assertions)
		throws Throwable {

		Statement statement = DeletableSystemGroupTestRule.INSTANCE.apply(
			new Statement() {

				@Override
				public void evaluate() throws Throwable {
					assertions.run();
				}

			},
			Description.createSuiteDescription(testClass));

		statement.evaluate();
	}

	@DeletableSystemGroup
	private static class AnnotatedClass {
	}

	@DeletableSystemGroup(enabled = false)
	private static class AnnotatedDisabledClass {
	}

	private static class UnannotatedClass {
	}

	private interface ThrowingRunnable {

		public void run() throws Throwable;

	}

}