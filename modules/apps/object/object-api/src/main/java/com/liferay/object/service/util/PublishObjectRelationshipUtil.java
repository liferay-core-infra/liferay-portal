/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Locale;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class PublishObjectRelationshipUtil {

	public static ObjectRelationship
			addObjectRelationshipAndDeployObjectDefinition(
				long userId, long objectDefinitionId1, long objectDefinitionId2,
				long parameterObjectFieldId, String deletionType,
				Map<Locale, String> labelMap, String name, String type)
		throws PortalException {

		ObjectRelationship objectRelationship =
			ObjectRelationshipLocalServiceUtil.addObjectRelationship(
				userId, objectDefinitionId1, objectDefinitionId2,
				parameterObjectFieldId, deletionType, labelMap, name, type);

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.getObjectDefinition(
				objectDefinitionId2);

		if (objectDefinition.isApproved()) {
			ObjectDefinitionLocalServiceUtil.deployObjectDefinition(
				objectDefinition);
		}

		return objectRelationship;
	}

}