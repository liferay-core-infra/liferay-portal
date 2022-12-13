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

package com.liferay.portal.workflow.metrics.service.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinition;
import com.liferay.portal.workflow.metrics.search.index.name.WorkflowMetricsIndexNameBuilderRegistry;
import com.liferay.portal.workflow.metrics.service.WorkflowMetricsSLADefinitionLocalService;
import com.liferay.portal.workflow.metrics.service.util.BaseWorkflowMetricsIndexerTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class WorkflowMetricsSLAProcessMessageListenerTest
	extends BaseWorkflowMetricsIndexerTestCase {

	@Test
	public void testProcess() throws Exception {
		assertCount(
			4,
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "node"),
			"WorkflowMetricsNodeType", "companyId",
			workflowDefinition.getCompanyId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());
		assertCount(
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "process"),
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
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "sla-instance-result"),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "processId",
			workflowDefinition.getWorkflowDefinitionId(), "slaDefinitionId",
			workflowMetricsSLADefinition.getWorkflowMetricsSLADefinitionId());

		KaleoInstance kaleoInstance = getKaleoInstance(addBlogsEntry());

		completeKaleoTaskInstanceToken(kaleoInstance);

		completeKaleoInstance(kaleoInstance);

		assertCount(
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "instance"),
			"WorkflowMetricsInstanceType", "className",
			kaleoInstance.getClassName(), "classPK", kaleoInstance.getClassPK(),
			"companyId", kaleoInstance.getCompanyId(), "completed", true,
			"instanceId", kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());
		assertCount(
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "instance"),
			"WorkflowMetricsInstanceType", "className",
			kaleoInstance.getClassName(), "classPK", kaleoInstance.getClassPK(),
			"companyId", kaleoInstance.getCompanyId(), "completed", true,
			"instanceId", kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());
		assertCount(
			0,
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "instance"),
			"WorkflowMetricsInstanceType", "className",
			kaleoInstance.getClassName(), "classPK", kaleoInstance.getClassPK(),
			"companyId", kaleoInstance.getCompanyId(), "completed", false,
			"instanceId", kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());

		_workflowMetricsSLAProcessMessageListener.receive(new Message());

		assertCount(
			0,
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "sla-instance-result"),
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
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "instance"),
			"WorkflowMetricsInstanceType", "className",
			kaleoInstance.getClassName(), "classPK", kaleoInstance.getClassPK(),
			"companyId", kaleoInstance.getCompanyId(), "completed", false,
			"instanceId", kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());

		_workflowMetricsSLAProcessMessageListener.receive(new Message());

		assertCount(
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "sla-instance-result"),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "processId",
			workflowDefinition.getWorkflowDefinitionId(), "slaDefinitionId",
			workflowMetricsSLADefinition.getWorkflowMetricsSLADefinitionId(),
			"status", "RUNNING");
		assertCount(
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "sla-instance-result"),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "instanceId",
			kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId());
		assertCount(
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "sla-instance-result"),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "instanceId",
			kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId(), "slaDefinitionId",
			workflowMetricsSLADefinition.getWorkflowMetricsSLADefinitionId());

		_workflowMetricsSLAProcessMessageListener.receive(new Message());

		assertCount(
			_workflowMetricsIndexNameBuilderRegistry.getIndexName(
				workflowDefinition.getCompanyId(), "sla-instance-result"),
			"WorkflowMetricsSLAInstanceResultType", "companyId",
			workflowDefinition.getCompanyId(), "instanceId",
			kaleoInstance.getKaleoInstanceId(), "processId",
			workflowDefinition.getWorkflowDefinitionId(), "slaDefinitionId",
			workflowMetricsSLADefinition.getWorkflowMetricsSLADefinitionId());
	}

	@Inject
	private WorkflowMetricsIndexNameBuilderRegistry
		_workflowMetricsIndexNameBuilderRegistry;

	@Inject
	private WorkflowMetricsSLADefinitionLocalService
		_workflowMetricsSLADefinitionLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.portal.workflow.metrics.internal.messaging.WorkflowMetricsSLAProcessMessageListener))"
	)
	private MessageListener _workflowMetricsSLAProcessMessageListener;

}