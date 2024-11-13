/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.deployer;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.ServiceRegistration;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public interface ObjectDefinitionDeployer {

	public List<ServiceRegistration<?>> deploy(
		ObjectDefinition objectDefinition);

	public default Map<Long, List<ServiceRegistration<?>>>
		deployActiveObjectDefinitions(
			long companyId,
			ObjectDefinitionLocalService objectDefinitionLocalService) {

		Map<Long, List<ServiceRegistration<?>>> activeServiceRegistrationsMap =
			new ConcurrentHashMap<>();

		for (ObjectDefinition objectDefinition :
				objectDefinitionLocalService.getObjectDefinitions(
					companyId, true, WorkflowConstants.STATUS_APPROVED)) {

			activeServiceRegistrationsMap.put(
				objectDefinition.getObjectDefinitionId(),
				deploy(objectDefinition));
		}

		return activeServiceRegistrationsMap;
	}

	public default void undeploy(ObjectDefinition objectDefinition) {
	}

}