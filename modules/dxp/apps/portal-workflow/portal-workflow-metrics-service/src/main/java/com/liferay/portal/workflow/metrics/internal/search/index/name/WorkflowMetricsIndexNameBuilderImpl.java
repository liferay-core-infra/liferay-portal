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

package com.liferay.portal.workflow.metrics.internal.search.index.name;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.workflow.metrics.search.index.WorkflowMetricsIndexEntityNameConstant;
import com.liferay.portal.workflow.metrics.search.index.name.WorkflowMetricsIndexNameBuilder;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jiaxu Wei
 */
@Component(service = WorkflowMetricsIndexNameBuilder.class)
public class WorkflowMetricsIndexNameBuilderImpl
	implements WorkflowMetricsIndexNameBuilder {

	@Override
	public String getIndexName(
		long companyId, String workflowMetricsIndexEntityName) {

		return indexNameBuilder.getIndexName(companyId) +
			_indexNameSuffixMap.get(workflowMetricsIndexEntityName);
	}

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	private static final Map<String, String> _indexNameSuffixMap =
		HashMapBuilder.put(
			WorkflowMetricsIndexEntityNameConstant.INSTANCE,
			"-workflow-metrics-instances"
		).put(
			WorkflowMetricsIndexEntityNameConstant.NODE,
			"-workflow-metrics-nodes"
		).put(
			WorkflowMetricsIndexEntityNameConstant.PROCESS,
			"-workflow-metrics-processes"
		).put(
			WorkflowMetricsIndexEntityNameConstant.SLA_INSTANCE_RESULT,
			"-workflow-metrics-sla-instance-results"
		).put(
			WorkflowMetricsIndexEntityNameConstant.SLA_TASK_RESULT,
			"-workflow-metrics-sla-task-results"
		).put(
			WorkflowMetricsIndexEntityNameConstant.TASK,
			"-workflow-metrics-tasks"
		).put(
			WorkflowMetricsIndexEntityNameConstant.TRANSITION,
			"-workflow-metrics-transitions"
		).build();

}