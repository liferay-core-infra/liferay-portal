/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.util.comparator;

import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;

/**
 * @author Leonardo Barros
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class WorkflowDefinitionNameComparator
	extends OrderByComparator<WorkflowDefinition> {

	public static final String ORDER_BY_ASC = "name ASC";

	public static final String ORDER_BY_DESC = "name DESC";

	public static final String[] ORDER_BY_FIELDS = {"name"};

	public static WorkflowDefinitionNameComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		WorkflowDefinition workflowDefinition1,
		WorkflowDefinition workflowDefinition2) {

		int value = Boolean.compare(
			workflowDefinition1.isActive(), workflowDefinition2.isActive());

		if (value == 0) {
			String workflowDefinitionName1 = workflowDefinition1.getName();
			String workflowDefinitionName2 = workflowDefinition2.getName();

			value = workflowDefinitionName1.compareTo(workflowDefinitionName2);

			if (_ascending) {
				return value;
			}
		}

		return -value;
	}

	@Override
	public String getOrderBy() {
		if (_ascending) {
			return ORDER_BY_ASC;
		}

		return ORDER_BY_DESC;
	}

	@Override
	public String[] getOrderByFields() {
		return ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private WorkflowDefinitionNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final WorkflowDefinitionNameComparator _INSTANCE_ASCENDING =
		new WorkflowDefinitionNameComparator(true);

	private static final WorkflowDefinitionNameComparator _INSTANCE_DESCENDING =
		new WorkflowDefinitionNameComparator(false);

	private final boolean _ascending;

}