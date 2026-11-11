/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.impl;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.persistence.ExpandoColumnPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoTablePersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoValuePersistence;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.LocalizationImpl;
import com.liferay.portlet.expando.model.impl.ExpandoColumnImpl;
import com.liferay.portlet.expando.model.impl.ExpandoTableImpl;
import com.liferay.portlet.expando.model.impl.ExpandoValueImpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Allen Ziegenfus
 * @author Jiefeng Wu
 */
public class ExpandoValueLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			LocalizationUtil.class, "_localization", new LocalizationImpl());

		ClassNameLocalService classNameLocalService = Mockito.mock(
			ClassNameLocalService.class);

		Mockito.when(
			classNameLocalService.getClassNameId(_CLASS_NAME)
		).thenReturn(
			_CLASS_NAME_ID
		);

		ExpandoTablePersistence expandoTablePersistence = Mockito.mock(
			ExpandoTablePersistence.class);

		ExpandoTable table = new ExpandoTableImpl();

		table.setTableId(_TABLE_ID);

		Mockito.when(
			expandoTablePersistence.fetchByC_C_N(
				_COMPANY_ID, _CLASS_NAME_ID,
				ExpandoTableConstants.DEFAULT_TABLE_NAME)
		).thenReturn(
			table
		);

		_addColumn("geolocationColumn", ExpandoColumnConstants.GEOLOCATION, "");
		_addColumn(
			"stringColumn", ExpandoColumnConstants.STRING,
			RandomTestUtil.randomString());
		_addColumn(
			"unsetLocalizedArrayColumn",
			ExpandoColumnConstants.STRING_ARRAY_LOCALIZED, "a,b,c");
		_addColumn(
			"unsetSelectionColumn", ExpandoColumnConstants.STRING_ARRAY,
			"a,b,c");
		_addColumn(
			"unsetStringColumn", ExpandoColumnConstants.STRING, "unsetDefault");

		ExpandoColumnPersistence expandoColumnPersistence = Mockito.mock(
			ExpandoColumnPersistence.class);

		Mockito.when(
			expandoColumnPersistence.findByTableId(_TABLE_ID)
		).thenReturn(
			new ArrayList<>(_columns.values())
		);

		_expandoValuePersistence = Mockito.mock(ExpandoValuePersistence.class);

		List<ExpandoValue> values = Arrays.asList(
			_createValue(
				"geolocationColumn", "{\"latitude\": 1.5, \"longitude\": 2.5}"),
			_createValue("stringColumn", "value"));

		Mockito.when(
			_expandoValuePersistence.findByT_CPK(_TABLE_ID, _CLASS_PK)
		).thenReturn(
			values
		);

		ReflectionTestUtil.setFieldValue(
			_expandoValueLocalServiceImpl, "_classNameLocalService",
			classNameLocalService);
		ReflectionTestUtil.setFieldValue(
			_expandoValueLocalServiceImpl, "_expandoColumnPersistence",
			expandoColumnPersistence);
		ReflectionTestUtil.setFieldValue(
			_expandoValueLocalServiceImpl, "_expandoTablePersistence",
			expandoTablePersistence);
		ReflectionTestUtil.setFieldValue(
			_expandoValueLocalServiceImpl, "expandoValuePersistence",
			_expandoValuePersistence);
	}

	@Test
	public void testGetRowData() throws Exception {
		Map<String, Serializable> rowData =
			_expandoValueLocalServiceImpl.getRowData(
				_COMPANY_ID, _CLASS_NAME,
				ExpandoTableConstants.DEFAULT_TABLE_NAME, _CLASS_PK);

		Mockito.verify(
			_expandoValuePersistence, Mockito.times(1)
		).findByT_CPK(
			_TABLE_ID, _CLASS_PK
		);

		Mockito.verify(
			_expandoValuePersistence, Mockito.never()
		).fetchByT_C_C(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()
		);

		Assert.assertEquals(
			rowData.toString(), _columns.size(), rowData.size());

		JSONObject jsonObject = (JSONObject)rowData.get("geolocationColumn");

		Assert.assertEquals(1.5, jsonObject.getDouble("latitude"), 0);
		Assert.assertEquals(2.5, jsonObject.getDouble("longitude"), 0);

		Assert.assertEquals("value", rowData.get("stringColumn"));
		Assert.assertEquals(
			Collections.emptyMap(), rowData.get("unsetLocalizedArrayColumn"));
		Assert.assertArrayEquals(
			new String[0], (String[])rowData.get("unsetSelectionColumn"));
		Assert.assertEquals("unsetDefault", rowData.get("unsetStringColumn"));
	}

	private void _addColumn(String name, int type, String defaultData) {
		ExpandoColumn column = new ExpandoColumnImpl();

		column.setColumnId(_columns.size() + 1);
		column.setName(name);
		column.setType(type);
		column.setDefaultData(defaultData);

		_columns.put(name, column);
	}

	private ExpandoValue _createValue(String columnName, String data) {
		ExpandoValue value = new ExpandoValueImpl();

		value.setColumn(_columns.get(columnName));
		value.setData(data);

		return value;
	}

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private static final long _CLASS_NAME_ID = RandomTestUtil.randomLong();

	private static final long _CLASS_PK = RandomTestUtil.randomLong();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _TABLE_ID = RandomTestUtil.randomLong();

	private final Map<String, ExpandoColumn> _columns = new LinkedHashMap<>();
	private final ExpandoValueLocalServiceImpl _expandoValueLocalServiceImpl =
		new ExpandoValueLocalServiceImpl();
	private ExpandoValuePersistence _expandoValuePersistence;

}