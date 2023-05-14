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
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.solr8.configuration.SolrConfiguration;
import com.liferay.portal.search.solr8.internal.http.HttpClientFactory;

import java.io.IOException;

import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

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

		_solrClientFactoryServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, SolrClientFactory.class,
				"(&(!(type=CLOUD))(!(type=REPLICATED)))",
				new PropertyServiceReferenceMapper<String, SolrClientFactory>(
					"type"));

		_httpClientFactoryServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, HttpClientFactory.class,
				"(&(!(type=BASIC))(!(type=CERT)))",
				new PropertyServiceReferenceMapper<String, HttpClientFactory>(
					"type"));
		_close();

		_solrConfiguration = ConfigurableUtil.createConfigurable(
			SolrConfiguration.class, properties);

		String clientType = _solrConfiguration.clientType();

		SolrClientFactory solrClientFactory =
			_solrClientFactoryServiceTrackerMap.getService(clientType);

		if ((solrClientFactory == null) && clientType.equals("CLOUD")) {
			solrClientFactory = _cloudSolrClientFactory;
		}

		if ((solrClientFactory == null) && clientType.equals("REPLICATED")) {
			solrClientFactory = _replicatedSolrClientFactory;
		}

		if (solrClientFactory == null) {
			throw new IllegalStateException(
				"Solr client factory not initialized: " + clientType);
		}

		String authMode = _solrConfiguration.authenticationMode();

		HttpClientFactory httpClientFactory =
			_httpClientFactoryServiceTrackerMap.getService(authMode);

		if ((httpClientFactory == null) && authMode.equals("BASIC")) {
			httpClientFactory = _basicAuthPoolingHttpClientFactory;
		}

		if ((httpClientFactory == null) && authMode.equals("CERT")) {
			httpClientFactory = _certAuthPoolingHttpClientFactory;
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
		_httpClientFactoryServiceTrackerMap.close();
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

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY, target = "(type=BASIC)"
	)
	private volatile HttpClientFactory _basicAuthPoolingHttpClientFactory;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY, target = "(type=CERT)"
	)
	private volatile HttpClientFactory _certAuthPoolingHttpClientFactory;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY, target = "(type=CLOUD)"
	)
	private volatile SolrClientFactory _cloudSolrClientFactory;

	private volatile ServiceTrackerMap<String, HttpClientFactory>
		_httpClientFactoryServiceTrackerMap;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(type=REPLICATED)"
	)
	private volatile SolrClientFactory _replicatedSolrClientFactory;

	private volatile SolrClient _solrClient;
	private volatile ServiceTrackerMap<String, SolrClientFactory>
		_solrClientFactoryServiceTrackerMap;
	private volatile SolrConfiguration _solrConfiguration;

}