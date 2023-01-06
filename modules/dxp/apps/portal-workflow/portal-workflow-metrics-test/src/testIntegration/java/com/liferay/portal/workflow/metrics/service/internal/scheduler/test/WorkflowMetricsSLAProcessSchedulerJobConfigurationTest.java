/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.workflow.metrics.service.internal.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinition;
import com.liferay.portal.workflow.metrics.search.index.WorkflowMetricsIndexEntityNameConstant;
import com.liferay.portal.workflow.metrics.search.index.name.WorkflowMetricsIndexNameBuilder;
import com.liferay.portal.workflow.metrics.service.WorkflowMetricsSLADefinitionLocalService;
import com.liferay.portal.workflow.metrics.service.util.BaseWorkflowMetricsIndexerTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class WorkflowMetricsSLAProcessSchedulerJobConfigurationTest
	extends BaseWorkflowMetricsIndexerTestCase {

	@Test
	public void testProcess() throws Exception {
		assertCount(
			4,
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.NODE),
			"WorkflowMetricsNodeType", "companyId",
			workflowDefinition.getCompanyId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());
		assertCount(
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.PROCESS),
			"WorkflowMetricsProcessType", "companyId",
			workflowDefinition.getCompanyId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());

		WorkflowMetricsSLADefinition workflowMetricsSLADefinition =
			_workflowMetricsSLADefinitionLocalService.
				addWorkflowMetricsSLADefinition(
					StringPool.BLANK, StringPool.BLANK, 50000, "Abc",
					new String[0], workflowDefinition.getWorkflowDefinitionId(),
					new String[] {getInitialNodeKey(workflowDefinition)},
					new String[] {getTerminalNodeKey(workflowDefinition)},
					ServiceContextTestUtil.getServiceContext());

		_workflowMetricsSLADefinitionLocalService.
			deactivateWorkflowMetricsSLADefinition(
				workflowMetricsSLADefinition.
					getWorkflowMetricsSLADefinitionId(),
				ServiceContextTestUtil.getServiceContext());

		assertCount(
			0,
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.SLA_INSTANCE_RESULT),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "processId",
			workflowDefinition.getWorkflowDefinitionId(), "slaDefinitionId",
			workflowMetricsSLADefinition.getWorkflowMetricsSLADefinitionId());

		KaleoInstance kaleoInstance = getKaleoInstance(addBlogsEntry());

		completeKaleoTaskInstanceToken(kaleoInstance);

		completeKaleoInstance(kaleoInstance);

		assertCount(
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.INSTANCE),
			"WorkflowMetricsInstanceType", "className",
			kaleoInstance.getClassName(), "classPK", kaleoInstance.getClassPK(),
			"companyId", kaleoInstance.getCompanyId(), "completed", true,
			"instanceId", kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());
		assertCount(
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.INSTANCE),
			"WorkflowMetricsInstanceType", "className",
			kaleoInstance.getClassName(), "classPK", kaleoInstance.getClassPK(),
			"companyId", kaleoInstance.getCompanyId(), "completed", true,
			"instanceId", kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());
		assertCount(
			0,
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.INSTANCE),
			"WorkflowMetricsInstanceType", "className",
			kaleoInstance.getClassName(), "classPK", kaleoInstance.getClassPK(),
			"companyId", kaleoInstance.getCompanyId(), "completed", false,
			"instanceId", kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());

		UnsafeRunnable<Exception> jobExecutorUnsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		jobExecutorUnsafeRunnable.run();

		assertCount(
			0,
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.SLA_INSTANCE_RESULT),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "processId",
			workflowDefinition.getWorkflowDefinitionId(), "slaDefinitionId",
			workflowMetricsSLADefinition.getWorkflowMetricsSLADefinitionId());

		workflowMetricsSLADefinition =
			_workflowMetricsSLADefinitionLocalService.
				addWorkflowMetricsSLADefinition(
					StringPool.BLANK, StringPool.BLANK, 50000, "Def",
					new String[0], workflowDefinition.getWorkflowDefinitionId(),
					new String[] {getInitialNodeKey(workflowDefinition)},
					new String[] {getTerminalNodeKey(workflowDefinition)},
					ServiceContextTestUtil.getServiceContext());

		kaleoInstance = getKaleoInstance(addBlogsEntry());

		assertCount(
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.INSTANCE),
			"WorkflowMetricsInstanceType", "className",
			kaleoInstance.getClassName(), "classPK", kaleoInstance.getClassPK(),
			"companyId", kaleoInstance.getCompanyId(), "completed", false,
			"instanceId", kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());

		jobExecutorUnsafeRunnable.run();

		assertCount(
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.SLA_INSTANCE_RESULT),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "processId",
			workflowDefinition.getWorkflowDefinitionId(), "slaDefinitionId",
			workflowMetricsSLADefinition.getWorkflowMetricsSLADefinitionId(),
			"status", "RUNNING");
		assertCount(
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.SLA_INSTANCE_RESULT),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "instanceId",
			kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());
		assertCount(
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.SLA_INSTANCE_RESULT),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "instanceId",
			kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId(), "slaDefinitionId",
			workflowMetricsSLADefinition.getWorkflowMetricsSLADefinitionId());

		jobExecutorUnsafeRunnable.run();

		assertCount(
			_workflowMetricsIndexNameBuilder.getIndexName(
				workflowDefinition.getCompanyId(),
				WorkflowMetricsIndexEntityNameConstant.SLA_INSTANCE_RESULT),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "instanceId",
			kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId(), "slaDefinitionId",
			workflowMetricsSLADefinition.getWorkflowMetricsSLADefinitionId());
	}

	@Inject(
		filter = "component.name=*.WorkflowMetricsSLAProcessSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

	@Inject
	private WorkflowMetricsIndexNameBuilder _workflowMetricsIndexNameBuilder;

	@Inject
	private WorkflowMetricsSLADefinitionLocalService
		_workflowMetricsSLADefinitionLocalService;

}