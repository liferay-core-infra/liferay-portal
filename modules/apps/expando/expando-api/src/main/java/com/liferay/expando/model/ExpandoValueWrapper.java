/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link ExpandoValue}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoValue
 * @generated
 */
public class ExpandoValueWrapper
	extends BaseModelWrapper<ExpandoValue>
	implements ExpandoValue, ModelWrapper<ExpandoValue> {

	public ExpandoValueWrapper(ExpandoValue expandoValue) {
		super(expandoValue);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("valueId", getValueId());
		attributes.put("companyId", getCompanyId());
		attributes.put("tableId", getTableId());
		attributes.put("columnId", getColumnId());
		attributes.put("rowId", getRowId());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put("data", getData());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		Long valueId = (Long)attributes.get("valueId");

		if (valueId != null) {
			setValueId(valueId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long tableId = (Long)attributes.get("tableId");

		if (tableId != null) {
			setTableId(tableId);
		}

		Long columnId = (Long)attributes.get("columnId");

		if (columnId != null) {
			setColumnId(columnId);
		}

		Long rowId = (Long)attributes.get("rowId");

		if (rowId != null) {
			setRowId(rowId);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classPK = (Long)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
		}

		String data = (String)attributes.get("data");

		if (data != null) {
			setData(data);
		}
	}

	@Override
	public ExpandoValue cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the fully qualified class name of this expando value.
	 *
	 * @return the fully qualified class name of this expando value
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this expando value.
	 *
	 * @return the class name ID of this expando value
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class pk of this expando value.
	 *
	 * @return the class pk of this expando value
	 */
	@Override
	public long getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the column ID of this expando value.
	 *
	 * @return the column ID of this expando value
	 */
	@Override
	public long getColumnId() {
		return model.getColumnId();
	}

	/**
	 * Returns the company ID of this expando value.
	 *
	 * @return the company ID of this expando value
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the ct collection ID of this expando value.
	 *
	 * @return the ct collection ID of this expando value
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the data of this expando value.
	 *
	 * @return the data of this expando value
	 */
	@Override
	public String getData() {
		return model.getData();
	}

	/**
	 * Returns the mvcc version of this expando value.
	 *
	 * @return the mvcc version of this expando value
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this expando value.
	 *
	 * @return the primary key of this expando value
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the row ID of this expando value.
	 *
	 * @return the row ID of this expando value
	 */
	@Override
	public long getRowId() {
		return model.getRowId();
	}

	/**
	 * Returns the table ID of this expando value.
	 *
	 * @return the table ID of this expando value
	 */
	@Override
	public long getTableId() {
		return model.getTableId();
	}

	/**
	 * Returns the value ID of this expando value.
	 *
	 * @return the value ID of this expando value
	 */
	@Override
	public long getValueId() {
		return model.getValueId();
	}

	@Override
	public void persist() {
		model.persist();
	}

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this expando value.
	 *
	 * @param classNameId the class name ID of this expando value
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this expando value.
	 *
	 * @param classPK the class pk of this expando value
	 */
	@Override
	public void setClassPK(long classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the column ID of this expando value.
	 *
	 * @param columnId the column ID of this expando value
	 */
	@Override
	public void setColumnId(long columnId) {
		model.setColumnId(columnId);
	}

	/**
	 * Sets the company ID of this expando value.
	 *
	 * @param companyId the company ID of this expando value
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the ct collection ID of this expando value.
	 *
	 * @param ctCollectionId the ct collection ID of this expando value
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the data of this expando value.
	 *
	 * @param data the data of this expando value
	 */
	@Override
	public void setData(String data) {
		model.setData(data);
	}

	/**
	 * Sets the mvcc version of this expando value.
	 *
	 * @param mvccVersion the mvcc version of this expando value
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this expando value.
	 *
	 * @param primaryKey the primary key of this expando value
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the row ID of this expando value.
	 *
	 * @param rowId the row ID of this expando value
	 */
	@Override
	public void setRowId(long rowId) {
		model.setRowId(rowId);
	}

	/**
	 * Sets the table ID of this expando value.
	 *
	 * @param tableId the table ID of this expando value
	 */
	@Override
	public void setTableId(long tableId) {
		model.setTableId(tableId);
	}

	/**
	 * Sets the value ID of this expando value.
	 *
	 * @param valueId the value ID of this expando value
	 */
	@Override
	public void setValueId(long valueId) {
		model.setValueId(valueId);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<ExpandoValue, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<ExpandoValue, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected ExpandoValueWrapper wrap(ExpandoValue expandoValue) {
		return new ExpandoValueWrapper(expandoValue);
	}

}