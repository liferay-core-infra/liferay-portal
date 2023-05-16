/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.assignment;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.assignment.KaleoTaskAssignmentSelector;
import com.liferay.portal.workflow.kaleo.runtime.assignment.ScriptingKaleoTaskAssignmentSelector;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jiaxu Wei Selton Guedes
 */
@Component(
	property = "scripting.language=java",
	service = KaleoTaskAssignmentSelector.class
)
public class TestJavaScriptingKaleoTaskAssignmentSelector
	implements ScriptingKaleoTaskAssignmentSelector {

	@Override
	public Map<String, ?> getKaleoTaskAssignments(
			KaleoTaskAssignment kaleoTaskAssignment,
			ExecutionContext executionContext)
		throws PortalException {

		_executed = true;

		return HashMapBuilder.put(
			USER_ASSIGNMENT,
			() -> {
				User user = new UserImpl();

				user.setUserId(kaleoTaskAssignment.getUserId());

				return user;
			}
		).build();
	}

	public boolean isExecuted() {
		return _executed;
	}

	private boolean _executed;

}