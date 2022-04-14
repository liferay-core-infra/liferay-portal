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

package com.liferay.portal.kernel.search;

import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;

/**
 * @author Michael C. Han
 */
public class BaseRelatedEntryIndexer implements RelatedEntryIndexer {

	@Override
	public void addRelatedClassNames(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		searchContext.setAttribute("relatedClassName", Boolean.TRUE);

		String[] relatedEntryClassNames = (String[])searchContext.getAttribute(
			"relatedEntryClassNames");

		if (ArrayUtil.isEmpty(relatedEntryClassNames)) {
			return;
		}

		BooleanFilter relatedBooleanFilters = new BooleanFilter();

		for (String relatedEntryClassName : relatedEntryClassNames) {
			Indexer<?> indexer = IndexerRegistryUtil.getIndexer(
				relatedEntryClassName);

			if (indexer == null) {
				continue;
			}

			BooleanFilter relatedBooleanFilter = new BooleanFilter();

			indexer.postProcessContextBooleanFilter(
				relatedBooleanFilter, searchContext);

			for (IndexerPostProcessor indexerPostProcessor :
					indexer.getIndexerPostProcessors()) {

				indexerPostProcessor.postProcessContextBooleanFilter(
					relatedBooleanFilter, searchContext);
			}

			indexer.postProcessContextBooleanFilter(
				relatedBooleanFilter, searchContext);

			relatedBooleanFilter.addRequiredTerm(
				Field.CLASS_NAME_ID,
				PortalUtil.getClassNameId(relatedEntryClassName));

			relatedBooleanFilters.add(
				relatedBooleanFilter, BooleanClauseOccur.SHOULD);
		}

		if (relatedBooleanFilters.hasClauses()) {
			contextBooleanFilter.add(
				relatedBooleanFilters, BooleanClauseOccur.MUST);
		}

		searchContext.setAttribute("relatedClassName", Boolean.FALSE);
	}

	@Override
	public void addRelatedEntryFields(Document document, Object object)
		throws Exception {
	}

	@Override
	public boolean isVisibleRelatedEntry(long classPK, int status)
		throws Exception {

		return true;
	}

	@Override
	public void updateFullQuery(SearchContext searchContext) {
	}

}