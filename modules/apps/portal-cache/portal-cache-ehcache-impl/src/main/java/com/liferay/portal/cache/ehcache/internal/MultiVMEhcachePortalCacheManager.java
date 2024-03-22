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

	@Override
	public String getPortalCacheManagerName() {
		return PortalCacheManagerNames.MULTI_VM;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		this.bundleContext = bundleContext;

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
	protected String getConfigFile() {
		return GetterUtil.get(
			props.get(PropsKeys.EHCACHE_MULTI_VM_CONFIG_LOCATION),
			_DEFAULT_CONFIG_FILE_NAME);
	}

	@Override
	protected ObjectValuePair<Configuration, PortalCacheManagerConfiguration>
		getConfigurationObjectValuePair() {

		ObjectValuePair<Configuration, PortalCacheManagerConfiguration>
			objectValuePair = super.getConfigurationObjectValuePair();

		if (GetterUtil.getBoolean(_props.get(PropsKeys.CLUSTER_LINK_ENABLED))) {
			Properties replicatorProperties = _props.getProperties(
				PropsKeys.EHCACHE_REPLICATOR_PROPERTIES + StringPool.PERIOD,
				true);

			Set<String> portalCacheNames = new HashSet<>(
				replicatorProperties.stringPropertyNames());

			PortalCacheManagerConfiguration portalCacheManagerConfiguration =
				objectValuePair.getValue();

			portalCacheNames.addAll(
				portalCacheManagerConfiguration.getPortalCacheNames());

			String defaultReplicatorPropertiesString =
				_getPortalPropertiesString(
					PropsKeys.EHCACHE_REPLICATOR_PROPERTIES_DEFAULT);

			for (String portalCacheName : portalCacheNames) {
				_populateCacheReplicator(
					portalCacheManagerConfiguration.getPortalCacheConfiguration(
						portalCacheName),
					GetterUtil.getString(
						replicatorProperties.getProperty(portalCacheName),
						defaultReplicatorPropertiesString));
			}

			_populateCacheReplicator(
				portalCacheManagerConfiguration.
					getDefaultPortalCacheConfiguration(),
				defaultReplicatorPropertiesString);
		}

		return objectValuePair;
	}

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

	@Reference
	private Props _props;

}