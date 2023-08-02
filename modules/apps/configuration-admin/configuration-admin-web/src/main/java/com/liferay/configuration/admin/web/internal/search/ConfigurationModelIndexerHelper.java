/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.search;

import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Valmir Junior
 */
@Component(service = ConfigurationModelIndexerHelper.class)
public class ConfigurationModelIndexerHelper {

	public BundleTracker<Collection<ConfigurationModel>> initialize() {
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

		return bundleTracker;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
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

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationModelIndexerHelper.class);

	private BundleContext _bundleContext;

	@Reference(
		target = "(component.name=com.liferay.configuration.admin.web.internal.search.ConfigurationModelIndexer)"
	)
	private Indexer<ConfigurationModel> _configurationModelIndexer;

	@Reference(target = "(!(filter.visibility=*))")
	private ConfigurationModelRetriever _configurationModelRetriever;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

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
				_log.error(
					"Unable to index documents for " + configurationModelsMap,
					searchException);
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

}