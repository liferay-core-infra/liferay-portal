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

package com.liferay.portal.workflow.kaleo.runtime.assignment;

import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.workflow.kaleo.KaleoTaskAssignmentFactory;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@ProviderType
public abstract class BaseKaleoTaskAssignmentSelector
	implements KaleoTaskAssignmentSelector {

	@SuppressWarnings("unchecked")
	protected Collection<KaleoTaskAssignment> getKaleoTaskAssignments(
		Map<String, ?> results) {

		List<KaleoTaskAssignment> kaleoTaskAssignments = new ArrayList<>();

		Object roles = results.get(
			ScriptingKaleoTaskAssignmentSelector.ROLES_ASSIGNMENT);

		if (roles != null) {
			getRoleKaleoTaskAssignments(
				(List<Role>)roles, kaleoTaskAssignments);

			return kaleoTaskAssignments;
		}

		Object user = results.get(
			ScriptingKaleoTaskAssignmentSelector.USER_ASSIGNMENT);

		if (user != null) {
			kaleoTaskAssignments.add(_getUserKaleoTaskAssignment((User)user));

			return kaleoTaskAssignments;
		}

		Object users = results.get(
			ScriptingKaleoTaskAssignmentSelector.USERS_ASSIGNMENT);

		if (users != null) {
			_getUserKaleoTaskAssignments(
				(List<User>)users, kaleoTaskAssignments);
		}

		return kaleoTaskAssignments;
	}

	protected void getRoleKaleoTaskAssignments(
		List<Role> roles, List<KaleoTaskAssignment> kaleoTaskAssignments) {

		if (roles == null) {
			return;
		}

		for (Role role : roles) {
			KaleoTaskAssignment kaleoTaskAssignment =
				kaleoTaskAssignmentFactory.createKaleoTaskAssignment();

			kaleoTaskAssignment.setAssigneeClassName(Role.class.getName());
			kaleoTaskAssignment.setAssigneeClassPK(role.getRoleId());

			kaleoTaskAssignments.add(kaleoTaskAssignment);
		}
	}

	@Reference
	protected KaleoTaskAssignmentFactory kaleoTaskAssignmentFactory;

	private KaleoTaskAssignment _getUserKaleoTaskAssignment(User user) {
		KaleoTaskAssignment kaleoTaskAssignment =
			kaleoTaskAssignmentFactory.createKaleoTaskAssignment();

		kaleoTaskAssignment.setAssigneeClassName(User.class.getName());
		kaleoTaskAssignment.setAssigneeClassPK(user.getUserId());

		return kaleoTaskAssignment;
	}

	private void _getUserKaleoTaskAssignments(
		List<User> users, List<KaleoTaskAssignment> kaleoTaskAssignments) {

		for (User user : users) {
			kaleoTaskAssignments.add(_getUserKaleoTaskAssignment(user));
		}
	}

}