/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.bundle.blacklist.internal;

import com.liferay.osgi.util.BundleUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.bundle.blacklist.BundleBlacklistManager;
import com.liferay.portal.bundle.blacklist.internal.configuration.BundleBlacklistConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.lpkg.deployer.LPKGDeployer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Tambara
 */
@Component(
	configurationPid = "com.liferay.portal.bundle.blacklist.internal.configuration.BundleBlacklistConfiguration",
	service = BundleBlacklist.class
)
public class BundleBlacklist {

	@Activate
	protected void activate(
			BundleContext bundleContext, Map<String, String> properties)
		throws Exception {

		modified(bundleContext, properties);

		_serviceRegistration = bundleContext.registerService(
			BundleBlacklistManager.class, new BundleBlacklistManagerImpl(),
			null);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Modified
	protected void modified(
			BundleContext bundleContext, Map<String, String> properties)
		throws Exception {

		Bundle bundle = bundleContext.getBundle();

		_blacklistFile = bundle.getDataFile(_BLACKLIST_FILE_NAME);

		Bundle systemBundle = bundleContext.getBundle(0);

		BundleContext systemBundleContext = systemBundle.getBundleContext();

		if (_selfMonitorBundleListener == null) {
			_selfMonitorBundleListener = new SelfMonitorBundleListener(
				bundle, systemBundleContext, _lpkgDeployer,
				_uninstalledBundles);
		}

		systemBundleContext.addBundleListener(_selfMonitorBundleListener);

		_loadFromBlacklistFile();

		BundleBlacklistConfiguration bundleBlacklistConfiguration =
			ConfigurableUtil.createConfigurable(
				BundleBlacklistConfiguration.class, properties);

		_blacklistBundleSymbolicNames = new HashSet<>(
			Arrays.asList(
				bundleBlacklistConfiguration.blacklistBundleSymbolicNames()));

		_blacklistBundleSymbolicNames.remove(bundle.getSymbolicName());

		bundleContext.addBundleListener(_bundleListener);

		_scanBundles(bundleContext);

		Set<Map.Entry<String, UninstalledBundleData>> entrySet =
			_uninstalledBundles.entrySet();

		Iterator<Map.Entry<String, UninstalledBundleData>> iterator =
			entrySet.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, UninstalledBundleData> entry = iterator.next();

			String symbolicName = entry.getKey();

			if (!_blacklistBundleSymbolicNames.contains(symbolicName)) {
				if (_log.isInfoEnabled()) {
					_log.info("Reinstalling bundle " + symbolicName);
				}

				UninstalledBundleData uninstalledBundleData = entry.getValue();

				BundleUtil.installBundle(
					bundleContext, _lpkgDeployer,
					uninstalledBundleData.getLocation(),
					uninstalledBundleData.getStartLevel());

				iterator.remove();

				_removeFromBlacklistFile(symbolicName);
			}
		}
	}

	private void _addToBlacklistFile(
			String symbolicName, UninstalledBundleData uninstalledBundleData)
		throws IOException {

		Properties blacklistProperties = new Properties();

		if (_blacklistFile.exists()) {
			try (InputStream inputStream = new FileInputStream(
					_blacklistFile)) {

				blacklistProperties.load(inputStream);
			}
		}

		blacklistProperties.setProperty(
			symbolicName, uninstalledBundleData.toString());

		try (OutputStream outputStream = new FileOutputStream(_blacklistFile)) {
			blacklistProperties.store(outputStream, null);
		}
	}

	private void _loadFromBlacklistFile() throws Exception {
		if (!_blacklistFile.exists()) {
			return;
		}

		Properties blacklistProperties = new Properties();

		try (InputStream inputStream = new FileInputStream(_blacklistFile)) {
			blacklistProperties.load(inputStream);
		}

		Set<Map.Entry<Object, Object>> entries = blacklistProperties.entrySet();

		for (Map.Entry<Object, Object> entry : entries) {
			String value = (String)entry.getValue();

			Matcher matcher = _pattern.matcher(value);

			if (matcher.matches()) {
				_uninstalledBundles.put(
					(String)entry.getKey(),
					new UninstalledBundleData(
						matcher.group(1), Integer.valueOf(matcher.group(2))));
			}
		}
	}

	private boolean _processBundle(Bundle bundle) {
		String symbolicName = bundle.getSymbolicName();

		if (_blacklistBundleSymbolicNames.contains(symbolicName)) {
			if (_log.isInfoEnabled()) {
				_log.info("Stopping blacklisted bundle " + bundle);
			}

			BundleStartLevel bundleStartLevel = bundle.adapt(
				BundleStartLevel.class);

			UninstalledBundleData uninstalledBundleData =
				new UninstalledBundleData(
					bundle.getLocation(), bundleStartLevel.getStartLevel());

			_uninstalledBundles.put(symbolicName, uninstalledBundleData);

			try {
				bundle.uninstall();

				_addToBlacklistFile(symbolicName, uninstalledBundleData);
			}
			catch (Exception exception) {
				_log.error("Unable to uninstall " + bundle, exception);

				_uninstalledBundles.remove(symbolicName);
			}

			return true;
		}

		return false;
	}

	private void _removeFromBlacklistFile(String symbolicName)
		throws Exception {

		Properties blacklistProperties = new Properties();

		if (_blacklistFile.exists()) {
			try (InputStream inputStream = new FileInputStream(
					_blacklistFile)) {

				blacklistProperties.load(inputStream);
			}
		}

		blacklistProperties.remove(symbolicName);

		try (OutputStream outputStream = new FileOutputStream(_blacklistFile)) {
			blacklistProperties.store(outputStream, null);
		}
	}

	private void _scanBundles(BundleContext bundleContext) {
		List<Bundle> uninstalledBundles = new ArrayList<>();

		for (Bundle bundle : bundleContext.getBundles()) {
			if ((bundle.getState() != Bundle.UNINSTALLED) &&
				_processBundle(bundle)) {

				uninstalledBundles.add(bundle);
			}
		}

		if (!uninstalledBundles.isEmpty()) {
			BundleUtil.refreshBundles(bundleContext, uninstalledBundles);
		}
	}

	private static final String _BLACKLIST_FILE_NAME = "blacklist.properties";

	private static final Log _log = LogFactoryUtil.getLog(
		BundleBlacklist.class);

	private static final Pattern _pattern = Pattern.compile(
		"\\{location=([^,]+), startLevel=(\\d+)\\}");

	private volatile Set<String> _blacklistBundleSymbolicNames;
	private volatile File _blacklistFile;

	private final BundleListener _bundleListener =
		new SynchronousBundleListener() {

			@Override
			public void bundleChanged(BundleEvent bundleEvent) {
				if (bundleEvent.getType() == BundleEvent.INSTALLED) {
					_processBundle(bundleEvent.getBundle());
				}
			}

		};

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private LPKGDeployer _lpkgDeployer;

	private volatile BundleListener _selfMonitorBundleListener;
	private ServiceRegistration<BundleBlacklistManager> _serviceRegistration;
	private final Map<String, UninstalledBundleData> _uninstalledBundles =
		new ConcurrentHashMap<>();

	private class BundleBlacklistManagerImpl implements BundleBlacklistManager {

		@Override
		public void addToBlacklistAndUninstall(String... bundleSymbolicNames)
			throws IOException {

			_updateProperties(
				blacklistBundleSymbolicNames -> {
					if (blacklistBundleSymbolicNames == null) {
						return bundleSymbolicNames;
					}

					Set<String> blacklistBundleSymbolicNamesSet =
						SetUtil.fromArray(blacklistBundleSymbolicNames);

					Collections.addAll(
						blacklistBundleSymbolicNamesSet, bundleSymbolicNames);

					return blacklistBundleSymbolicNamesSet.toArray(
						new String[0]);
				});
		}

		@Override
		public Collection<String> getBlacklistBundleSymbolicNames() {
			return new ArrayList<>(_uninstalledBundles.keySet());
		}

		@Override
		public void removeFromBlacklistAndInstall(String... bundleSymbolicNames)
			throws IOException {

			_updateProperties(
				blacklistBundleSymbolicNames -> {
					if (blacklistBundleSymbolicNames == null) {
						return null;
					}

					Set<String> blacklistBundleSymbolicNamesSet =
						SetUtil.fromArray(blacklistBundleSymbolicNames);

					for (String bundleSymbolicName : bundleSymbolicNames) {
						blacklistBundleSymbolicNamesSet.remove(
							bundleSymbolicName);
					}

					return blacklistBundleSymbolicNamesSet.toArray(
						new String[0]);
				});
		}

		private void _updateConfiguration(
				Configuration configuration,
				Dictionary<String, Object> properties)
			throws IOException {

			Bundle bundle = FrameworkUtil.getBundle(
				BundleBlacklistManager.class);

			BundleContext bundleContext = bundle.getBundleContext();

			CountDownLatch countDownLatch = new CountDownLatch(1);

			ServiceListener serviceListener = new ServiceListener() {

				@Override
				public void serviceChanged(ServiceEvent serviceEvent) {
					if (serviceEvent.getType() != ServiceEvent.MODIFIED) {
						return;
					}

					ServiceReference<?> serviceReference =
						serviceEvent.getServiceReference();

					Object service = bundleContext.getService(serviceReference);

					Class<?> serviceClass = service.getClass();

					if (BundleBlacklist.class.getName() ==
							serviceClass.getName()) {

						countDownLatch.countDown();
					}

					bundleContext.ungetService(serviceReference);
				}

			};

			bundleContext.addServiceListener(serviceListener);

			try {
				configuration.update(properties);

				countDownLatch.await();
			}
			catch (InterruptedException interruptedException) {
				if (_log.isDebugEnabled()) {
					_log.debug(interruptedException);
				}
			}
			finally {
				bundleContext.removeServiceListener(serviceListener);
			}
		}

		private void _updateProperties(
				Function<String[], String[]> updateFunction)
			throws IOException {

			Configuration configuration = _configurationAdmin.getConfiguration(
				BundleBlacklistConfiguration.class.getName(),
				StringPool.QUESTION);

			Dictionary<String, Object> properties =
				configuration.getProperties();

			String[] blacklistBundleSymbolicNames = null;

			if (properties == null) {
				properties = new HashMapDictionary<>();
			}
			else {

				// LPS-114840

				Object value = properties.get("blacklistBundleSymbolicNames");

				if (value instanceof String) {
					blacklistBundleSymbolicNames = StringUtil.split(
						(String)value);
				}
				else {
					blacklistBundleSymbolicNames = (String[])properties.get(
						"blacklistBundleSymbolicNames");
				}
			}

			blacklistBundleSymbolicNames = updateFunction.apply(
				blacklistBundleSymbolicNames);

			if (blacklistBundleSymbolicNames == null) {
				return;
			}

			if (blacklistBundleSymbolicNames.length == 0) {
				properties.remove("blacklistBundleSymbolicNames");
			}
			else {
				properties.put(
					"blacklistBundleSymbolicNames",
					blacklistBundleSymbolicNames);
			}

			_updateConfiguration(configuration, properties);
		}

	}

}