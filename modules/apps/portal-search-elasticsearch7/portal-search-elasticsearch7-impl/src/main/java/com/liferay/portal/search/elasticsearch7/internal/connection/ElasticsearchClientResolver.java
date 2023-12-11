/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.connection;

import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationObserver;

import java.util.Collection;

import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author André de Oliveira
 */
public interface ElasticsearchClientResolver
	extends ElasticsearchConfigurationObserver {

	public void addElasticsearchConnection(
		ElasticsearchConnection elasticsearchConnection);

	public void applyConfigurations();

	public ElasticsearchConnection getElasticsearchConnection();

	public ElasticsearchConnection getElasticsearchConnection(
		boolean preferLocalCluster);

	public ElasticsearchConnection getElasticsearchConnection(
		String connectionId);

	public Collection<ElasticsearchConnection> getElasticsearchConnections();

	public String getLocalClusterConnectionId();

	public RestHighLevelClient getRestHighLevelClient();

	public RestHighLevelClient getRestHighLevelClient(String connectionId);

	public RestHighLevelClient getRestHighLevelClient(
		String connectionId, boolean preferLocalCluster);

	public boolean isCrossClusterReplicationEnabled();

	public void removeElasticsearchConnection(String connectionId);

}