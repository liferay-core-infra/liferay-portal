/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util.background.task;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.SearchEngineHelperUtil;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSenderUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.index.SyncReindexManager;
import com.liferay.portal.search.internal.SearchEngineHelperImpl;
import com.liferay.portal.search.internal.background.task.ReindexSingleIndexerBackgroundTaskExecutor;
import com.liferay.portal.search.test.util.search.engine.SearchEngineFixture;
import com.liferay.portal.util.PropsImpl;

import java.io.Serializable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Adam Brandizzi
 */
public abstract class BaseReindexSingleIndexerBackgroundTaskExecutorTestCase {

	@BeforeClass
	public static void setUpClass() {
		PropsUtil.setProps(new PropsImpl());
	}

	@AfterClass
	public static void tearDownClass() {
		_indexerRegistryUtilMockedStatic.close();
		_indexWriterHelperUtilMockedStatic.close();
		_reindexStatusMessageSenderUtilMockedStatic.close();
		_searchEngineHelperUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		setUpBackgroundTask(companyId);

		setUpIndexerRegistry();

		SearchEngineFixture searchEngineFixture = getSearchEngineFixture();

		searchEngineFixture.setUp();

		SearchEngineHelper searchEngineHelper = new SearchEngineHelperImpl();

		ReflectionTestUtil.setFieldValue(
			searchEngineHelper, "_searchEngine",
			searchEngineFixture.getSearchEngine());

		_companyId = companyId;
		_searchEngineFixture = searchEngineFixture;
		_searchEngineHelper = searchEngineHelper;
	}

	@After
	public void tearDown() throws Exception {
		if (_searchEngineFixture != null) {
			_searchEngineFixture.tearDown();
		}
	}

	@Test
	public void testFieldMappings() throws Exception {
		ReindexSingleIndexerBackgroundTaskExecutor
			reindexSingleIndexerBackgroundTaskExecutor =
				getReindexSingleIndexerBackgroundTaskExecutor();

		reindexSingleIndexerBackgroundTaskExecutor.execute(_backgroundTask);

		assertFieldType(Field.ENTRY_CLASS_NAME, "keyword");
	}

	protected abstract void assertFieldType(String fieldName, String fieldType)
		throws Exception;

	protected String getIndexName() {
		IndexNameBuilder indexNameBuilder =
			_searchEngineFixture.getIndexNameBuilder();

		return indexNameBuilder.getIndexName(_companyId);
	}

	protected ReindexSingleIndexerBackgroundTaskExecutor
		getReindexSingleIndexerBackgroundTaskExecutor() {

		_indexerRegistryUtilMockedStatic.when(
			() -> IndexerRegistryUtil.getIndexer(Mockito.anyString())
		).thenAnswer(
			invocation -> _indexerRegistry.getIndexer(Mockito.anyString())
		);

		_searchEngineHelperUtilMockedStatic.when(
			SearchEngineHelperUtil::getSearchEngineHelper
		).thenReturn(
			_searchEngineHelper
		);

		Mockito.when(
			_syncReindexManagerSnapshot.get()
		).thenReturn(
			_syncReindexManager
		);

		return new ReindexSingleIndexerBackgroundTaskExecutor(
			_syncReindexManagerSnapshot, _systemIndexers);
	}

	protected abstract SearchEngineFixture getSearchEngineFixture();

	protected void setUpBackgroundTask(long companyId) {
		Mockito.when(
			_backgroundTask.getTaskContextMap()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				ReindexBackgroundTaskConstants.CLASS_NAME,
				RandomTestUtil.randomString()
			).put(
				ReindexBackgroundTaskConstants.COMPANY_IDS,
				new long[] {companyId}
			).build()
		);
	}

	protected void setUpIndexerRegistry() {
		Mockito.when(
			_indexerRegistry.getIndexer(Mockito.anyString())
		).thenReturn(
			_indexer
		);
	}

	private static final MockedStatic<IndexerRegistryUtil>
		_indexerRegistryUtilMockedStatic = Mockito.mockStatic(
			IndexerRegistryUtil.class);
	private static final MockedStatic<IndexWriterHelperUtil>
		_indexWriterHelperUtilMockedStatic = Mockito.mockStatic(
			IndexWriterHelperUtil.class);
	private static final MockedStatic<ReindexStatusMessageSenderUtil>
		_reindexStatusMessageSenderUtilMockedStatic = Mockito.mockStatic(
			ReindexStatusMessageSenderUtil.class);
	private static final MockedStatic<SearchEngineHelperUtil>
		_searchEngineHelperUtilMockedStatic = Mockito.mockStatic(
			SearchEngineHelperUtil.class);

	private final BackgroundTask _backgroundTask = Mockito.mock(
		BackgroundTask.class);
	private long _companyId;
	private final Indexer<Object> _indexer = Mockito.mock(Indexer.class);
	private final IndexerRegistry _indexerRegistry = Mockito.mock(
		IndexerRegistry.class);
	private SearchEngineFixture _searchEngineFixture;
	private SearchEngineHelper _searchEngineHelper;
	private final SyncReindexManager _syncReindexManager = Mockito.mock(
		SyncReindexManager.class);
	private final Snapshot<SyncReindexManager> _syncReindexManagerSnapshot =
		Mockito.mock(Snapshot.class);
	private final ServiceTrackerList<Indexer<?>> _systemIndexers = Mockito.mock(
		ServiceTrackerList.class);

}