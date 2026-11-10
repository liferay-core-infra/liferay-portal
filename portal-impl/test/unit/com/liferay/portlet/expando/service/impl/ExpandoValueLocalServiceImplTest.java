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
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Allen Ziegenfus
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

		ExpandoTable table = new ExpandoTableImpl();

		table.setTableId(_TABLE_ID);

		ExpandoTablePersistence expandoTablePersistence = Mockito.mock(
			ExpandoTablePersistence.class);

		Mockito.when(
			expandoTablePersistence.fetchByC_C_N(
				_COMPANY_ID, _CLASS_NAME_ID,
				ExpandoTableConstants.DEFAULT_TABLE_NAME)
		).thenReturn(
			table
		);

		_addColumn(1, "integerColumn", ExpandoColumnConstants.INTEGER, "0");
		_addColumn(2, "stringColumn", ExpandoColumnConstants.STRING, "default");
		_addColumn(
			3, "unsetStringColumn", ExpandoColumnConstants.STRING,
			"unsetDefault");
		_addColumn(4, "longArrayColumn", ExpandoColumnConstants.LONG_ARRAY, "");
		_addColumn(
			5, "unsetLongArrayColumn", ExpandoColumnConstants.LONG_ARRAY, "");
		_addColumn(
			6, "unsetBooleanColumn", ExpandoColumnConstants.BOOLEAN, "true");
		_addColumn(
			7, "geolocationColumn", ExpandoColumnConstants.GEOLOCATION, "");
		_addColumn(
			8, "unsetSelectionColumn", ExpandoColumnConstants.STRING_ARRAY,
			"a,b,c");
		_addColumn(
			9, "unsetLocalizedColumn", ExpandoColumnConstants.STRING_LOCALIZED,
			"");

		ExpandoColumnPersistence expandoColumnPersistence = Mockito.mock(
			ExpandoColumnPersistence.class);

		Mockito.when(
			expandoColumnPersistence.findByT_N(
				Mockito.eq(_TABLE_ID), Mockito.any(String[].class))
		).thenReturn(
			new ArrayList<>(_columns.values())
		);

		for (ExpandoColumn column : _columns.values()) {
			Mockito.when(
				expandoColumnPersistence.fetchByT_N(_TABLE_ID, column.getName())
			).thenReturn(
				column
			);
		}

		_expandoValuePersistence = Mockito.mock(ExpandoValuePersistence.class);

		List<ExpandoValue> values = Arrays.asList(
			_addValue(
				"geolocationColumn", "{\"latitude\":1.5,\"longitude\":2.5}"),
			_addValue("integerColumn", "42"),
			_addValue("longArrayColumn", "1,2,3"),
			_addValue("stringColumn", "value"));

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
			_expandoValueLocalServiceImpl, "expandoValueLocalService",
			_expandoValueLocalServiceImpl);
		ReflectionTestUtil.setFieldValue(
			_expandoValueLocalServiceImpl, "expandoValuePersistence",
			_expandoValuePersistence);
	}

	@Test
	public void testGetDataWithColumnNames() throws Exception {
		Map<String, Serializable> data = _getData();

		Assert.assertEquals(data.toString(), _columns.size(), data.size());

		JSONObject jsonObject = (JSONObject)data.get("geolocationColumn");

		Assert.assertEquals(1.5, jsonObject.getDouble("latitude"), 0);
		Assert.assertEquals(2.5, jsonObject.getDouble("longitude"), 0);

		Assert.assertEquals(42, data.get("integerColumn"));
		Assert.assertArrayEquals(
			new long[] {1, 2, 3}, (long[])data.get("longArrayColumn"));
		Assert.assertEquals("value", data.get("stringColumn"));
		Assert.assertTrue((Boolean)data.get("unsetBooleanColumn"));
		Assert.assertArrayEquals(
			new long[0], (long[])data.get("unsetLongArrayColumn"));
		Assert.assertEquals(
			Collections.emptyMap(), data.get("unsetLocalizedColumn"));
		Assert.assertArrayEquals(
			new String[0], (String[])data.get("unsetSelectionColumn"));
		Assert.assertEquals("unsetDefault", data.get("unsetStringColumn"));

		// All values of the row are read with one query instead of one
		// query per column

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
	}

	@Test
	public void testGetDataWithColumnNamesMatchesGetDataWithColumnName()
		throws Exception {

		Map<String, Serializable> data = _getData();

		for (String columnName : _columns.keySet()) {
			Serializable expectedData = _expandoValueLocalServiceImpl.getData(
				_COMPANY_ID, _CLASS_NAME,
				ExpandoTableConstants.DEFAULT_TABLE_NAME, columnName,
				_CLASS_PK);

			Serializable actualData = data.get(columnName);

			if (expectedData instanceof JSONObject) {
				expectedData = expectedData.toString();
				actualData = actualData.toString();
			}

			Assert.assertTrue(
				columnName, Objects.deepEquals(expectedData, actualData));
		}
	}

	private ExpandoColumn _addColumn(
		long columnId, String name, int type, String defaultData) {

		ExpandoColumn column = new ExpandoColumnImpl();

		column.setColumnId(columnId);
		column.setCompanyId(_COMPANY_ID);
		column.setTableId(_TABLE_ID);
		column.setName(name);
		column.setType(type);
		column.setDefaultData(defaultData);

		_columns.put(name, column);

		return column;
	}

	private ExpandoValue _addValue(String columnName, String data) {
		ExpandoColumn column = _columns.get(columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(_COMPANY_ID);
		value.setTableId(_TABLE_ID);
		value.setColumn(column);
		value.setClassNameId(_CLASS_NAME_ID);
		value.setClassPK(_CLASS_PK);
		value.setData(data);

		Mockito.when(
			_expandoValuePersistence.fetchByT_C_C(
				_TABLE_ID, column.getColumnId(), _CLASS_PK)
		).thenReturn(
			value
		);

		return value;
	}

	private Map<String, Serializable> _getData() throws Exception {
		return _expandoValueLocalServiceImpl.getData(
			_COMPANY_ID, _CLASS_NAME, ExpandoTableConstants.DEFAULT_TABLE_NAME,
			_columns.keySet(), _CLASS_PK);
	}

	private static final String _CLASS_NAME = "com.liferay.Test";

	private static final long _CLASS_NAME_ID = 100;

	private static final long _CLASS_PK = 300;

	private static final long _COMPANY_ID = 10;

	private static final long _TABLE_ID = 200;

	private final Map<String, ExpandoColumn> _columns = new LinkedHashMap<>();
	private final ExpandoValueLocalServiceImpl _expandoValueLocalServiceImpl =
		new ExpandoValueLocalServiceImpl();
	private ExpandoValuePersistence _expandoValuePersistence;

}