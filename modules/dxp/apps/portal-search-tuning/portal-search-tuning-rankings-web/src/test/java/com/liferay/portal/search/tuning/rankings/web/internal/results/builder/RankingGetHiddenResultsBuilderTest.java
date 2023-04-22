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

package com.liferay.portal.search.tuning.rankings.web.internal.results.builder;

import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.tuning.rankings.web.internal.helper.RankingResultHelper;
import com.liferay.portal.search.tuning.rankings.web.internal.index.Ranking;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Wade Cao
 */
public class RankingGetHiddenResultsBuilderTest
	extends BaseRankingResultsBuilderTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_rankingGetHiddenResultsBuilder = new RankingGetHiddenResultsBuilder(
			dlAppLocalService, fastDateFormatFactory, queries, rankingIndexName,
			rankingIndexReader, resourceActions, resourceRequest,
			resourceResponse, searchEngineAdapter);

		_rankingGetHiddenResultsBuilder.from(
			0
		).size(
			2
		);
	}

	@Test
	public void testBuild() throws Exception {
		setUpDLAppLocalService();
		setUpFastDateFormatFactory();
		setUpRankingResultHelper();
		setUpResourceRequest();

		Ranking ranking = Mockito.mock(Ranking.class);

		Mockito.doReturn(
			Arrays.asList("1", "2")
		).when(
			ranking
		).getHiddenDocumentIds();

		setUpRankingIndexReader(ranking);

		setUpSearchEngineAdapter(
			setUpGetDocumentResponseGetDocument(
				setUpDocumentWithGetString(), setUpGetDocumentResponse()));

		Snapshot<RankingResultHelper> rankingResultHelperSnapshot =
			Mockito.mock(Snapshot.class);

		Mockito.doReturn(
			rankingResultHelper
		).when(
			rankingResultHelperSnapshot
		).get();

		ReflectionTestUtil.getAndSetFieldValue(
			_rankingGetHiddenResultsBuilder, "_rankingResultHelperSnapshot",
			rankingResultHelperSnapshot);

		Assert.assertEquals(
			mapper.readTree(_getExpectedDocumentsString()),
			mapper.readTree(
				_rankingGetHiddenResultsBuilder.build(
				).toJSONString()));
	}

	@Test
	public void testBuildWithRankingNotPresent() {
		setUpRankingIndexReader(null);

		Assert.assertEquals(
			JSONUtil.put(
				"documents", JSONFactoryUtil.createJSONArray()
			).put(
				"total", 0
			).toString(),
			_rankingGetHiddenResultsBuilder.build(
			).toString());
	}

	private String _getExpectedDocumentsString() {
		return JSONUtil.put(
			"documents",
			JSONUtil.putAll(
				JSONUtil.put(
					"author", "theAuthor"
				).put(
					"clicks", "theClicks"
				).put(
					"date", "20021209000109"
				).put(
					"deleted", false
				).put(
					"description", "undefined"
				).put(
					"hidden", true
				).put(
					"icon", "document-image"
				).put(
					"id", "theUID"
				).put(
					"pinned", false
				).put(
					"title", "theTitle"
				).put(
					"viewURL", ""
				),
				JSONUtil.put(
					"author", "theAuthor"
				).put(
					"clicks", "theClicks"
				).put(
					"date", "20021209000109"
				).put(
					"deleted", false
				).put(
					"description", "undefined"
				).put(
					"hidden", true
				).put(
					"icon", "document-image"
				).put(
					"id", "theUID"
				).put(
					"pinned", false
				).put(
					"title", "theTitle"
				).put(
					"viewURL", ""
				))
		).put(
			"total", 2
		).toString();
	}

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private RankingGetHiddenResultsBuilder _rankingGetHiddenResultsBuilder;

}