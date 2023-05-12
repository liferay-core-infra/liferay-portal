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

package com.liferay.portal.search.solr8.internal.connection;

import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.solr8.configuration.SolrConfiguration;
import com.liferay.portal.search.solr8.internal.http.HttpClientFactory;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.solr.client.solrj.SolrClient;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.search.solr8.configuration.SolrConfiguration",
	service = SolrClientManager.class
)
public class SolrClientManager {

	public SolrClient getSolrClient() {
		return _solrClient;
	}

	@Activate
	@Modified
	protected synchronized void activate(
			BundleContext bundleContext, Map<String, Object> properties)
		throws Exception {

		_httpClientFactories.put(
			"BASIC", _basicAuthPoolingHttpClientFactorySnapshot.get());
		_httpClientFactories.put(
			"CERT", _certAuthPoolingHttpClientFactorySnapshot.get());

		_httpClientFactoryServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, HttpClientFactory.class,
				"(&(!(type=BASIC))(!(type=CERT)))",
				new PropertyServiceReferenceMapper<String, HttpClientFactory>(
					"type"));

		_solrClientFactories.put(
			"CLOUD", _cloudSolrClientFactorySnapshot.get());
		_solrClientFactories.put(
			"REPLICATED", _replicatedSolrClientFactorySnapshot.get());

		_solrClientFactoryServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, SolrClientFactory.class,
				"(&(!(type=CLOUD))(!(type=REPLICATED)))",
				new PropertyServiceReferenceMapper<String, SolrClientFactory>(
					"type"));

		_close();

		_solrConfiguration = ConfigurableUtil.createConfigurable(
			SolrConfiguration.class, properties);

		String clientType = _solrConfiguration.clientType();

		SolrClientFactory solrClientFactory = _solrClientFactories.get(
			clientType);

		if (solrClientFactory == null) {
			solrClientFactory = _solrClientFactoryServiceTrackerMap.getService(
				clientType);
		}

		if (solrClientFactory == null) {
			throw new IllegalStateException(
				"Solr client factory not initialized: " + clientType);
		}

		String authMode = _solrConfiguration.authenticationMode();

		HttpClientFactory httpClientFactory = _httpClientFactories.get(
			authMode);

		if (httpClientFactory == null) {
			httpClientFactory = _httpClientFactoryServiceTrackerMap.getService(
				authMode);
		}

		if (httpClientFactory == null) {
			throw new IllegalStateException(
				"No HTTP client factory for " + authMode);
		}

		_solrClient = solrClientFactory.getSolrClient(
			_solrConfiguration, httpClientFactory);
	}

	@Deactivate
	protected synchronized void deactivate(Map<String, Object> properties) {
		_close();
		_httpClientFactories.remove("BASIC");
		_httpClientFactories.remove("CERT");
		_httpClientFactoryServiceTrackerMap.close();
		_solrClientFactories.remove("CLOUD");
		_solrClientFactories.remove("REPLICATED");
		_solrClientFactoryServiceTrackerMap.close();
	}

	private void _close() {
		if (_solrClient != null) {
			try {
				_solrClient.close();
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(ioException);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SolrClientManager.class);

	private static final Snapshot<HttpClientFactory>
		_basicAuthPoolingHttpClientFactorySnapshot = new Snapshot<>(
			SolrClientManager.class, HttpClientFactory.class, "(type=BASIC)",
			true);
	private static final Snapshot<HttpClientFactory>
		_certAuthPoolingHttpClientFactorySnapshot = new Snapshot<>(
			SolrClientManager.class, HttpClientFactory.class, "(type=CERT)",
			true);
	private static final Snapshot<SolrClientFactory>
		_cloudSolrClientFactorySnapshot = new Snapshot<>(
			SolrClientManager.class, SolrClientFactory.class, "(type=CLOUD)",
			true);
	private static final Snapshot<SolrClientFactory>
		_replicatedSolrClientFactorySnapshot = new Snapshot<>(
			SolrClientManager.class, SolrClientFactory.class,
			"(type=REPLICATED)", true);

	private final Map<String, HttpClientFactory> _httpClientFactories =
		new HashMap<>();
	private volatile ServiceTrackerMap<String, HttpClientFactory>
		_httpClientFactoryServiceTrackerMap;
	private volatile SolrClient _solrClient;
	private final Map<String, SolrClientFactory> _solrClientFactories =
		new ConcurrentHashMap<>();
	private volatile ServiceTrackerMap<String, SolrClientFactory>
		_solrClientFactoryServiceTrackerMap;
	private volatile SolrConfiguration _solrConfiguration;

}