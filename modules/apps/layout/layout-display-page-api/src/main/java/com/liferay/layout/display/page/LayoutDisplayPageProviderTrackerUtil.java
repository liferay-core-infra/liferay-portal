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

package com.liferay.layout.display.page;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Jiaxu Wei
 */
public class LayoutDisplayPageProviderTrackerUtil {

	public static LayoutDisplayPageProvider<?> getLayoutDisplayPageProvider(
		String className) {

		return getLayoutDisplayPageProviderTracker().
			getLayoutDisplayPageProviderByClassName(className);
	}

	public static LayoutDisplayPageProviderRegistry
		getLayoutDisplayPageProviderTracker() {

		return _serviceTracker.getService();
	}

	private static final Bundle _bundle = FrameworkUtil.getBundle(
		LayoutDisplayPageProviderRegistry.class);

	private static final ServiceTracker
		<LayoutDisplayPageProviderRegistry, LayoutDisplayPageProviderRegistry>
			_serviceTracker =
				new ServiceTracker
					<LayoutDisplayPageProviderRegistry,
					 LayoutDisplayPageProviderRegistry>(
						 _bundle.getBundleContext(),
						 LayoutDisplayPageProviderRegistry.class, null) {

					{
						open();
					}
				};

}