/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.impl;

import com.liferay.expando.kernel.exception.ValueDataException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.persistence.ExpandoColumnPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoRowPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoTablePersistence;
import com.liferay.expando.kernel.util.ExpandoValueDeleteHandler;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.typeconverter.DateArrayConverter;
import com.liferay.portal.typeconverter.NumberArrayConverter;
import com.liferay.portal.typeconverter.NumberConverter;
import com.liferay.portlet.expando.model.impl.ExpandoValueImpl;
import com.liferay.portlet.expando.service.base.ExpandoValueLocalServiceBaseImpl;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import jodd.typeconverter.TypeConverterManager;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 */
public class ExpandoValueLocalServiceImpl
	extends ExpandoValueLocalServiceBaseImpl {

	public ExpandoValueLocalServiceImpl() {
		TypeConverterManager typeConverterManager = TypeConverterManager.get();

		typeConverterManager.register(Date[].class, new DateArrayConverter());
		typeConverterManager.register(Number.class, new NumberConverter());
		typeConverterManager.register(
			Number[].class, new NumberArrayConverter());
	}

	@Override
	public ExpandoValue addValue(
			long classNameId, long tableId, long columnId, long classPK,
			String data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByPrimaryKey(
			tableId);

		return doAddValue(
			expandoTable.getCompanyId(), classNameId, tableId, columnId,
			classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setBoolean(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean[] data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setBooleanArray(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setDate(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date[] data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setDateArray(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, double data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setDouble(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, double[] data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setDoubleArray(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, float data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setFloat(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, float[] data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setFloatArray(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, int data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setInteger(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, int[] data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setIntegerArray(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, JSONObject dataJSONObject)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setGeolocationJSONObject(dataJSONObject);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, long data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setLong(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, long[] data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setLongArray(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Map<Locale, ?> dataMap,
			Locale defaultLocale)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());

		int type = expandoColumn.getType();

		if (type == ExpandoColumnConstants.STRING_ARRAY_LOCALIZED) {
			expandoValue.setStringArrayMap(
				(Map<Locale, String[]>)dataMap, defaultLocale);
		}
		else {
			expandoValue.setStringMap(
				(Map<Locale, String>)dataMap, defaultLocale);
		}

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setNumber(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number[] data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setNumberArray(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Object data)
		throws PortalException {

		ExpandoColumn expandoColumn = null;

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		if (expandoTable != null) {
			expandoColumn = _expandoColumnPersistence.fetchByT_N(
				expandoTable.getTableId(), columnName);
		}

		int type = expandoColumn.getType();

		data = convertType(type, data);

		if (type == ExpandoColumnConstants.BOOLEAN) {
			Boolean booleanData = (Boolean)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				booleanData.booleanValue());
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(boolean[])data);
		}
		else if (type == ExpandoColumnConstants.DATE) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(Date)data);
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(Date[])data);
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			Double doubleData = (Double)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				doubleData.doubleValue());
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(double[])data);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			Float floatData = (Float)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				floatData.floatValue());
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(float[])data);
		}
		else if (type == ExpandoColumnConstants.GEOLOCATION) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				JSONFactoryUtil.createJSONObject(
					HtmlUtil.unescape(data.toString())));
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			Integer integerData = (Integer)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				integerData.intValue());
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(int[])data);
		}
		else if (type == ExpandoColumnConstants.LONG) {
			Long longData = (Long)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				longData.longValue());
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(long[])data);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(Number)data);
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(Number[])data);
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			Short shortData = (Short)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				shortData.shortValue());
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(short[])data);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(String[])data);
		}
		else if (type == ExpandoColumnConstants.STRING) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(String)data);
		}

		return expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK,
			(Map<Locale, ?>)data, LocaleUtil.getSiteDefault());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, short data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setShort(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, short[] data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setShortArray(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setString(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String[] data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setCompanyId(expandoTable.getCompanyId());
		expandoValue.setColumnId(expandoColumn.getColumnId());
		expandoValue.setStringArray(data);

		return expandoValueLocalService.addValue(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumn.getColumnId(), classPK, expandoValue.getData());
	}

	@Override
	public void addValues(
			long classNameId, long tableId, List<ExpandoColumn> expandoColumns,
			long classPK, Map<String, String> data)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByPrimaryKey(
			tableId);

		ExpandoRow expandoRow = _expandoRowPersistence.fetchByT_C(
			tableId, classPK);

		if (expandoRow == null) {
			long rowId = counterLocalService.increment();

			expandoRow = _expandoRowPersistence.create(rowId);

			expandoRow.setCompanyId(expandoTable.getCompanyId());
			expandoRow.setTableId(tableId);
			expandoRow.setClassPK(classPK);

			expandoRow = _expandoRowPersistence.update(expandoRow);
		}

		boolean rowModified = false;

		for (ExpandoColumn expandoColumn : expandoColumns) {
			String dataString = data.get(expandoColumn.getName());

			if (dataString == null) {
				continue;
			}

			ExpandoValue expandoValue = expandoValuePersistence.fetchByC_R(
				expandoColumn.getColumnId(), expandoRow.getRowId());

			if (expandoValue == null) {
				long valueId = counterLocalService.increment();

				expandoValue = expandoValuePersistence.create(valueId);

				expandoValue.setCompanyId(expandoTable.getCompanyId());
				expandoValue.setTableId(tableId);
				expandoValue.setColumnId(expandoColumn.getColumnId());
				expandoValue.setRowId(expandoRow.getRowId());
				expandoValue.setClassNameId(classNameId);
				expandoValue.setClassPK(classPK);
			}

			if (expandoValue.isNew() ||
				!Objects.equals(expandoValue.getData(), dataString)) {

				expandoValue.setData(dataString);

				expandoValuePersistence.update(expandoValue);

				rowModified = true;
			}
		}

		if (rowModified) {
			expandoRow.setModifiedDate(new Date());

			_expandoRowPersistence.update(expandoRow);
		}
	}

	@Override
	public void addValues(
			long companyId, long classNameId, String tableName, long classPK,
			Map<String, Serializable> attributes)
		throws PortalException {

		Map<String, String> data = new HashMap<>();

		ExpandoTable expandoTable = _expandoTablePersistence.findByC_C_N(
			companyId, classNameId, tableName);

		Collection<String> names = attributes.keySet();

		List<ExpandoColumn> expandoColumns =
			_expandoColumnPersistence.findByT_N(
				expandoTable.getTableId(), names.toArray(new String[0]));

		for (ExpandoColumn expandoColumn : expandoColumns) {
			ExpandoValue expandoValue = new ExpandoValueImpl();

			expandoValue.setCompanyId(companyId);

			Serializable attributeValue = attributes.get(
				expandoColumn.getName());

			expandoValue.setColumn(expandoColumn);

			int type = expandoColumn.getType();

			if (type == ExpandoColumnConstants.BOOLEAN) {
				expandoValue.setBoolean((Boolean)attributeValue);
			}
			else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
				expandoValue.setBooleanArray((boolean[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.DATE) {
				expandoValue.setDate((Date)attributeValue);
			}
			else if (type == ExpandoColumnConstants.DATE_ARRAY) {
				expandoValue.setDateArray((Date[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.DOUBLE) {
				expandoValue.setDouble((Double)attributeValue);
			}
			else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
				expandoValue.setDoubleArray((double[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.FLOAT) {
				expandoValue.setFloat((Float)attributeValue);
			}
			else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
				expandoValue.setFloatArray((float[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.GEOLOCATION) {
				expandoValue.setGeolocationJSONObject(
					JSONFactoryUtil.createJSONObject(
						attributeValue.toString()));
			}
			else if (type == ExpandoColumnConstants.INTEGER) {
				expandoValue.setInteger((Integer)attributeValue);
			}
			else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
				expandoValue.setIntegerArray((int[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.LONG) {
				expandoValue.setLong((Long)attributeValue);
			}
			else if (type == ExpandoColumnConstants.LONG_ARRAY) {
				expandoValue.setLongArray((long[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.NUMBER) {
				expandoValue.setNumber((Number)attributeValue);
			}
			else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
				expandoValue.setNumberArray((Number[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.SHORT) {
				expandoValue.setShort((Short)attributeValue);
			}
			else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
				expandoValue.setShortArray((short[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.STRING_ARRAY) {
				expandoValue.setStringArray((String[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
				Map<Locale, String> defaultValuesMap =
					(Map<Locale, String>)attributeValue;

				Locale defaultLocale = LocaleUtil.getSiteDefault();

				if (Validator.isNull(defaultValuesMap.get(defaultLocale))) {
					for (String defaultValue : defaultValuesMap.values()) {
						if (Validator.isNotNull(defaultValue)) {
							throw new ValueDataException.
								MustInformDefaultLocale(defaultLocale);
						}
					}
				}

				expandoValue.setStringMap(
					(Map<Locale, String>)attributeValue, defaultLocale);
			}
			else {
				expandoValue.setString((String)attributeValue);
			}

			data.put(expandoColumn.getName(), expandoValue.getData());
		}

		addValues(
			expandoTable.getClassNameId(), expandoTable.getTableId(),
			expandoColumns, classPK, data);
	}

	@Override
	public void addValues(
			long companyId, String className, String tableName, long classPK,
			Map<String, Serializable> attributes)
		throws PortalException {

		addValues(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, classPK, attributes);
	}

	@Override
	public void deleteColumnValues(long columnId) {
		List<ExpandoValue> expandoValues =
			expandoValuePersistence.findByColumnId(columnId);

		for (ExpandoValue expandoValue : expandoValues) {
			deleteValue(expandoValue);
		}
	}

	@Override
	public void deleteRowValues(long rowId) {
		List<ExpandoValue> expandoValues = expandoValuePersistence.findByRowId(
			rowId);

		for (ExpandoValue expandoValue : expandoValues) {
			deleteValue(expandoValue);
		}
	}

	@Override
	public void deleteTableValues(long tableId) {
		List<ExpandoValue> expandoValues =
			expandoValuePersistence.findByTableId(tableId);

		for (ExpandoValue expandoValue : expandoValues) {
			deleteValue(expandoValue);
		}
	}

	@Override
	public void deleteValue(ExpandoValue expandoValue) {
		expandoValuePersistence.remove(expandoValue);

		// Notify delete handlers

		List<ExpandoValueDeleteHandler> expandoValueDeleteHandlers =
			ExpandoValueDeleteHandlerHolder.getService(
				expandoValue.getClassName());

		if (expandoValueDeleteHandlers != null) {
			for (ExpandoValueDeleteHandler expandoValueDeleteHandler :
					expandoValueDeleteHandlers) {

				expandoValueDeleteHandler.deletedExpandoValue(
					expandoValue.getClassPK());
			}
		}

		List<ExpandoValue> values = expandoValuePersistence.findByRowId(
			expandoValue.getRowId());

		if (values.isEmpty()) {
			ExpandoRow expandoRow = _expandoRowPersistence.fetchByPrimaryKey(
				expandoValue.getRowId());

			if (expandoRow != null) {
				_expandoRowPersistence.remove(expandoRow);
			}
		}
	}

	@Override
	public void deleteValue(long valueId) throws PortalException {
		ExpandoValue expandoValue = expandoValuePersistence.findByPrimaryKey(
			valueId);

		deleteValue(expandoValue);
	}

	@Override
	public void deleteValue(long columnId, long rowId) throws PortalException {
		ExpandoValue expandoValue = expandoValuePersistence.findByC_R(
			columnId, rowId);

		deleteValue(expandoValue);
	}

	@Override
	public void deleteValue(
			long companyId, long classNameId, String tableName,
			String columnName, long classPK)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (expandoTable == null) {
			return;
		}

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		if (expandoColumn == null) {
			return;
		}

		ExpandoValue expandoValue = expandoValuePersistence.fetchByT_C_C(
			expandoTable.getTableId(), expandoColumn.getColumnId(), classPK);

		if (expandoValue != null) {
			deleteValue(expandoValue.getValueId());
		}
	}

	@Override
	public void deleteValue(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException {

		expandoValueLocalService.deleteValue(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName, classPK);
	}

	@Override
	public void deleteValues(long classNameId, long classPK) {
		List<ExpandoValue> expandoValues = expandoValuePersistence.findByC_C(
			classNameId, classPK);

		for (ExpandoValue expandoValue : expandoValues) {
			deleteValue(expandoValue);
		}
	}

	@Override
	public void deleteValues(String className, long classPK) {
		expandoValueLocalService.deleteValues(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public List<ExpandoValue> getColumnValues(
		long columnId, int start, int end) {

		return expandoValuePersistence.findByColumnId(columnId, start, end);
	}

	@Override
	public List<ExpandoValue> getColumnValues(
		long companyId, long classNameId, String tableName, String columnName,
		int start, int end) {

		return expandoValueLocalService.getColumnValues(
			companyId, classNameId, tableName, columnName, null, start, end);
	}

	@Override
	public List<ExpandoValue> getColumnValues(
		long companyId, long classNameId, String tableName, String columnName,
		String data, int start, int end) {

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (expandoTable == null) {
			return Collections.emptyList();
		}

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		if (expandoColumn == null) {
			return Collections.emptyList();
		}

		if (data == null) {
			return expandoValuePersistence.findByT_C(
				expandoTable.getTableId(), expandoColumn.getColumnId(), start,
				end);
		}

		return expandoValuePersistence.findByT_C_D(
			expandoTable.getTableId(), expandoColumn.getColumnId(), data, start,
			end);
	}

	@Override
	public List<ExpandoValue> getColumnValues(
		long companyId, String className, String tableName, String columnName,
		int start, int end) {

		return expandoValueLocalService.getColumnValues(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName, start, end);
	}

	@Override
	public List<ExpandoValue> getColumnValues(
		long companyId, String className, String tableName, String columnName,
		String data, int start, int end) {

		return expandoValueLocalService.getColumnValues(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName, data, start, end);
	}

	@Override
	public int getColumnValuesCount(long columnId) {
		return expandoValuePersistence.countByColumnId(columnId);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, long classNameId, String tableName, String columnName) {

		return expandoValueLocalService.getColumnValuesCount(
			companyId, classNameId, tableName, columnName, null);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, long classNameId, String tableName, String columnName,
		String data) {

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (expandoTable == null) {
			return 0;
		}

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		if (expandoColumn == null) {
			return 0;
		}

		if (data == null) {
			return expandoValuePersistence.countByT_C(
				expandoTable.getTableId(), expandoColumn.getColumnId());
		}

		return expandoValuePersistence.countByT_C_D(
			expandoTable.getTableId(), expandoColumn.getColumnId(), data);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, String className, String tableName, String columnName) {

		return expandoValueLocalService.getColumnValuesCount(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, String className, String tableName, String columnName,
		String data) {

		return expandoValueLocalService.getColumnValuesCount(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName, data);
	}

	@Override
	public Map<String, Serializable> getData(
			long companyId, String className, String tableName,
			Collection<String> columnNames, long classPK)
		throws PortalException {

		List<ExpandoColumn> expandoColumns = Collections.emptyList();

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		if (expandoTable != null) {
			expandoColumns = _expandoColumnPersistence.findByT_N(
				expandoTable.getTableId(), columnNames.toArray(new String[0]));
		}

		Map<String, Serializable> attributeValues = new HashMap<>(
			(int)(columnNames.size() * 1.4));

		ExpandoValue expandoValue = new ExpandoValueImpl();

		for (ExpandoColumn expandoColumn : expandoColumns) {
			expandoValue.setColumn(expandoColumn);
			expandoValue.setData(expandoColumn.getDefaultData());

			Serializable attributeValue = doGetData(
				companyId, className, tableName, expandoColumn.getName(),
				classPK, expandoValue, expandoColumn.getType());

			attributeValues.put(expandoColumn.getName(), attributeValue);
		}

		return attributeValues;
	}

	@Override
	public Serializable getData(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException {

		ExpandoColumn expandoColumn = null;

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		if (expandoTable != null) {
			expandoColumn = _expandoColumnPersistence.fetchByT_N(
				expandoTable.getTableId(), columnName);
		}

		if (expandoColumn == null) {
			return null;
		}

		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setColumn(expandoColumn);
		expandoValue.setData(expandoColumn.getDefaultData());

		return doGetData(
			companyId, className, tableName, columnName, classPK, expandoValue,
			expandoColumn.getType());
	}

	@Override
	public boolean getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getBoolean();
	}

	@Override
	public boolean[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean[] defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getBooleanArray();
	}

	@Override
	public Date getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getDate();
	}

	@Override
	public Date[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date[] defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getDateArray();
	}

	@Override
	public double getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, double defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getDouble();
	}

	@Override
	public double[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, double[] defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getDoubleArray();
	}

	@Override
	public float getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, float defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getFloat();
	}

	@Override
	public float[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, float[] defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getFloatArray();
	}

	@Override
	public int getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, int defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getInteger();
	}

	@Override
	public int[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, int[] defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getIntegerArray();
	}

	@Override
	public JSONObject getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, JSONObject defaultDataJSONObject)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultDataJSONObject;
		}

		return expandoValue.getGeolocationJSONObject();
	}

	@Override
	public long getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, long defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getLong();
	}

	@Override
	public long[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, long[] defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getLongArray();
	}

	@Override
	public Map<?, ?> getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Map<?, ?> defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		ExpandoColumn expandoColumn = expandoValue.getColumn();

		int type = expandoColumn.getType();

		if (type == ExpandoColumnConstants.STRING_ARRAY_LOCALIZED) {
			return expandoValue.getStringArrayMap();
		}

		return expandoValue.getStringMap();
	}

	@Override
	public Number getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getNumber();
	}

	@Override
	public Number[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number[] defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getNumberArray();
	}

	@Override
	public short getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, short defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getShort();
	}

	@Override
	public short[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, short[] defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getShortArray();
	}

	@Override
	public String getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, String defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getString();
	}

	@Override
	public String[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, String[] defaultData)
		throws PortalException {

		ExpandoValue expandoValue = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (expandoValue == null) {
			return defaultData;
		}

		return expandoValue.getStringArray();
	}

	@Override
	public List<ExpandoValue> getDefaultTableColumnValues(
		long companyId, long classNameId, String columnName, int start,
		int end) {

		return expandoValueLocalService.getColumnValues(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME,
			columnName, start, end);
	}

	@Override
	public List<ExpandoValue> getDefaultTableColumnValues(
		long companyId, String className, String columnName, int start,
		int end) {

		return expandoValueLocalService.getDefaultTableColumnValues(
			companyId, _classNameLocalService.getClassNameId(className),
			columnName, start, end);
	}

	@Override
	public int getDefaultTableColumnValuesCount(
		long companyId, long classNameId, String columnName) {

		return expandoValueLocalService.getColumnValuesCount(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME,
			columnName);
	}

	@Override
	public int getDefaultTableColumnValuesCount(
		long companyId, String className, String columnName) {

		return expandoValueLocalService.getDefaultTableColumnValuesCount(
			companyId, _classNameLocalService.getClassNameId(className),
			columnName);
	}

	@Override
	public List<ExpandoValue> getRowValues(long rowId) {
		return expandoValuePersistence.findByRowId(rowId);
	}

	@Override
	public List<ExpandoValue> getRowValues(long rowId, int start, int end) {
		return expandoValuePersistence.findByRowId(rowId, start, end);
	}

	@Override
	public List<ExpandoValue> getRowValues(
		long companyId, long classNameId, String tableName, long classPK,
		int start, int end) {

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (expandoTable == null) {
			return Collections.emptyList();
		}

		return expandoValuePersistence.findByT_CPK(
			expandoTable.getTableId(), classPK, start, end);
	}

	@Override
	public List<ExpandoValue> getRowValues(
		long companyId, String className, String tableName, long classPK,
		int start, int end) {

		return expandoValueLocalService.getRowValues(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, classPK, start, end);
	}

	@Override
	public int getRowValuesCount(long rowId) {
		return expandoValuePersistence.countByRowId(rowId);
	}

	@Override
	public int getRowValuesCount(
		long companyId, long classNameId, String tableName, long classPK) {

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (expandoTable == null) {
			return 0;
		}

		return expandoValuePersistence.countByT_CPK(
			expandoTable.getTableId(), classPK);
	}

	@Override
	public int getRowValuesCount(
		long companyId, String className, String tableName, long classPK) {

		return expandoValueLocalService.getRowValuesCount(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, classPK);
	}

	@Override
	public ExpandoValue getValue(long valueId) throws PortalException {
		return expandoValuePersistence.findByPrimaryKey(valueId);
	}

	@Override
	public ExpandoValue getValue(long columnId, long rowId)
		throws PortalException {

		return expandoValuePersistence.findByC_R(columnId, rowId);
	}

	@Override
	public ExpandoValue getValue(long tableId, long columnId, long classPK) {
		return expandoValuePersistence.fetchByT_C_C(tableId, columnId, classPK);
	}

	@Override
	public ExpandoValue getValue(
		long companyId, long classNameId, String tableName, String columnName,
		long classPK) {

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (expandoTable == null) {
			return null;
		}

		ExpandoColumn expandoColumn = _expandoColumnPersistence.fetchByT_N(
			expandoTable.getTableId(), columnName);

		if (expandoColumn == null) {
			return null;
		}

		return expandoValuePersistence.fetchByT_C_C(
			expandoTable.getTableId(), expandoColumn.getColumnId(), classPK);
	}

	@Override
	public ExpandoValue getValue(
		long companyId, String className, String tableName, String columnName,
		long classPK) {

		return expandoValueLocalService.getValue(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName, classPK);
	}

	protected <T> T convertType(int type, Object data) {
		if (data == null) {
			return (T)data;
		}

		data = handleCollections(type, data);
		data = handleStrings(type, data);

		TypeConverterManager typeConverterManager = TypeConverterManager.get();

		if (type == ExpandoColumnConstants.BOOLEAN) {
			data = typeConverterManager.convertType(data, Boolean.TYPE);
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			data = typeConverterManager.convertType(data, boolean[].class);
		}
		else if (type == ExpandoColumnConstants.DATE) {
			data = typeConverterManager.convertType(data, Date.class);
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
			data = typeConverterManager.convertType(data, Date[].class);
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			data = typeConverterManager.convertType(data, Double.TYPE);
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			data = typeConverterManager.convertType(data, double[].class);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			data = typeConverterManager.convertType(data, Float.TYPE);
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			data = typeConverterManager.convertType(data, float[].class);
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			data = typeConverterManager.convertType(data, Integer.TYPE);
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			data = typeConverterManager.convertType(data, int[].class);
		}
		else if (type == ExpandoColumnConstants.LONG) {
			data = typeConverterManager.convertType(data, Long.TYPE);
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			data = typeConverterManager.convertType(data, long[].class);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			data = typeConverterManager.convertType(data, Number.class);
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			data = typeConverterManager.convertType(data, Number[].class);
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			data = typeConverterManager.convertType(data, Short.TYPE);
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			data = typeConverterManager.convertType(data, short[].class);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			data = typeConverterManager.convertType(data, String[].class);
		}

		return (T)data;
	}

	protected ExpandoValue doAddValue(
		long companyId, long classNameId, long tableId, long columnId,
		long classPK, String data) {

		ExpandoValue expandoValue = expandoValuePersistence.fetchByT_C_C(
			tableId, columnId, classPK);

		if (expandoValue == null) {
			ExpandoRow expandoRow = _expandoRowPersistence.fetchByT_C(
				tableId, classPK);

			if (expandoRow == null) {
				expandoRow = _expandoRowPersistence.create(
					counterLocalService.increment());

				expandoRow.setCompanyId(companyId);
				expandoRow.setModifiedDate(new Date());
				expandoRow.setTableId(tableId);
				expandoRow.setClassPK(classPK);

				expandoRow = _expandoRowPersistence.update(expandoRow);
			}

			expandoValue = expandoValuePersistence.create(
				counterLocalService.increment());

			expandoValue.setCompanyId(companyId);
			expandoValue.setTableId(tableId);
			expandoValue.setColumnId(columnId);
			expandoValue.setRowId(expandoRow.getRowId());
			expandoValue.setClassNameId(classNameId);
			expandoValue.setClassPK(classPK);
			expandoValue.setData(data);

			return expandoValuePersistence.update(expandoValue);
		}

		if (!Objects.equals(expandoValue.getData(), data)) {
			expandoValue.setData(data);

			expandoValue = expandoValuePersistence.update(expandoValue);

			ExpandoRow expandoRow = _expandoRowPersistence.fetchByT_C(
				tableId, classPK);

			expandoRow.setModifiedDate(new Date());

			_expandoRowPersistence.update(expandoRow);
		}

		return expandoValue;
	}

	protected Serializable doGetData(
			long companyId, String className, String tableName,
			String columnName, long classPK, ExpandoValue expandoValue,
			int type)
		throws PortalException {

		if (type == ExpandoColumnConstants.BOOLEAN) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				expandoValue.getBoolean());
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new boolean[0]);
		}
		else if (type == ExpandoColumnConstants.DATE) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				expandoValue.getDate());
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new Date[0]);
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				expandoValue.getDouble());
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new double[0]);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				expandoValue.getFloat());
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new float[0]);
		}
		else if (type == ExpandoColumnConstants.GEOLOCATION) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				expandoValue.getGeolocationJSONObject());
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				expandoValue.getInteger());
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new int[0]);
		}
		else if (type == ExpandoColumnConstants.LONG) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				expandoValue.getLong());
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new long[0]);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				expandoValue.getNumber());
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new Number[0]);
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				expandoValue.getShort());
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new short[0]);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new String[0]);
		}
		else if (type == ExpandoColumnConstants.STRING) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				expandoValue.getString());
		}

		return (Serializable)expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK,
			new HashMap<Object, Object>());
	}

	protected Object handleCollections(int type, Object object) {
		if (!(object instanceof Collection) || !isTypeArray(type)) {
			return object;
		}

		Collection<?> collection = (Collection<?>)object;

		return collection.toArray();
	}

	protected Object handleStrings(int type, Object object) {
		if (!(object instanceof String)) {
			return object;
		}

		String string = (String)object;

		if (isTypeArray(type) && string.startsWith(StringPool.OPEN_BRACKET) &&
			string.endsWith(StringPool.CLOSE_BRACKET)) {

			string = string.substring(1, string.length() - 1);
		}

		return string;
	}

	protected boolean isTypeArray(int type) {
		if ((type == ExpandoColumnConstants.BOOLEAN_ARRAY) ||
			(type == ExpandoColumnConstants.DATE_ARRAY) ||
			(type == ExpandoColumnConstants.DOUBLE_ARRAY) ||
			(type == ExpandoColumnConstants.FLOAT_ARRAY) ||
			(type == ExpandoColumnConstants.INTEGER_ARRAY) ||
			(type == ExpandoColumnConstants.LONG_ARRAY) ||
			(type == ExpandoColumnConstants.NUMBER_ARRAY) ||
			(type == ExpandoColumnConstants.SHORT_ARRAY) ||
			(type == ExpandoColumnConstants.STRING_ARRAY)) {

			return true;
		}

		return false;
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ExpandoColumnPersistence.class)
	private ExpandoColumnPersistence _expandoColumnPersistence;

	@BeanReference(type = ExpandoRowPersistence.class)
	private ExpandoRowPersistence _expandoRowPersistence;

	@BeanReference(type = ExpandoTablePersistence.class)
	private ExpandoTablePersistence _expandoTablePersistence;

	private static class ExpandoValueDeleteHandlerHolder {

		public static List<ExpandoValueDeleteHandler> getService(String key) {
			return _serviceTrackerMap.getService(key);
		}

		private static final ServiceTrackerMap
			<String, List<ExpandoValueDeleteHandler>> _serviceTrackerMap =
				ServiceTrackerMapFactory.openMultiValueMap(
					SystemBundleUtil.getBundleContext(),
					ExpandoValueDeleteHandler.class, "model.class.name");

	}

}