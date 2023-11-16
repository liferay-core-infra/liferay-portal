/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.item.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;
import com.liferay.content.dashboard.item.filter.ContentDashboardItemFilter;
import com.liferay.content.dashboard.item.filter.provider.ContentDashboardItemFilterProvider;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.Objects;

import javax.portlet.Portlet;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class ContentDashboardItemFilterProviderRegistryTest {

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			ContentDashboardItemFilterProviderRegistryTest.class);

		_bundleContext = bundle.getBundleContext();

		_serviceReference = _bundleContext.getServiceReferences(
			Portlet.class.getName(),
			"(component.name=" +
				"com.liferay.content.dashboard.web.internal.portlet." +
					"ContentDashboardAdminPortlet)");

		_contentDashboardAdminPortlet = _bundleContext.getService(
			_serviceReference[0]);
	}

	@After
	public void tearDown() {
		_bundleContext.ungetService(_serviceReference[0]);
	}

	@Test
	public void testGetContentDashboardItemFilterProviders() {
		_bundleContext.registerService(
			ContentDashboardItemFilterProvider.class,
			new ContentDashboardItemFilterProvider() {

				@Override
				public ContentDashboardItemFilter getContentDashboardItemFilter(
						HttpServletRequest httpServletRequest)
					throws ContentDashboardItemActionException {

					return null;
				}

				@Override
				public String getKey() {
					return "mockContentDashboardItemFilter";
				}

				@Override
				public ContentDashboardItemFilter.Type getType() {
					return ContentDashboardItemFilter.Type.ITEM_SELECTOR;
				}

				@Override
				public boolean isShow(HttpServletRequest httpServletRequest) {
					return false;
				}

			},
			new HashMapDictionary<>());

		boolean found = false;

		Iterable<ContentDashboardItemFilterProvider>
			contentDashboardItemFilterProviders =
				ReflectionTestUtil.getFieldValue(
					_contentDashboardAdminPortlet,
					"_contentDashboardItemFilterProviderServiceTrackerList");

		for (ContentDashboardItemFilterProvider
				contentDashboardItemFilterProvider :
					contentDashboardItemFilterProviders) {

			if (Objects.equals(
					contentDashboardItemFilterProvider.getKey(),
					"mockContentDashboardItemFilter")) {

				found = true;

				break;
			}
		}

		Assert.assertTrue(found);
	}

	private BundleContext _bundleContext;
	private Object _contentDashboardAdminPortlet;
	private ServiceReference<?>[] _serviceReference;

}