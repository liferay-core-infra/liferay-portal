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

package com.liferay.portal.kernel.test.util;

import com.liferay.portal.kernel.messaging.InvokerMessageListener;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListenerWrapper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule.SyncHandler;

import java.util.List;

/**
 * @author Hai Yu
 */
public class SchedulerDispatchMessageListenerTestUtil {

	public static MessageListener getSchedulerDispatchMessageListener(
		String className) {

		SyncHandler syncHandler = (SyncHandler)ReflectionTestUtil.getFieldValue(
			SynchronousDestinationTestRule.INSTANCE, "_syncHandler");

		List<InvokerMessageListener> invokerMessageListeners =
			(List<InvokerMessageListener>)ReflectionTestUtil.getFieldValue(
				syncHandler, "_schedulerInvokerMessageListeners");

		for (InvokerMessageListener invokerMessageListener :
				invokerMessageListeners) {

			SchedulerEventMessageListenerWrapper
				schedulerEventMessageListenerWrapper =
					(SchedulerEventMessageListenerWrapper)
						invokerMessageListener.getMessageListener();

			MessageListener messageListener =
				(MessageListener)ReflectionTestUtil.getFieldValue(
					schedulerEventMessageListenerWrapper, "_messageListener");

			Class<?> messageListenerClass = messageListener.getClass();

			if (className.equals(messageListenerClass.getName())) {
				return messageListener;
			}
		}

		return null;
	}

}