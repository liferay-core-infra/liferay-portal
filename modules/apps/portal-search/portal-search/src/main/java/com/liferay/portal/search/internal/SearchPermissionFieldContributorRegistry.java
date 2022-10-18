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

package com.liferay.portal.search.internal;

import com.liferay.portal.search.spi.model.permission.SearchPermissionFieldContributor;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author jiaxu wei
 */
@Component(
	immediate = true, service = SearchPermissionFieldContributorRegistry.class
)
public class SearchPermissionFieldContributorRegistry {

	public List<SearchPermissionFieldContributor>
		getSearchPermissionFieldContributors() {

		return _searchPermissionFieldContributors;
	}

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private volatile List<SearchPermissionFieldContributor>
		_searchPermissionFieldContributors;

}