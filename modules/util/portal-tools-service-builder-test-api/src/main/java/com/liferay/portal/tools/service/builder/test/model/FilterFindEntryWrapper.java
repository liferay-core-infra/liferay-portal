/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link FilterFindEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FilterFindEntry
 * @generated
 */
public class FilterFindEntryWrapper
	extends BaseModelWrapper<FilterFindEntry>
	implements FilterFindEntry, ModelWrapper<FilterFindEntry> {

	public FilterFindEntryWrapper(FilterFindEntry filterFindEntry) {
		super(filterFindEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("filterFindEntryId", getFilterFindEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("type", getType());
		attributes.put("integer", getInteger());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long filterFindEntryId = (Long)attributes.get("filterFindEntryId");

		if (filterFindEntryId != null) {
			setFilterFindEntryId(filterFindEntryId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Integer integer = (Integer)attributes.get("integer");

		if (integer != null) {
			setInteger(integer);
		}
	}

	@Override
	public FilterFindEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the filter find entry ID of this filter find entry.
	 *
	 * @return the filter find entry ID of this filter find entry
	 */
	@Override
	public long getFilterFindEntryId() {
		return model.getFilterFindEntryId();
	}

	/**
	 * Returns the group ID of this filter find entry.
	 *
	 * @return the group ID of this filter find entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the integer of this filter find entry.
	 *
	 * @return the integer of this filter find entry
	 */
	@Override
	public int getInteger() {
		return model.getInteger();
	}

	/**
	 * Returns the primary key of this filter find entry.
	 *
	 * @return the primary key of this filter find entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the type of this filter find entry.
	 *
	 * @return the type of this filter find entry
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the filter find entry ID of this filter find entry.
	 *
	 * @param filterFindEntryId the filter find entry ID of this filter find entry
	 */
	@Override
	public void setFilterFindEntryId(long filterFindEntryId) {
		model.setFilterFindEntryId(filterFindEntryId);
	}

	/**
	 * Sets the group ID of this filter find entry.
	 *
	 * @param groupId the group ID of this filter find entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the integer of this filter find entry.
	 *
	 * @param integer the integer of this filter find entry
	 */
	@Override
	public void setInteger(int integer) {
		model.setInteger(integer);
	}

	/**
	 * Sets the primary key of this filter find entry.
	 *
	 * @param primaryKey the primary key of this filter find entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the type of this filter find entry.
	 *
	 * @param type the type of this filter find entry
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected FilterFindEntryWrapper wrap(FilterFindEntry filterFindEntry) {
		return new FilterFindEntryWrapper(filterFindEntry);
	}

}