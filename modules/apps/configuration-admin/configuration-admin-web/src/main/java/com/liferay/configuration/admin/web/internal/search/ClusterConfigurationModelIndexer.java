/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.search;

import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.concurrent.NoticeableFuture;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiServiceUtil;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Tina Tian
 */
@Component(
	service = {
		ClusterConfigurationModelIndexer.class, IdentifiableOSGiService.class
	}
)
public class ClusterConfigurationModelIndexer
	implements IdentifiableOSGiService {

	@Override
	public String getOSGiServiceIdentifier() {
		return ClusterConfigurationModelIndexer.class.getName();
	}

	public void initialize() throws PortletException {
		if (_initialized) {
			return;
		}

		synchronized (this) {
			if (_initialized) {
				return;
			}

			if (_clusterMasterExecutor.isMaster()) {
				_initialize();
			}
			else {
				NoticeableFuture<Void> noticeableFuture =
					_clusterMasterExecutor.executeOnMaster(
						new MethodHandler(
							_initializeMethodKey, getOSGiServiceIdentifier()));

				try {
					noticeableFuture.get();
				}
				catch (Exception exception) {
					throw new PortletException(
						"Unable to initialize configuration model index",
						exception);
				}
			}

			_initialized = true;
		}
	}

	@Activate
	protected void activate() {
		if (_clusterExecutor.isEnabled()) {
			_configurationModelsClusterMasterTokenTransitionListener =
				new ConfigurationModelsClusterMasterTokenTransitionListener();

			_clusterMasterExecutor.addClusterMasterTokenTransitionListener(
				_configurationModelsClusterMasterTokenTransitionListener);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (_configurationModelsClusterMasterTokenTransitionListener != null) {
			_clusterMasterExecutor.removeClusterMasterTokenTransitionListener(
				_configurationModelsClusterMasterTokenTransitionListener);
		}

		_stopBundleTracker();
	}

	private static void _initialize(String osgiServiceIdentifier)
		throws Exception {

		ClusterConfigurationModelIndexer clusterConfigurationModelIndexer =
			(ClusterConfigurationModelIndexer)
				IdentifiableOSGiServiceUtil.getIdentifiableOSGiService(
					osgiServiceIdentifier);

		clusterConfigurationModelIndexer.initialize();
	}

	private static void _reset(String osgiServiceIdentifier) {
		ClusterConfigurationModelIndexer clusterConfigurationModelIndexer =
			(ClusterConfigurationModelIndexer)
				IdentifiableOSGiServiceUtil.getIdentifiableOSGiService(
					osgiServiceIdentifier);

		clusterConfigurationModelIndexer._initialized = false;
	}

	private void _commit() {
		try {
			_indexWriterHelper.commit();
		}
		catch (SearchException searchException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to commit", searchException);
			}
		}
	}

	private void _initialize() {
		Map<String, Collection<ConfigurationModel>> configurationModelsMap =
			new ConcurrentHashMap<>();

		Bundle[] bundles = _bundleContext.getBundles();

		List<ConfigurationModel> configurationModelsList = new ArrayList<>();

		for (Bundle bundle : bundles) {
			if (bundle.getState() != Bundle.ACTIVE) {
				continue;
			}

			Map<String, ConfigurationModel> configurationModels =
				_configurationModelRetriever.getConfigurationModels(
					bundle, ExtendedObjectClassDefinition.Scope.SYSTEM, null);

			configurationModelsList.addAll(configurationModels.values());

			configurationModelsMap.put(
				bundle.getSymbolicName(), configurationModels.values());
		}

		try {
			_configurationModelIndexer.reindex(configurationModelsList);
		}
		catch (SearchException searchException) {
			throw new RuntimeException(searchException);
		}

		_commit();

		BundleTracker<Collection<ConfigurationModel>> bundleTracker =
			new BundleTracker<>(
				_bundleContext, Bundle.ACTIVE,
				new ConfigurationModelsBundleTrackerCustomizer(
					configurationModelsMap));

		bundleTracker.open();
	}

	private synchronized void _stopBundleTracker() {
		if (_bundleTracker != null) {
			_bundleTracker.close();

			_bundleTracker = null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClusterConfigurationModelIndexer.class);

	private static final MethodKey _initializeMethodKey = new MethodKey(
		ClusterConfigurationModelIndexer.class, "_initialize", String.class);
	private static final MethodKey _resetMethodKey = new MethodKey(
		ClusterConfigurationModelIndexer.class, "_reset", String.class);

	private BundleContext _bundleContext;
	private BundleTracker<Collection<ConfigurationModel>> _bundleTracker;

	@Reference
	private ClusterExecutor _clusterExecutor;

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference(
		target = "(component.name=com.liferay.configuration.admin.web.internal.search.ConfigurationModelIndexer)"
	)
	private Indexer<ConfigurationModel> _configurationModelIndexer;

	@Reference(target = "(!(filter.visibility=*))")
	private ConfigurationModelRetriever _configurationModelRetriever;

	private ConfigurationModelsClusterMasterTokenTransitionListener
		_configurationModelsClusterMasterTokenTransitionListener;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	private volatile boolean _initialized;

	private class ConfigurationModelsBundleTrackerCustomizer
		implements BundleTrackerCustomizer<Collection<ConfigurationModel>> {

		@Override
		public Collection<ConfigurationModel> addingBundle(
			Bundle bundle, BundleEvent bundleEvent) {

			Collection<ConfigurationModel> configurationModels =
				_configurationModelsMap.remove(bundle.getSymbolicName());

			if (configurationModels != null) {
				if (configurationModels.isEmpty()) {
					return null;
				}

				return configurationModels;
			}

			Map<String, ConfigurationModel> configurationModelsMap =
				_configurationModelRetriever.getConfigurationModels(
					bundle, ExtendedObjectClassDefinition.Scope.SYSTEM, null);

			if (configurationModelsMap.isEmpty()) {
				return null;
			}

			try {
				_configurationModelIndexer.reindex(
					configurationModelsMap.values());
			}
			catch (SearchException searchException) {
				throw new RuntimeException(searchException);
			}

			_commit();

			return configurationModelsMap.values();
		}

		@Override
		public void modifiedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			Collection<ConfigurationModel> configurationModels) {
		}

		@Override
		public void removedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			Collection<ConfigurationModel> configurationModels) {

			for (ConfigurationModel configurationModel : configurationModels) {
				try {
					_configurationModelIndexer.delete(configurationModel);
				}
				catch (SearchException searchException) {
					if (_log.isWarnEnabled()) {
						_log.warn("Unable to reindex models", searchException);
					}
				}
			}

			_commit();
		}

		private ConfigurationModelsBundleTrackerCustomizer(
			Map<String, Collection<ConfigurationModel>>
				configurationModelsMap) {

			_configurationModelsMap = configurationModelsMap;
		}

		private final Map<String, Collection<ConfigurationModel>>
			_configurationModelsMap;

	}

	private class ConfigurationModelsClusterMasterTokenTransitionListener
		implements ClusterMasterTokenTransitionListener {

		@Override
		public void masterTokenAcquired() {
			_initialized = false;

			ClusterRequest clusterRequest =
				ClusterRequest.createMulticastRequest(
					new MethodHandler(
						_resetMethodKey, getOSGiServiceIdentifier()),
					true);

			clusterRequest.setFireAndForget(true);

			_clusterExecutor.execute(clusterRequest);
		}

		@Override
		public void masterTokenReleased() {
			_stopBundleTracker();
		}

	}

}