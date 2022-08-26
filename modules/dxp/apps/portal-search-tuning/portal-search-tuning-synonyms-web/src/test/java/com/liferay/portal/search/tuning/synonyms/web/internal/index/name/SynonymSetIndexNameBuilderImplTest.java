/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.index.name;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class SynonymSetIndexNameBuilderImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testMultiTenancy() {
		SynonymSetIndexNameBuilderImpl synonymSetIndexNameBuilderImpl =
			new SynonymSetIndexNameBuilderImpl();

		ReflectionTestUtil.setFieldValue(
			synonymSetIndexNameBuilderImpl, "_indexNameBuilder",
			new IndexNameBuilder() {

				@Override
				public String getIndexName(long companyId) {
					return "liferay-" + companyId;
				}

			});

		Assert.assertEquals(
			"liferay-2021-search-tuning-synonyms",
			synonymSetIndexNameBuilderImpl.getSynonymSetIndexName(
				2021
			).getIndexName());
	}

}