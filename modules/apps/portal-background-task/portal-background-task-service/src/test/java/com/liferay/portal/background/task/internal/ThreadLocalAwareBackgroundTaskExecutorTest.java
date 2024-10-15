/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class ThreadLocalAwareBackgroundTaskExecutorTest
	extends BaseBackgroundTaskTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testStaleBackgroundTaskIsSkipped() throws Exception {
		HashMap<String, Serializable> backgroundValues =
			HashMapBuilder.<String, Serializable>put(
				"clusterInvoke", true
			).build();

		backgroundTaskThreadLocalManagerImpl.setThreadLocalValues(
			COMPANY_ID, backgroundValues);

		CompanyLocalService companyLocalService = Mockito.mock(
			CompanyLocalService.class);

		Mockito.when(
			companyLocalService.fetchCompany(Mockito.anyLong())
		).thenReturn(
			null
		).thenReturn(
			Mockito.mock(Company.class)
		);

		backgroundTaskThreadLocalManagerImpl.companyLocalService =
			companyLocalService;

		BackgroundTaskExecutor backgroundTaskExecutor = Mockito.mock(
			BackgroundTaskExecutor.class);

		ThreadLocalAwareBackgroundTaskExecutor
			threadLocalAwareBackgroundTaskExecutor =
				new ThreadLocalAwareBackgroundTaskExecutor(
					backgroundTaskExecutor,
					backgroundTaskThreadLocalManagerImpl);

		BackgroundTask backgroundTask = Mockito.mock(BackgroundTask.class);

		HashMap<String, Serializable> threadLocalValues =
			HashMapBuilder.<String, Serializable>put(
				"clusterInvoke", !(boolean)backgroundValues.get("clusterInvoke")
			).build();

		Assert.assertNotEquals(
			backgroundValues.get("clusterInvoke"),
			threadLocalValues.get("clusterInvoke"));

		backgroundTask.setTaskContextMap(threadLocalValues);

		Mockito.when(
			backgroundTask.getCompanyId()
		).thenReturn(
			1L
		);

		Mockito.when(
			backgroundTask.getTaskContextMap()
		).thenReturn(
			Collections.singletonMap(
				BackgroundTaskThreadLocalManagerImpl.KEY_THREAD_LOCAL_VALUES,
				threadLocalValues)
		);

		BackgroundTaskResult backgroundTaskResult =
			threadLocalAwareBackgroundTaskExecutor.execute(backgroundTask);

		Assert.assertTrue(backgroundTaskResult.isSuccessful());

		Assert.assertEquals(
			backgroundTaskThreadLocalManagerImpl.getThreadLocalValues(
			).get(
				"clusterInvoke"
			),
			backgroundValues.get("clusterInvoke"));

		Mockito.verifyNoInteractions(backgroundTaskExecutor);
	}

}