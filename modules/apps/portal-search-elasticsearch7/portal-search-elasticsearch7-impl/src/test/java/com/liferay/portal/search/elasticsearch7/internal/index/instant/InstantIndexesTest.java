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

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.index.IndexSynchronizer;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.CreateIndexRequestExecutor;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.CreateIndexRequestExecutorImpl;
import com.liferay.portal.search.elasticsearch7.spi.index.IndexRegistrar;
import com.liferay.portal.search.spi.index.IndexDefinition;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author André de Oliveira
 */
public class InstantIndexesTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_bundleContext = SystemBundleUtil.getBundleContext();

		_elasticsearchFixture = new ElasticsearchFixture(
			InstantIndexesTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Before
	public void setUp() throws Exception {
		_indexSynchronizer = _createIndexSynchronizer(_elasticsearchFixture);

		_serviceRegistrations.add(
			_bundleContext.registerService(
				IndexRegistrar.class, new InstancesAndProcessesIndexRegistrar(),
				null));
	}

	@After
	public void tearDown() throws Exception {
		if (_indexSynchronizer != null) {
			_deactivateIndexSynchronizer();

			_indexSynchronizer = null;
		}

		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	public void testAutomaticIndexCreation() throws Exception {
		_registerEventsIndexDefinition();
		_registerTasksIndexDefinition();

		_activateIndexSynchronizer();

		_assertIndexesExist(
			_INDEX_NAME_WORKFLOW_EVENTS,
			InstancesAndProcessesIndexRegistrar.INDEX_NAME_WORKFLOW_INSTANCES,
			InstancesAndProcessesIndexRegistrar.INDEX_NAME_WORKFLOW_PROCESSES,
			_INDEX_NAME_WORKFLOW_TASKS);
	}

	@Test
	public void testRuntimeIndexCreation() throws Exception {
		_registerTasksIndexDefinition();

		_activateIndexSynchronizer();

		_assertIndexesExist(
			InstancesAndProcessesIndexRegistrar.INDEX_NAME_WORKFLOW_INSTANCES,
			InstancesAndProcessesIndexRegistrar.INDEX_NAME_WORKFLOW_PROCESSES,
			_INDEX_NAME_WORKFLOW_TASKS);

		_registerEventsIndexDefinition();

		_assertIndexesExist(
			_INDEX_NAME_WORKFLOW_EVENTS,
			InstancesAndProcessesIndexRegistrar.INDEX_NAME_WORKFLOW_INSTANCES,
			InstancesAndProcessesIndexRegistrar.INDEX_NAME_WORKFLOW_PROCESSES,
			_INDEX_NAME_WORKFLOW_TASKS);
	}

	@Test
	public void testStartTwiceIndexCreation() throws Exception {
		_registerEventsIndexDefinition();
		_registerTasksIndexDefinition();

		_activateIndexSynchronizer();

		_assertIndexesExist(
			_INDEX_NAME_WORKFLOW_EVENTS,
			InstancesAndProcessesIndexRegistrar.INDEX_NAME_WORKFLOW_INSTANCES,
			InstancesAndProcessesIndexRegistrar.INDEX_NAME_WORKFLOW_PROCESSES,
			_INDEX_NAME_WORKFLOW_TASKS);

		_deactivateIndexSynchronizer();

		_activateIndexSynchronizer();

		_assertIndexesExist(
			_INDEX_NAME_WORKFLOW_EVENTS,
			InstancesAndProcessesIndexRegistrar.INDEX_NAME_WORKFLOW_INSTANCES,
			InstancesAndProcessesIndexRegistrar.INDEX_NAME_WORKFLOW_PROCESSES,
			_INDEX_NAME_WORKFLOW_TASKS);
	}

	private void _activateIndexSynchronizer() {
		ReflectionTestUtil.invoke(
			_indexSynchronizer, "activate",
			new Class<?>[] {BundleContext.class}, _bundleContext);
	}

	private void _assertIndexesExist(String... expectedIndices) {
		GetIndexRequest getIndexRequest = new GetIndexRequest();

		getIndexRequest.indices(expectedIndices);

		GetIndexResponse getIndexResponse = _getIndexResponse(getIndexRequest);

		String[] actualIndices = getIndexResponse.getIndices();

		Assert.assertEquals(
			Arrays.asList(expectedIndices), Arrays.asList(actualIndices));
	}

	private CreateIndexRequestExecutor _createCreateIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		CreateIndexRequestExecutor createIndexRequestExecutor =
			new CreateIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			createIndexRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return createIndexRequestExecutor;
	}

	private IndexSynchronizer _createIndexSynchronizer(
		ElasticsearchFixture elasticsearchFixture) {

		IndexSynchronizer indexSynchronizer = new IndexSynchronizer();

		ReflectionTestUtil.setFieldValue(
			indexSynchronizer, "_createIndexRequestExecutor",
			_createCreateIndexRequestExecutor(elasticsearchFixture));

		return indexSynchronizer;
	}

	private void _deactivateIndexSynchronizer() {
		ReflectionTestUtil.invoke(
			_indexSynchronizer, "deactivate", new Class<?>[0]);
	}

	private GetIndexResponse _getIndexResponse(
		GetIndexRequest getIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchFixture.getRestHighLevelClient();

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.get(getIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _registerEventsIndexDefinition() {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				IndexDefinition.class, new EventsIndexDefinition(), null));
	}

	private void _registerTasksIndexDefinition() {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				IndexDefinition.class, new TasksIndexDefinition(), null));
	}

	private static final String _INDEX_NAME_WORKFLOW_EVENTS = "workflow-events";

	private static final String _INDEX_NAME_WORKFLOW_TASKS = "workflow-tasks";

	private static BundleContext _bundleContext;
	private static ElasticsearchFixture _elasticsearchFixture;

	private IndexSynchronizer _indexSynchronizer;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}