/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.cache.PortalCacheReplicator;
import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.cache.ehcache.internal.configurator.EhcachePortalCacheManagerConfigurator;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;

import java.io.Serializable;

import java.net.URL;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import net.sf.ehcache.config.Configuration;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(
	property = PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.MULTI_VM,
	service = PortalCacheManager.class
)
public class MultiVMEhcachePortalCacheManager
	<K extends Serializable, V extends Serializable>
		extends BaseEhcachePortalCacheManager<K, V> {

	@Activate
	protected void activate(BundleContext bundleContext) {
		this.bundleContext = bundleContext;

		clusterEnabled = GetterUtil.getBoolean(
			_props.get(PropsKeys.CLUSTER_LINK_ENABLED));
		_defaultReplicatorPropertiesString = _getPortalPropertiesString(
			PropsKeys.EHCACHE_REPLICATOR_PROPERTIES_DEFAULT);
		_replicatorProperties = _props.getProperties(
			PropsKeys.EHCACHE_REPLICATOR_PROPERTIES + StringPool.PERIOD, true);

		setConfigFile(props.get(PropsKeys.EHCACHE_MULTI_VM_CONFIG_LOCATION));
		setDefaultConfigFile(_DEFAULT_CONFIG_FILE_NAME);
		setPortalCacheManagerName(PortalCacheManagerNames.MULTI_VM);

		initialize();

		if (_log.isDebugEnabled()) {
			_log.debug("Activated " + PortalCacheManagerNames.MULTI_VM);
		}
	}

	@Deactivate
	protected void deactivate() {
		destroy();
	}

	@Override
	protected ObjectValuePair<Configuration, PortalCacheManagerConfiguration>
		getConfigurationObjectValuePair(
			URL configurationURL, ClassLoader classLoader) {

		ObjectValuePair<Configuration, PortalCacheManagerConfiguration>
			objectValuePair = super.getConfigurationObjectValuePair(
				configurationURL, classLoader);

		if (clusterEnabled) {
			Set<String> portalCacheNames = new HashSet<>(
				_replicatorProperties.stringPropertyNames());

			PortalCacheManagerConfiguration portalCacheManagerConfiguration =
				objectValuePair.getValue();

			portalCacheNames.addAll(
				portalCacheManagerConfiguration.getPortalCacheNames());

			for (String portalCacheName : portalCacheNames) {
				_populateCacheReplicator(
					portalCacheManagerConfiguration.getPortalCacheConfiguration(
						portalCacheName),
					GetterUtil.getString(
						_replicatorProperties.getProperty(portalCacheName),
						_defaultReplicatorPropertiesString));
			}

			_populateCacheReplicator(
				portalCacheManagerConfiguration.
					getDefaultPortalCacheConfiguration(),
				_defaultReplicatorPropertiesString);
		}

		return objectValuePair;
	}

	protected boolean clusterEnabled;

	private String _getPortalPropertiesString(String portalPropertyKey) {
		String[] array = _props.getArray(portalPropertyKey);

		if (array.length == 0) {
			return null;
		}

		if (array.length == 1) {
			return array[0];
		}

		StringBundler sb = new StringBundler(array.length * 2);

		for (String value : array) {
			sb.append(value);
			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private void _populateCacheReplicator(
		PortalCacheConfiguration portalCacheConfiguration,
		String replicatorPropertiesString) {

		Properties replicatorProperties =
			EhcachePortalCacheManagerConfigurator.parseProperties(
				replicatorPropertiesString, StringPool.COMMA);

		replicatorProperties.put(PortalCacheReplicator.REPLICATOR, true);

		Set<Properties> portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		portalCacheListenerPropertiesSet.add(replicatorProperties);
	}

	private static final String _DEFAULT_CONFIG_FILE_NAME =
		"/ehcache/liferay-multi-vm.xml";

	private static final Log _log = LogFactoryUtil.getLog(
		MultiVMEhcachePortalCacheManager.class);

	private String _defaultReplicatorPropertiesString;

	@Reference
	private Props _props;

	private Properties _replicatorProperties;

}