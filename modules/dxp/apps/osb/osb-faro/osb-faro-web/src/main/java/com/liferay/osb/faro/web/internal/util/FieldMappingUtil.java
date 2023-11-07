/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.engine.client.model.FieldMappingMap;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.petra.function.transform.TransformUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Matthew Kong
 */
public class FieldMappingUtil {

	public static void addDefaultFieldMappings(
			ContactsEngineClient contactsEngineClient, FaroProject faroProject)
		throws Exception {

		contactsEngineClient.addFieldMappings(
			faroProject, null, FieldMappingConstants.CONTEXT_ORGANIZATION,
			FieldMappingConstants.OWNER_TYPE_ACCOUNT,
			getNewFieldMappingMaps(
				contactsEngineClient, faroProject,
				FieldMappingConstants.CONTEXT_ORGANIZATION,
				FieldMappingConstants.getSalesforceAccountFieldMappingMaps()));

		List<FieldMappingMap> fieldMappingMaps = new ArrayList<>();

		fieldMappingMaps.addAll(
			FieldMappingConstants.getDefaultFieldMappingMaps());
		fieldMappingMaps.addAll(
			FieldMappingConstants.getLiferayFieldMappingMaps());
		fieldMappingMaps.addAll(
			FieldMappingConstants.getSalesforceIndividualFieldMappingMaps());

		contactsEngineClient.addFieldMappings(
			faroProject, null, FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
			FieldMappingConstants.OWNER_TYPE_INDIVIDUAL,
			getNewFieldMappingMaps(
				contactsEngineClient, faroProject,
				FieldMappingConstants.CONTEXT_DEMOGRAPHICS, fieldMappingMaps));
	}

	public static List<FieldMappingMap> getNewFieldMappingMaps(
		ContactsEngineClient contactsEngineClient, FaroProject faroProject,
		String context, List<FieldMappingMap> fieldMappingMaps) {

		List<FieldMappingMap> newFieldMappingMaps = new ArrayList<>();

		Results<FieldMapping> results = contactsEngineClient.getFieldMappings(
			faroProject, context,
			TransformUtil.transform(fieldMappingMaps, FieldMappingMap::getName),
			1, 10000, null);

		Set<String> currentFieldNames = new HashSet<>();

		for (FieldMapping fieldMapping : results.getItems()) {
			currentFieldNames.add(fieldMapping.getFieldName());
		}

		Set<String> newFieldMappingNames = new HashSet<>();

		for (FieldMappingMap fieldMappingMap : fieldMappingMaps) {
			String name = fieldMappingMap.getName();

			if (currentFieldNames.contains(name) ||
				newFieldMappingNames.contains(name)) {

				continue;
			}

			newFieldMappingNames.add(name);

			newFieldMappingMaps.add(fieldMappingMap);
		}

		return newFieldMappingMaps;
	}

	public static void validateCreate(String name) {
		Matcher matcher = _pattern.matcher(name);

		if (!matcher.find()) {
			throw new FaroException("Invalid field mapping name: " + name);
		}
	}

	private static final Pattern _pattern = Pattern.compile(
		"^[A-Za-z_][\\w]{0,126}[A-Za-z0-9]$");

}