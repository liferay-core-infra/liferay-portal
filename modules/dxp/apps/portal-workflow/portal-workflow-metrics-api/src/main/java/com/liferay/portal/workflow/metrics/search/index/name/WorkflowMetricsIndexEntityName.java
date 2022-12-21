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

/**
 * @author Jiaxu Wei
 */
public enum WorkflowMetricsIndexEntityName {

	INSTANCE("instance"), NODE("node"), PROCESS("process"),
	SLA_INSTANCE_RESULT("sla-instance-result"),
	SLA_TASK_RESULT("sla-task-result"), TASK("task"), TRANSITION("transition");

	public String getValue() {
		return _value;
	}

	@Override
	public String toString() {
		return _value;
	}

	private WorkflowMetricsIndexEntityName(String value) {
		_value = value;
	}

	private final String _value;

}