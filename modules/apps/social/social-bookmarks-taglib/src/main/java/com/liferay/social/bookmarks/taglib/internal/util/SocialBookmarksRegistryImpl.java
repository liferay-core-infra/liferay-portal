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

package com.liferay.social.bookmarks.taglib.internal.util;

import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.social.bookmarks.SocialBookmark;
import com.liferay.social.bookmarks.SocialBookmarksRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Alejandro Tardín
 */
@Component(immediate = true, service = SocialBookmarksRegistry.class)
public class SocialBookmarksRegistryImpl implements SocialBookmarksRegistry {

	@Override
	public SocialBookmark getSocialBookmark(String type) {
		SocialBookmark socialBookmark = _serviceTrackerMap.getService(type);

		if (socialBookmark == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					String.format("Social bookmark %s is not available", type));
			}
		}

		return socialBookmark;
	}

	@Override
	public List<SocialBookmark> getSocialBookmarks() {
		List<SocialBookmark> socialBookmarks = new ArrayList<>();

		for (String type : getSocialBookmarksTypes()) {
			socialBookmarks.add(getSocialBookmark(type));
		}

		return socialBookmarks;
	}

	@Override
	public List<String> getSocialBookmarksTypes() {
		Set<String> socialBookmarksTypes = new LinkedHashSet<>();

		_serviceReferences.forEach(
			serviceReference -> socialBookmarksTypes.add(
				(String)serviceReference.getProperty("social.bookmarks.type")));

		return new ArrayList<>(socialBookmarksTypes);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SocialBookmark.class, "social.bookmarks.type",
			new SocialBookmarkTypeServiceTrackerCustomizer(
				bundleContext, _serviceReferences));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SocialBookmarksRegistryImpl.class);

	private List<ServiceReference<SocialBookmark>> _serviceReferences =
		new CopyOnWriteArrayList<>();
	private ServiceTrackerMap<String, SocialBookmark> _serviceTrackerMap;

	private static class SocialBookmarkTypeServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<SocialBookmark, SocialBookmark> {

		public SocialBookmarkTypeServiceTrackerCustomizer(
			BundleContext bundleContext,
			List<ServiceReference<SocialBookmark>> serviceReferences) {

			_bundleContext = bundleContext;
			_serviceReferences = serviceReferences;
		}

		@Override
		public SocialBookmark addingService(
			ServiceReference<SocialBookmark> serviceReference) {

			SocialBookmark socialBookmark = _bundleContext.getService(
				serviceReference);

			_update(serviceReference, false);

			return socialBookmark;
		}

		@Override
		public void modifiedService(
			ServiceReference<SocialBookmark> serviceReference,
			SocialBookmark socialBookmark) {

			_serviceReferences.sort(_comparator);
		}

		@Override
		public void removedService(
			ServiceReference<SocialBookmark> serviceReference,
			SocialBookmark socialBookmark) {

			_update(serviceReference, true);
			_bundleContext.ungetService(serviceReference);
		}

		private void _update(
			ServiceReference<SocialBookmark> serviceReference, boolean remove) {

			synchronized (_serviceReferences) {
				int index = Collections.binarySearch(
					_serviceReferences, serviceReference, _comparator);

				if (remove) {
					if (index >= 0) {
						_serviceReferences.remove(index);
					}
				}
				else if (index < 0) {
					_serviceReferences.add(-index - 1, serviceReference);
				}
			}
		}

		private final BundleContext _bundleContext;
		private PropertyServiceReferenceComparator<SocialBookmark> _comparator =
			new PropertyServiceReferenceComparator<>(
				"social.bookmarks.priority");
		private final List<ServiceReference<SocialBookmark>> _serviceReferences;

	}

}