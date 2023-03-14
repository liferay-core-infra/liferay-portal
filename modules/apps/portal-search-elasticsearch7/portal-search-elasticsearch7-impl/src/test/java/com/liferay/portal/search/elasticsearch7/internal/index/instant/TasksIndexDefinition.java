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

package com.liferay.portal.search.elasticsearch7.internal.index.instant;

import com.liferay.portal.search.spi.index.IndexDefinition;

import org.osgi.service.component.annotations.Component;

/**
 * @author André de Oliveira
 */
@Component(service = IndexDefinition.class)
public class TasksIndexDefinition implements IndexDefinition {

	@Override
	public String getIndexName() {
		return "workflow-tasks";
	}

	@Override
	public String getIndexSettingsResourceName() {
		return "workflow-tasks-type-mappings.json";
	}

}