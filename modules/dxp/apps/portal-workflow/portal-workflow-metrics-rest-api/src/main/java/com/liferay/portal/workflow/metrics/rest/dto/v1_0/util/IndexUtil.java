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

package com.liferay.portal.workflow.metrics.rest.dto.v1_0.util;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Index;
import com.liferay.portal.workflow.metrics.search.index.WorkflowMetricsIndexEntityNameConstant;

import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Rafael Praxedes
 */
public class IndexUtil {

	public static String getLabel(String indexEntityName) {
		return _labelMap.get(indexEntityName);
	}

	public static Index toIndex(
		String indexEntityName, Language language,
		ResourceBundle resourceBundle) {

		return new Index() {
			{
				key = indexEntityName;
				label = language.get(
					resourceBundle, _labelMap.get(indexEntityName));

				setGroup(
					() -> {
						if (indexEntityName.startsWith("sla")) {
							return Index.Group.SLA;
						}

						return Index.Group.METRIC;
					});
			}
		};
	}

	private static final Map<String, String> _labelMap =
		LinkedHashMapBuilder.put(
			WorkflowMetricsIndexEntityNameConstant.INSTANCE,
			"workflow-metrics-instances"
		).put(
			WorkflowMetricsIndexEntityNameConstant.NODE,
			"workflow-metrics-nodes"
		).put(
			WorkflowMetricsIndexEntityNameConstant.PROCESS,
			"workflow-metrics-processes"
		).put(
			WorkflowMetricsIndexEntityNameConstant.SLA_INSTANCE_RESULT,
			"workflow-sla-instance-results"
		).put(
			WorkflowMetricsIndexEntityNameConstant.SLA_TASK_RESULT,
			"workflow-sla-task-results"
		).put(
			WorkflowMetricsIndexEntityNameConstant.TASK,
			"workflow-metrics-tasks"
		).put(
			WorkflowMetricsIndexEntityNameConstant.TRANSITION,
			"workflow-metrics-transitions"
		).build();

}