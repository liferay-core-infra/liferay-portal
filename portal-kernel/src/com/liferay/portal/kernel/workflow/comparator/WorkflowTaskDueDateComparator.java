/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow.comparator;

import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowTask;

import java.util.Date;

/**
 * @author Shuyang Zhou
 */
public class WorkflowTaskDueDateComparator
	extends OrderByComparator<WorkflowTask> {

	public WorkflowTaskDueDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	@Override
	public int compare(WorkflowTask workflowTask1, WorkflowTask workflowTask2) {
		Date dueDate1 = workflowTask1.getDueDate();
		Date dueDate2 = workflowTask2.getDueDate();

		int value = dueDate1.compareTo(dueDate2);

		if (value == 0) {
			Long workflowTaskId1 = workflowTask1.getWorkflowTaskId();
			Long workflowTaskId2 = workflowTask2.getWorkflowTaskId();

			value = workflowTaskId1.compareTo(workflowTaskId2);
		}

		if (_ascending) {
			return value;
		}

		return -value;
	}

	@Override
	public String getOrderBy() {
		if (isAscending()) {
			return _ORDER_BY_ASC;
		}

		return _ORDER_BY_DESC;
	}

	@Override
	public String[] getOrderByFields() {
		return _ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	@Override
	public boolean isAscending(String field) {
		if (field.equals("completed")) {
			return true;
		}

		return super.isAscending(field);
	}

	private static final String _ORDER_BY_ASC =
		"completed ASC, dueDate ASC, modifiedDate ASC, kaleoTaskId ASC";

	private static final String _ORDER_BY_DESC =
		"completed ASC, dueDate DESC, modifiedDate DESC, kaleoTaskId DESC";

	private static final String[] _ORDER_BY_FIELDS = {
		"completed", "dueDate", "modifiedDate", "kaleoTaskId"
	};

	private final boolean _ascending;

}