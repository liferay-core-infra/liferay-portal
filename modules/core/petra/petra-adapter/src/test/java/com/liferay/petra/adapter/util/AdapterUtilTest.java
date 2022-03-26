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

package com.liferay.petra.adapter.util;

import com.liferay.petra.reflect.ProxyUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class AdapterUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testAdaptWithListOfDelegateObjects() {
		List<A> aList = new ArrayList<>();

		aList.add(new AImpl(_TEST_OBJECT_ID_ONE));
		aList.add(new AImpl(_TEST_OBJECT_ID_TWO));

		List<B> bList = AdapterUtil.adapt(_testProxyFunction(B.class), aList);

		B b1 = bList.get(0);
		B b2 = bList.get(1);

		Assert.assertEquals(_TEST_OBJECT_ID_ONE, b1.getId());

		Assert.assertEquals(_TEST_OBJECT_ID_TWO, b2.getId());
	}

	@Test
	public void testAdaptWithNullObjectAndNullList() {

		// Test for Null Object and Null List

		Assert.assertNull(AdapterUtil.adapt(_testProxyFunction(A.class), null));

		List<A> aNullList = new ArrayList<>();

		aNullList.add(null);
		aNullList.add(null);

		List<B> bList = AdapterUtil.adapt(
			_testProxyFunction(B.class), aNullList);

		for (B b : bList) {
			Assert.assertNull(b);
		}
	}

	@Test
	public void testAdaptWithSingleDelegatedObject() {
		A a1 = new AImpl(_TEST_OBJECT_ID_ONE);

		A a2 = new AImpl(_TEST_OBJECT_ID_TWO);

		B b2 = AdapterUtil.adapt(_testProxyFunction(B.class), a2);

		B b1 = AdapterUtil.adapt(_testProxyFunction(B.class), a1);

		Assert.assertEquals(_TEST_OBJECT_ID_ONE, b1.getId());

		Assert.assertEquals(a1.getId(), b1.getId());

		Assert.assertNotEquals(b1.getId(), b2.getId());
	}

	@Test
	public void testConstructor() {
		new AdapterUtil();
	}

	@Test
	public void testGetDelegateModel() {
		A a1 = new AImpl(_TEST_OBJECT_ID_ONE);

		B b1 = AdapterUtil.adapt(
			_testProxyFunction(B.class, TestModelWrapper.class), a1);

		Assert.assertEquals(
			ReflectionTestUtil.invoke(b1, "getWrappedModel", null), a1);
	}

	@Test
	public void testMethodCallWithDifferentParameters() {
		Object proxyWithTestInvocationHandler = ProxyUtil.newProxyInstance(
			A.class.getClassLoader(), new Class<?>[] {A.class},
			new TestInvocationHandler(new AImpl(_TEST_OBJECT_ID_ONE)));

		A proxyWithDelegationHandler = AdapterUtil.adapt(
			_testProxyFunction(A.class), new AImpl(_TEST_OBJECT_ID_ONE));

		// pass in a proxy instance with TestInvocationHandler
		// should return proxyWithTestInvocationHandler id

		Assert.assertEquals(
			_TEST_OBJECT_ID_ONE,
			proxyWithDelegationHandler.testWithDifferentParameterType(
				proxyWithTestInvocationHandler));

		// pass in a proxy instance with AdapterUtil.DelegateInvocationHandler
		// should return proxyWithDelegationHandler id

		Assert.assertEquals(
			_TEST_OBJECT_ID_ONE,
			proxyWithDelegationHandler.testWithDifferentParameterType(
				proxyWithDelegationHandler));

		// _TEST_OBJECT_ID_TWO is a String, should return null

		Assert.assertNull(
			proxyWithDelegationHandler.testWithDifferentParameterType(
				_TEST_OBJECT_ID_TWO));
	}

	private <T> Function<InvocationHandler, T> _testProxyFunction(
		Class<?>... interfaceClasses) {

		Class<?> proxyClass = ProxyUtil.getProxyClass(
			interfaceClasses[0].getClassLoader(), interfaceClasses);

		try {
			Constructor<T> constructor =
				(Constructor<T>)proxyClass.getConstructor(
					InvocationHandler.class);

			return invocationHandler -> {
				try {
					return constructor.newInstance(invocationHandler);
				}
				catch (ReflectiveOperationException
							reflectiveOperationException) {

					throw new InternalError(reflectiveOperationException);
				}
			};
		}
		catch (NoSuchMethodException noSuchMethodException) {
			noSuchMethodException.printStackTrace();
		}

		return null;
	}

	private static final String _TEST_OBJECT_ID_ONE = "OBJECT_1";

	private static final String _TEST_OBJECT_ID_TWO = "OBJECT_2";

	private static class AImpl implements A {

		@Override
		public String getId() {
			return _id;
		}

		@Override
		public void setId(String id) {
			_id = id;
		}

		@Override
		public String testWithDifferentParameterType(Object object) {
			if (object instanceof A || object instanceof B) {
				return (String)ReflectionTestUtil.invoke(object, "getId", null);
			}

			return null;
		}

		private AImpl(String id) {
			_id = id;
		}

		private String _id;

	}

	private static class TestInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws ReflectiveOperationException {

			return method.invoke(_delegateObject, args);
		}

		private TestInvocationHandler(Object delegateObject) {
			_delegateObject = delegateObject;
		}

		private final Object _delegateObject;

	}

	private interface A {

		public String getId();

		public void setId(String id);

		public String testWithDifferentParameterType(Object object);

	}

	private interface B {

		public String getId();

		public void setId(String id);

		public String testWithDifferentParameterType(Object object);

	}

	private interface TestModelWrapper<T> {

		public T getWrappedModel();

	}

}