/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.internal.recommendation.search.index;

import com.liferay.commerce.machine.learning.internal.search.api.CommerceMLIndexer;
import com.liferay.commerce.machine.learning.internal.search.index.helper.CommerceMLSearchEngineUtil;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(service = CommerceMLIndexer.class)
public class UserCommerceMLRecommendationIndexer implements CommerceMLIndexer {

	@Override
	public void createIndex(long companyId) {
		CommerceMLSearchEngineUtil.createIndex(
			getIndexName(companyId), _INDEX_MAPPING_FILE_NAME,
			_searchCapabilities, _searchEngineAdapter);
	}

	@Override
	public void dropIndex(long companyId) {
		CommerceMLSearchEngineUtil.dropIndex(
			getIndexName(companyId), _searchCapabilities, _searchEngineAdapter);
	}

	@Override
	public String getIndexName(long companyId) {
		return String.format(
			_INDEX_NAME_PATTERN, _indexNameBuilder.getIndexName(companyId));
	}

	private static final String _INDEX_MAPPING_FILE_NAME =
		"user-commerce-ml-recommendation-mappings.json";

	private static final String _INDEX_NAME_PATTERN =
		"%s-user-commerce-ml-recommendation";

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}