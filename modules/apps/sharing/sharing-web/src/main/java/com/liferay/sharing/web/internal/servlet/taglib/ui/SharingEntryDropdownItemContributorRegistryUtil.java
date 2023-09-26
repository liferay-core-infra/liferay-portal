/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.servlet.taglib.ui;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.servlet.taglib.ui.SharingEntryDropdownItemContributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Adolfo Pérez
 */
public class SharingEntryDropdownItemContributorRegistryUtil {

	public static SharingEntryDropdownItemContributor
		getSharingEntryMenuItemContributor(String className) {

		return new CompositeSharingEntryDropdownItemContributor(
			_serviceTrackerMap.getService(className));
	}

	private static final ServiceTrackerMap
		<String, List<SharingEntryDropdownItemContributor>> _serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			SharingEntryDropdownItemContributorRegistryUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, SharingEntryDropdownItemContributor.class,
			"model.class.name");
	}

	private static final class CompositeSharingEntryDropdownItemContributor
		implements SharingEntryDropdownItemContributor {

		public CompositeSharingEntryDropdownItemContributor(
			List<SharingEntryDropdownItemContributor>
				sharingEntryDropdownItemContributors) {

			_sharingEntryDropdownItemContributors =
				sharingEntryDropdownItemContributors;
		}

		@Override
		public List<DropdownItem> getSharingEntryDropdownItems(
			SharingEntry sharingEntry, ThemeDisplay themeDisplay) {

			if (ListUtil.isEmpty(_sharingEntryDropdownItemContributors)) {
				return Collections.emptyList();
			}

			List<DropdownItem> dropdownItems = new ArrayList<>();

			for (SharingEntryDropdownItemContributor
					sharingEntryDropdownItemContributor :
						_sharingEntryDropdownItemContributors) {

				dropdownItems.addAll(
					sharingEntryDropdownItemContributor.
						getSharingEntryDropdownItems(
							sharingEntry, themeDisplay));
			}

			return dropdownItems;
		}

		private final List<SharingEntryDropdownItemContributor>
			_sharingEntryDropdownItemContributors;

	}

}