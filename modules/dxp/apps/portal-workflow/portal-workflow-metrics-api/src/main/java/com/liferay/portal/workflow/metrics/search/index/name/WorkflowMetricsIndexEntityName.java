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

package com.liferay.portal.workflow.metrics.search.index.name;

import java.util.Objects;

/**
 * @author Jiaxu Wei
 */
public enum WorkflowMetricsIndexEntityName {

	INSTANCES("instance", "instances"), NODES("node", "nodes"),
	PROCESSES("process", "processes"),
	SLA_INSTANCE_RESULTS("sla-instance-result", "sla-instance-results"),
	SLA_TASK_RESULTS("sla-task-result", "sla-task-results"),
	TASKS("task", "tasks"), TRANSITIONS("transition", "transitions");

	public static WorkflowMetricsIndexEntityName parse(String key) {
		if (Objects.equals(INSTANCES.getKey(), key)) {
			return INSTANCES;
		}
		else if (Objects.equals(NODES.getKey(), key)) {
			return NODES;
		}
		else if (Objects.equals(PROCESSES.getKey(), key)) {
			return PROCESSES;
		}
		else if (Objects.equals(SLA_INSTANCE_RESULTS.getKey(), key)) {
			return SLA_INSTANCE_RESULTS;
		}
		else if (Objects.equals(SLA_TASK_RESULTS.getKey(), key)) {
			return SLA_TASK_RESULTS;
		}
		else if (Objects.equals(TASKS.getKey(), key)) {
			return TASKS;
		}
		else if (Objects.equals(TRANSITIONS.getKey(), key)) {
			return TRANSITIONS;
		}

		throw new IllegalArgumentException("Invalid value " + key);
	}

	public String getKey() {
		return _key;
	}

	@Override
	public String toString() {
		return _value;
	}

	private WorkflowMetricsIndexEntityName(String key, String value) {
		_key = key;
		_value = value;
	}

	private final String _key;
	private final String _value;

}