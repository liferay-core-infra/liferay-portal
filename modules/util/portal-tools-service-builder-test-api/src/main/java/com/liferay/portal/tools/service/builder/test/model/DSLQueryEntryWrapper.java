/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link DSLQueryEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DSLQueryEntry
 * @generated
 */
public class DSLQueryEntryWrapper
	extends BaseModelWrapper<DSLQueryEntry>
	implements DSLQueryEntry, ModelWrapper<DSLQueryEntry> {

	public DSLQueryEntryWrapper(DSLQueryEntry dslQueryEntry) {
		super(dslQueryEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("dslQueryEntryId", getDslQueryEntryId());
		attributes.put("name", getName());
		attributes.put("bigDecimalValue", getBigDecimalValue());
		attributes.put("doubleValue", getDoubleValue());
		attributes.put("intValue", getIntValue());
		attributes.put("longValue", getLongValue());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long dslQueryEntryId = (Long)attributes.get("dslQueryEntryId");

		if (dslQueryEntryId != null) {
			setDslQueryEntryId(dslQueryEntryId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		BigDecimal bigDecimalValue = (BigDecimal)attributes.get(
			"bigDecimalValue");

		if (bigDecimalValue != null) {
			setBigDecimalValue(bigDecimalValue);
		}

		Double doubleValue = (Double)attributes.get("doubleValue");

		if (doubleValue != null) {
			setDoubleValue(doubleValue);
		}

		Integer intValue = (Integer)attributes.get("intValue");

		if (intValue != null) {
			setIntValue(intValue);
		}

		Long longValue = (Long)attributes.get("longValue");

		if (longValue != null) {
			setLongValue(longValue);
		}
	}

	@Override
	public DSLQueryEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the big decimal value of this dsl query entry.
	 *
	 * @return the big decimal value of this dsl query entry
	 */
	@Override
	public BigDecimal getBigDecimalValue() {
		return model.getBigDecimalValue();
	}

	/**
	 * Returns the double value of this dsl query entry.
	 *
	 * @return the double value of this dsl query entry
	 */
	@Override
	public double getDoubleValue() {
		return model.getDoubleValue();
	}

	/**
	 * Returns the dsl query entry ID of this dsl query entry.
	 *
	 * @return the dsl query entry ID of this dsl query entry
	 */
	@Override
	public long getDslQueryEntryId() {
		return model.getDslQueryEntryId();
	}

	/**
	 * Returns the int value of this dsl query entry.
	 *
	 * @return the int value of this dsl query entry
	 */
	@Override
	public int getIntValue() {
		return model.getIntValue();
	}

	/**
	 * Returns the long value of this dsl query entry.
	 *
	 * @return the long value of this dsl query entry
	 */
	@Override
	public long getLongValue() {
		return model.getLongValue();
	}

	/**
	 * Returns the name of this dsl query entry.
	 *
	 * @return the name of this dsl query entry
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this dsl query entry.
	 *
	 * @return the primary key of this dsl query entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the big decimal value of this dsl query entry.
	 *
	 * @param bigDecimalValue the big decimal value of this dsl query entry
	 */
	@Override
	public void setBigDecimalValue(BigDecimal bigDecimalValue) {
		model.setBigDecimalValue(bigDecimalValue);
	}

	/**
	 * Sets the double value of this dsl query entry.
	 *
	 * @param doubleValue the double value of this dsl query entry
	 */
	@Override
	public void setDoubleValue(double doubleValue) {
		model.setDoubleValue(doubleValue);
	}

	/**
	 * Sets the dsl query entry ID of this dsl query entry.
	 *
	 * @param dslQueryEntryId the dsl query entry ID of this dsl query entry
	 */
	@Override
	public void setDslQueryEntryId(long dslQueryEntryId) {
		model.setDslQueryEntryId(dslQueryEntryId);
	}

	/**
	 * Sets the int value of this dsl query entry.
	 *
	 * @param intValue the int value of this dsl query entry
	 */
	@Override
	public void setIntValue(int intValue) {
		model.setIntValue(intValue);
	}

	/**
	 * Sets the long value of this dsl query entry.
	 *
	 * @param longValue the long value of this dsl query entry
	 */
	@Override
	public void setLongValue(long longValue) {
		model.setLongValue(longValue);
	}

	/**
	 * Sets the name of this dsl query entry.
	 *
	 * @param name the name of this dsl query entry
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this dsl query entry.
	 *
	 * @param primaryKey the primary key of this dsl query entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected DSLQueryEntryWrapper wrap(DSLQueryEntry dslQueryEntry) {
		return new DSLQueryEntryWrapper(dslQueryEntry);
	}

}