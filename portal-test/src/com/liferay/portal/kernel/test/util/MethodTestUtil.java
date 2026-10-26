/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.implementation.ExceptionMethod;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author Tina Tian
 */
public class MethodTestUtil {

	public static AutoCloseable throwException(String message, Method method) {
		ResettableClassFileTransformer resettableClassFileTransformer =
			new AgentBuilder.Default(
			).disableClassFormatChanges(
			).ignore(
				ElementMatchers.none()
			).with(
				AgentBuilder.RedefinitionStrategy.RETRANSFORMATION
			).type(
				ElementMatchers.is(method.getDeclaringClass())
			).transform(
				(builder, typeDescription, classLoader, module,
				 protectionDomain) -> builder.method(
					ElementMatchers.is(method)
				).intercept(
					ExceptionMethod.throwing(Exception.class, message)
				)
			).installOn(
				_instrumentation
			);

		return () -> resettableClassFileTransformer.reset(
			_instrumentation,
			AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
	}

	private static final Instrumentation _instrumentation =
		ByteBuddyAgent.install();

}