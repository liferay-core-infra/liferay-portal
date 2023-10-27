/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.persistence.PortletPersistence;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ConcurrentHashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.service.impl.PortletLocalServiceImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Daniel Englert
 */
public class PortletLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_portletId = "test.portletId";
		_companyId = 1;
		_portletDatabaseId = 1;
		_portletLocalServiceImpl = new PortletLocalServiceImpl();
		_portletPersistence = Mockito.mock(PortletPersistence.class);
		_counterLocalService = Mockito.mock(CounterLocalService.class);
		PortletLocalService portletLocalService = Mockito.mock(
			PortletLocalService.class);

		_portlet = new PortletImpl();

		_portlet.setCompanyId(_companyId);
		_portlet.setPortletId(_portletId);

		_portletsMap = ConcurrentHashMapBuilder.<String, Portlet>put(
			_portletId, _portlet
		).build();

		_portletLocalServiceImpl.setCounterLocalService(_counterLocalService);

		_portletLocalServiceImpl.setPortletPersistence(_portletPersistence);

		ReflectionTestUtil.setFieldValue(
			_portletLocalServiceImpl, "_portletsMap", _portletsMap);
		ReflectionTestUtil.setFieldValue(
			_portletLocalServiceImpl, "portletLocalService",
			portletLocalService);
	}

	@Test
	public void testLoadGetPortletsMap() {
		long portletInDatabaseId = 20;
		ArrayList<Portlet> listToGiveBack = new ArrayList<>();

		Portlet portletInDataBase = new PortletImpl();

		portletInDataBase.setId(portletInDatabaseId);
		portletInDataBase.setCompanyId(_companyId);
		portletInDataBase.setPortletId(_portletId);

		listToGiveBack.add(portletInDataBase);

		Mockito.when(
			_portletPersistence.findByCompanyId(_companyId)
		).thenReturn(
			listToGiveBack
		);

		_portletLocalServiceImpl.loadGetPortletsMap(_companyId);

		Assert.assertEquals(
			portletInDatabaseId,
			_portletsMap.get(
				_portletId
			).getId());
	}

	@Test
	public void testUpdatePortletNotInDatabase() {
		Mockito.mockStatic(
			PortalUtil.class
		).when(
			() -> PortalUtil.getJsSafePortletId(_portletId)
		).thenReturn(
			_portletId
		);
		Mockito.when(
			_portletPersistence.fetchByC_P(_companyId, _portletId)
		).thenReturn(
			null
		);
		Mockito.when(
			_counterLocalService.increment()
		).thenReturn(
			_portletDatabaseId
		);
		Mockito.when(
			_portletPersistence.create(_portletDatabaseId)
		).thenReturn(
			_portlet
		);
		Mockito.when(
			_portletPersistence.update(_portlet)
		).thenReturn(
			_portlet
		);

		_portletLocalServiceImpl.updatePortlet(
			_companyId, _portletId, "testRoles", true);

		Assert.assertEquals(
			_portletDatabaseId,
			_portletsMap.get(
				_portletId
			).getId());
	}

	private long _companyId;
	private CounterLocalService _counterLocalService;
	private Portlet _portlet;
	private long _portletDatabaseId;
	private String _portletId;
	private PortletLocalServiceImpl _portletLocalServiceImpl;
	private PortletPersistence _portletPersistence;
	private Map<String, Portlet> _portletsMap;

}