/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.impl;

import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.kernel.service.persistence.ExpandoTablePersistence;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portlet.expando.service.base.ExpandoRowLocalServiceBaseImpl;

import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
public class ExpandoRowLocalServiceImpl extends ExpandoRowLocalServiceBaseImpl {

	@Override
	public ExpandoRow addRow(long tableId, long classPK)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTablePersistence.findByPrimaryKey(
			tableId);

		long rowId = counterLocalService.increment();

		ExpandoRow expandoRow = expandoRowPersistence.create(rowId);

		expandoRow.setCompanyId(expandoTable.getCompanyId());
		expandoRow.setTableId(tableId);
		expandoRow.setClassPK(classPK);

		return expandoRowPersistence.update(expandoRow);
	}

	@Override
	public void deleteRow(ExpandoRow expandoRow) {

		// Row

		expandoRowPersistence.remove(expandoRow);

		// Values

		_expandoValueLocalService.deleteRowValues(expandoRow.getRowId());
	}

	@Override
	public void deleteRow(long rowId) throws PortalException {
		ExpandoRow expandoRow = expandoRowPersistence.findByPrimaryKey(rowId);

		deleteRow(expandoRow);
	}

	@Override
	public void deleteRow(long tableId, long classPK) throws PortalException {
		ExpandoRow expandoRow = expandoRowPersistence.findByT_C(
			tableId, classPK);

		deleteRow(expandoRow);
	}

	@Override
	public void deleteRow(
			long companyId, long classNameId, String tableName, long classPK)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTableLocalService.getTable(
			companyId, classNameId, tableName);

		expandoRowLocalService.deleteRow(expandoTable.getTableId(), classPK);
	}

	@Override
	public void deleteRow(
			long companyId, String className, String tableName, long classPK)
		throws PortalException {

		expandoRowLocalService.deleteRow(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, classPK);
	}

	@Override
	public void deleteRows(long classPK) {
		List<ExpandoRow> expandoRows = expandoRowPersistence.findByClassPK(
			classPK);

		for (ExpandoRow expandoRow : expandoRows) {
			deleteRow(expandoRow);
		}
	}

	@Override
	public void deleteRows(long companyId, long classNameId, long classPK) {
		List<ExpandoTable> expandoTables = _expandoTableLocalService.getTables(
			companyId, classNameId);

		for (ExpandoTable expandoTable : expandoTables) {
			ExpandoRow expandoRow = expandoRowPersistence.fetchByT_C(
				expandoTable.getTableId(), classPK);

			if (expandoRow == null) {
				continue;
			}

			deleteRow(expandoRow);
		}
	}

	@Override
	public ExpandoRow fetchRow(long tableId, long classPK) {
		return expandoRowPersistence.fetchByT_C(tableId, classPK);
	}

	@Override
	public List<ExpandoRow> getDefaultTableRows(
		long companyId, long classNameId, int start, int end) {

		return expandoRowLocalService.getRows(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME,
			start, end);
	}

	@Override
	public List<ExpandoRow> getDefaultTableRows(
		long companyId, String className, int start, int end) {

		return expandoRowLocalService.getDefaultTableRows(
			companyId, _classNameLocalService.getClassNameId(className), start,
			end);
	}

	@Override
	public int getDefaultTableRowsCount(long companyId, long classNameId) {
		return expandoRowLocalService.getRowsCount(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME);
	}

	@Override
	public int getDefaultTableRowsCount(long companyId, String className) {
		return expandoRowLocalService.getDefaultTableRowsCount(
			companyId, _classNameLocalService.getClassNameId(className));
	}

	@Override
	public ExpandoRow getRow(long rowId) throws PortalException {
		return expandoRowPersistence.findByPrimaryKey(rowId);
	}

	@Override
	public ExpandoRow getRow(long tableId, long classPK)
		throws PortalException {

		return expandoRowPersistence.findByT_C(tableId, classPK);
	}

	@Override
	public ExpandoRow getRow(
		long companyId, long classNameId, String tableName, long classPK) {

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (expandoTable == null) {
			return null;
		}

		return expandoRowPersistence.fetchByT_C(
			expandoTable.getTableId(), classPK);
	}

	@Override
	public ExpandoRow getRow(
		long companyId, String className, String tableName, long classPK) {

		return expandoRowLocalService.getRow(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, classPK);
	}

	@Override
	public List<ExpandoRow> getRows(long tableId, int start, int end) {
		return expandoRowPersistence.findByTableId(tableId, start, end);
	}

	@Override
	public List<ExpandoRow> getRows(
		long companyId, long classNameId, String tableName, int start,
		int end) {

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (expandoTable == null) {
			return Collections.emptyList();
		}

		return expandoRowPersistence.findByTableId(
			expandoTable.getTableId(), start, end);
	}

	@Override
	public List<ExpandoRow> getRows(
		long companyId, String className, String tableName, int start,
		int end) {

		return expandoRowLocalService.getRows(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, start, end);
	}

	@Override
	public int getRowsCount(long tableId) {
		return expandoRowPersistence.countByTableId(tableId);
	}

	@Override
	public int getRowsCount(
		long companyId, long classNameId, String tableName) {

		ExpandoTable expandoTable = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (expandoTable == null) {
			return 0;
		}

		return expandoRowPersistence.countByTableId(expandoTable.getTableId());
	}

	@Override
	public int getRowsCount(
		long companyId, String className, String tableName) {

		return expandoRowLocalService.getRowsCount(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ExpandoTableLocalService.class)
	private ExpandoTableLocalService _expandoTableLocalService;

	@BeanReference(type = ExpandoTablePersistence.class)
	private ExpandoTablePersistence _expandoTablePersistence;

	@BeanReference(type = ExpandoValueLocalService.class)
	private ExpandoValueLocalService _expandoValueLocalService;

}