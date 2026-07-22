/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link DateTypeEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DateTypeEntry
 * @generated
 */
public class DateTypeEntryWrapper
	extends BaseModelWrapper<DateTypeEntry>
	implements DateTypeEntry, ModelWrapper<DateTypeEntry> {

	public DateTypeEntryWrapper(DateTypeEntry dateTypeEntry) {
		super(dateTypeEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("dateTypeEntryId", getDateTypeEntryId());
		attributes.put("dateValue", getDateValue());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long dateTypeEntryId = (Long)attributes.get("dateTypeEntryId");

		if (dateTypeEntryId != null) {
			setDateTypeEntryId(dateTypeEntryId);
		}

		Date dateValue = (Date)attributes.get("dateValue");

		if (dateValue != null) {
			setDateValue(dateValue);
		}
	}

	@Override
	public DateTypeEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the date type entry ID of this date type entry.
	 *
	 * @return the date type entry ID of this date type entry
	 */
	@Override
	public long getDateTypeEntryId() {
		return model.getDateTypeEntryId();
	}

	/**
	 * Returns the date value of this date type entry.
	 *
	 * @return the date value of this date type entry
	 */
	@Override
	public Date getDateValue() {
		return model.getDateValue();
	}

	/**
	 * Returns the primary key of this date type entry.
	 *
	 * @return the primary key of this date type entry
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
	 * Sets the date type entry ID of this date type entry.
	 *
	 * @param dateTypeEntryId the date type entry ID of this date type entry
	 */
	@Override
	public void setDateTypeEntryId(long dateTypeEntryId) {
		model.setDateTypeEntryId(dateTypeEntryId);
	}

	/**
	 * Sets the date value of this date type entry.
	 *
	 * @param dateValue the date value of this date type entry
	 */
	@Override
	public void setDateValue(Date dateValue) {
		model.setDateValue(dateValue);
	}

	/**
	 * Sets the primary key of this date type entry.
	 *
	 * @param primaryKey the primary key of this date type entry
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
	protected DateTypeEntryWrapper wrap(DateTypeEntry dateTypeEntry) {
		return new DateTypeEntryWrapper(dateTypeEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-207772052