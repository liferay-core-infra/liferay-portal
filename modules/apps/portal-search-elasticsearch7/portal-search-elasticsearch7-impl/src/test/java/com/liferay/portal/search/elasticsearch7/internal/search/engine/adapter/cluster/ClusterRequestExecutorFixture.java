/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.cluster;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.engine.adapter.cluster.ClusterRequestExecutor;

/**
 * @author Dylan Rebelak
 */
public class ClusterRequestExecutorFixture {

	public ClusterRequestExecutor getClusterRequestExecutor() {
		return _clusterRequestExecutor;
	}

	public void setUp() {
		ClusterHealthStatusTranslator clusterHealthStatusTranslator =
			new ClusterHealthStatusTranslatorImpl();

		_clusterRequestExecutor = new ElasticsearchClusterRequestExecutor();

		ReflectionTestUtil.setFieldValue(
			_clusterRequestExecutor, "_healthClusterRequestExecutor",
			_createHealthClusterRequestExecutor(
				clusterHealthStatusTranslator,
				_elasticsearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			_clusterRequestExecutor, "_stateClusterRequestExecutor",
			_createStateClusterRequestExecutor(
				_elasticsearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			_clusterRequestExecutor, "_statsClusterRequestExecutor",
			_createStatsClusterRequestExecutor(
				clusterHealthStatusTranslator,
				_elasticsearchConnectionManager));
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		_elasticsearchConnectionManager = elasticsearchConnectionManager;
	}

	private HealthClusterRequestExecutor _createHealthClusterRequestExecutor(
		ClusterHealthStatusTranslator clusterHealthStatusTranslator,
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		HealthClusterRequestExecutor healthClusterRequestExecutor =
			new HealthClusterRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			healthClusterRequestExecutor, "_clusterHealthStatusTranslator",
			clusterHealthStatusTranslator);
		ReflectionTestUtil.setFieldValue(
			healthClusterRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return healthClusterRequestExecutor;
	}

	private StateClusterRequestExecutor _createStateClusterRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		StateClusterRequestExecutor stateClusterRequestExecutor =
			new StateClusterRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			stateClusterRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return stateClusterRequestExecutor;
	}

	private StatsClusterRequestExecutor _createStatsClusterRequestExecutor(
		ClusterHealthStatusTranslator clusterHealthStatusTranslator,
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		StatsClusterRequestExecutor statsClusterRequestExecutor =
			new StatsClusterRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			statsClusterRequestExecutor, "_clusterHealthStatusTranslator",
			clusterHealthStatusTranslator);
		ReflectionTestUtil.setFieldValue(
			statsClusterRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			statsClusterRequestExecutor, "_jsonFactory", new JSONFactoryImpl());

		return statsClusterRequestExecutor;
	}

	private ClusterRequestExecutor _clusterRequestExecutor;
	private ElasticsearchConnectionManager _elasticsearchConnectionManager;

}