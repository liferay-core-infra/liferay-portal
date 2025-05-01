/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.internal.jmx;

import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael C. Han
 */
public class DestinationStatisticsManagerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRegisterMBean() throws Exception {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

		DestinationDefinition destinationDefinition = Mockito.mock(
			DestinationDefinition.class);

		Mockito.when(
			destinationDefinition.getDestinationName()
		).thenReturn(
			"test"
		);

		ObjectName objectName = new ObjectName(
			"com.liferay.portal.messaging:classification=" +
				"messaging_destination,name=MessagingDestinationStatistics-" +
					destinationDefinition.getDestinationName());

		mBeanServer.registerMBean(
			new DestinationStatisticsManager(destinationDefinition, null),
			objectName);

		Assert.assertTrue(mBeanServer.isRegistered(objectName));
	}

}