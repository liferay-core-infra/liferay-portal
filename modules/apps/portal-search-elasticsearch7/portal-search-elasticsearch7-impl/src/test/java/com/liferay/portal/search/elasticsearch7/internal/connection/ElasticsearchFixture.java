/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.connection;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.constants.ConnectionConstants;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.ElasticsearchInstancePaths;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.PathUtil;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.Sidecar;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collections;
import java.util.Map;

import org.elasticsearch.action.ingest.PutPipelineRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.IngestClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.xcontent.XContentType;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class ElasticsearchFixture implements ElasticsearchClientResolver {

	public ElasticsearchFixture() {
		_elasticsearchConnectionFixture =
			_elasticsearchConnectionFixtureSingleton.
				getElasticsearchConnectionFixture();

		_singleton = true;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public ElasticsearchFixture(Class<?> clazz) {
		this();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public ElasticsearchFixture(String subdirName) {
		this();
	}

	public ElasticsearchFixture(
		String clusterName,
		Map<String, Object> elasticsearchConfigurationProperties) {

		_elasticsearchConnectionFixture = new ElasticsearchConnectionFixture(
			clusterName, elasticsearchConfigurationProperties);

		_singleton = false;
	}

	public ElasticsearchConnection createElasticsearchConnection() {
		_elasticsearchConnectionFixtureSingleton.stop();

		return _elasticsearchConnectionFixture.createElasticsearchConnection();
	}

	public Map<String, Object> getElasticsearchConfigurationProperties() {
		return _elasticsearchConnectionFixture.
			getElasticsearchConfigurationProperties();
	}

	public ElasticsearchConnection getElasticsearchConnection() {
		return _elasticsearchConnectionFixture.getElasticsearchConnection();
	}

	public GetIndexResponse getIndex(String... indices) {
		RestHighLevelClient restHighLevelClient = getRestHighLevelClient();

		IndicesClient indicesClient = restHighLevelClient.indices();

		GetIndexRequest getIndexRequest = new GetIndexRequest(indices);

		try {
			return indicesClient.get(getIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient() {
		return _elasticsearchConnectionFixture.getRestHighLevelClient();
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient(String connectionId) {
		return getRestHighLevelClient();
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient(
		String connectionId, boolean preferLocalCluster) {

		return getRestHighLevelClient();
	}

	public void setUp() throws Exception {
		if (_singleton) {
			_elasticsearchConnectionFixtureSingleton.start();

			return;
		}

		_elasticsearchConnectionFixtureSingleton.stop();

		_elasticsearchConnectionFixture.createNode();
	}

	public void tearDown() throws Exception {
		if (!_singleton) {
			_elasticsearchConnectionFixture.destroyNode();
		}
	}

	public void waitForElasticsearchToStart() {
		ClusterHealthResponseUtil.getClusterHealthResponse(
			this,
			new HealthExpectations() {
				{
					setActivePrimaryShards(0);
					setActiveShards(0);
					setNumberOfDataNodes(1);
					setNumberOfNodes(1);
					setStatus(ClusterHealthStatus.GREEN);
					setUnassignedShards(0);
				}
			});
	}

	private static final ElasticsearchConnectionFixtureSingleton
		_elasticsearchConnectionFixtureSingleton =
			new ElasticsearchConnectionFixtureSingleton();

	private final ElasticsearchConnectionFixture
		_elasticsearchConnectionFixture;
	private final boolean _singleton;

	private static class ElasticsearchConnectionFixture
		implements ElasticsearchClientResolver {

		public ElasticsearchConnection createElasticsearchConnection() {
			PropsUtil.set(PropsKeys.LIFERAY_HOME, _TMP_PATH.toString());
			PropsUtil.set(
				PropsKeys.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR,
				String.valueOf(_TMP_PATH.resolve("lib-process-executor")));

			ElasticsearchConfigurationWrapper
				elasticsearchConfigurationWrapper =
					new ElasticsearchConfigurationWrapper() {
						{
							setElasticsearchConfiguration(
								ConfigurableUtil.createConfigurable(
									ElasticsearchConfiguration.class,
									_elasticsearchConfigurationProperties));
						}

						@Override
						public String httpCORSAllowOrigin() {
							return "'*'";
						}

					};

			Sidecar sidecar = new Sidecar(
				elasticsearchConfigurationWrapper,
				_createElasticsearchInstancePaths());

			ElasticsearchConnectionBuilder elasticsearchConnectionBuilder =
				new ElasticsearchConnectionBuilder();

			elasticsearchConnectionBuilder.active(
				true
			).connectionId(
				ConnectionConstants.SIDECAR_CONNECTION_ID
			).postCloseRunnable(
				sidecar::stop
			).preConnectElasticsearchConnectionConsumer(
				elasticsearchConnection -> {
					_deleteTmpDir();

					sidecar.start();

					elasticsearchConnection.setNetworkHostAddresses(
						new String[] {sidecar.getNetworkHostAddress()});
				}
			);

			_elasticsearchConnection = elasticsearchConnectionBuilder.build();

			return _elasticsearchConnection;
		}

		public void createNode() {
			createElasticsearchConnection();

			_elasticsearchConnection.connect();

			_putTimestampPipeline(getRestHighLevelClient());
		}

		public void destroyNode() {
			if (_elasticsearchConnection != null) {
				_elasticsearchConnection.close();
			}

			_deleteTmpDir();
		}

		public Map<String, Object> getElasticsearchConfigurationProperties() {
			return _elasticsearchConfigurationProperties;
		}

		public ElasticsearchConnection getElasticsearchConnection() {
			return _elasticsearchConnection;
		}

		@Override
		public RestHighLevelClient getRestHighLevelClient() {
			return _elasticsearchConnection.getRestHighLevelClient();
		}

		@Override
		public RestHighLevelClient getRestHighLevelClient(String connectionId) {
			return getRestHighLevelClient();
		}

		@Override
		public RestHighLevelClient getRestHighLevelClient(
			String connectionId, boolean preferLocalCluster) {

			return getRestHighLevelClient();
		}

		private ElasticsearchConnectionFixture(
			String clusterName,
			Map<String, Object> elasticsearchConfigurationProperties) {

			String sidecarJVMOptions = "-Xmx256m";

			if (!JavaDetector.isJDK8()) {
				sidecarJVMOptions =
					"-Xmx256m|--add-opens=java.base/java.lang=ALL-UNNAMED|--" +
						"add-opens=java.base/java.lang.invoke=ALL-UNNAMED";
			}

			_elasticsearchConfigurationProperties =
				HashMapBuilder.<String, Object>put(
					"clusterName", clusterName
				).put(
					"configurationPid",
					ElasticsearchConfiguration.class.getName()
				).put(
					"httpCORSAllowOrigin", "*"
				).put(
					"logExceptionsOnly", false
				).put(
					"sidecarJVMOptions", sidecarJVMOptions
				).putAll(
					elasticsearchConfigurationProperties
				).build();

			_workPath = _TMP_PATH.resolve(clusterName);
		}

		private ElasticsearchInstancePaths _createElasticsearchInstancePaths() {
			ElasticsearchInstancePaths elasticsearchInstancePaths =
				Mockito.mock(ElasticsearchInstancePaths.class);

			Mockito.doReturn(
				_TMP_PATH.resolve("sidecar-elasticsearch")
			).when(
				elasticsearchInstancePaths
			).getHomePath();

			Mockito.doReturn(
				_workPath
			).when(
				elasticsearchInstancePaths
			).getWorkPath();

			return elasticsearchInstancePaths;
		}

		private void _deleteTmpDir() {
			PathUtil.deleteDir(_workPath);
		}

		private void _putTimestampPipeline(
			RestHighLevelClient restHighLevelClient) {

			IngestClient ingestClient = restHighLevelClient.ingest();

			String json = JSONUtil.put(
				"description", "Adds timestamp to documents"
			).put(
				"processors",
				JSONUtil.put(
					JSONUtil.put(
						"set",
						JSONUtil.put(
							"field", "_source.timestamp"
						).put(
							"value", "{{{_ingest.timestamp}}}"
						)))
			).toString();

			PutPipelineRequest putPipelineRequest = new PutPipelineRequest(
				"timestamp",
				new BytesArray(json.getBytes(StandardCharsets.UTF_8)),
				XContentType.JSON);

			try {
				ingestClient.putPipeline(
					putPipelineRequest, RequestOptions.DEFAULT);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		private static final Path _TMP_PATH = Paths.get("tmp");

		private final Map<String, Object> _elasticsearchConfigurationProperties;
		private ElasticsearchConnection _elasticsearchConnection;
		private final Path _workPath;

	}

	private static class ElasticsearchConnectionFixtureSingleton {

		public void start() {
			if (!_connected) {
				_elasticsearchConnectionFixture.createNode();

				_connected = true;
			}
		}

		public void stop() {
			if (_connected) {
				_elasticsearchConnectionFixture.destroyNode();

				_connected = false;
			}
		}

		protected ElasticsearchConnectionFixture
			getElasticsearchConnectionFixture() {

			return _elasticsearchConnectionFixture;
		}

		private ElasticsearchConnectionFixtureSingleton() {
			_elasticsearchConnectionFixture =
				new ElasticsearchConnectionFixture(
					ElasticsearchFixture.class.getSimpleName(),
					Collections.emptyMap());
		}

		private boolean _connected;
		private final ElasticsearchConnectionFixture
			_elasticsearchConnectionFixture;

	}

}