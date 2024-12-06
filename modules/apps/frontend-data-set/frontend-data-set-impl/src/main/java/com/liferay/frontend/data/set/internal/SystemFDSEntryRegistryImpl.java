/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.SystemFDSEntryRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Daniel Sanz
 */
@Component(service = SystemFDSEntryRegistry.class)
public class SystemFDSEntryRegistryImpl implements SystemFDSEntryRegistry {

	@Override
	public Map<String, SystemFDSEntry> getSystemFDSEntries() {
		HashMap<String, SystemFDSEntry> systemFDSEntries = new HashMap<>();

		for (String key : _serviceTrackerMap.keySet()) {
			systemFDSEntries.put(key, _serviceTrackerMap.getService(key));
		}

		return Collections.unmodifiableMap(systemFDSEntries);
	}

	@Override
	public SystemFDSEntry getSystemFDSEntry(String fdsName) {
		return _serviceTrackerMap.getService(fdsName);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SystemFDSEntry.class, "frontend.data.set.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private BundleContext _bundleContext;
	private ServiceTrackerMap<String, SystemFDSEntry> _serviceTrackerMap;

}