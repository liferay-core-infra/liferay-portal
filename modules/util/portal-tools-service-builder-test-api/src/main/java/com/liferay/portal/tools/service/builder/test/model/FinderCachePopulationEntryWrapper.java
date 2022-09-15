/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link FinderCachePopulationEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FinderCachePopulationEntry
 * @generated
 */
public class FinderCachePopulationEntryWrapper
	extends BaseModelWrapper<FinderCachePopulationEntry>
	implements FinderCachePopulationEntry,
			   ModelWrapper<FinderCachePopulationEntry> {

	public FinderCachePopulationEntryWrapper(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		super(finderCachePopulationEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put(
			"pinderCachePopulationEntryId", getPinderCachePopulationEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("uniqueName", getUniqueName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long pinderCachePopulationEntryId = (Long)attributes.get(
			"pinderCachePopulationEntryId");

		if (pinderCachePopulationEntryId != null) {
			setPinderCachePopulationEntryId(pinderCachePopulationEntryId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		String uniqueName = (String)attributes.get("uniqueName");

		if (uniqueName != null) {
			setUniqueName(uniqueName);
		}
	}

	@Override
	public FinderCachePopulationEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this finder cache population entry.
	 *
	 * @return the company ID of this finder cache population entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the group ID of this finder cache population entry.
	 *
	 * @return the group ID of this finder cache population entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the pinder cache population entry ID of this finder cache population entry.
	 *
	 * @return the pinder cache population entry ID of this finder cache population entry
	 */
	@Override
	public long getPinderCachePopulationEntryId() {
		return model.getPinderCachePopulationEntryId();
	}

	/**
	 * Returns the primary key of this finder cache population entry.
	 *
	 * @return the primary key of this finder cache population entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the unique name of this finder cache population entry.
	 *
	 * @return the unique name of this finder cache population entry
	 */
	@Override
	public String getUniqueName() {
		return model.getUniqueName();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this finder cache population entry.
	 *
	 * @param companyId the company ID of this finder cache population entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the group ID of this finder cache population entry.
	 *
	 * @param groupId the group ID of this finder cache population entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the pinder cache population entry ID of this finder cache population entry.
	 *
	 * @param pinderCachePopulationEntryId the pinder cache population entry ID of this finder cache population entry
	 */
	@Override
	public void setPinderCachePopulationEntryId(
		long pinderCachePopulationEntryId) {

		model.setPinderCachePopulationEntryId(pinderCachePopulationEntryId);
	}

	/**
	 * Sets the primary key of this finder cache population entry.
	 *
	 * @param primaryKey the primary key of this finder cache population entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the unique name of this finder cache population entry.
	 *
	 * @param uniqueName the unique name of this finder cache population entry
	 */
	@Override
	public void setUniqueName(String uniqueName) {
		model.setUniqueName(uniqueName);
	}

	@Override
	protected FinderCachePopulationEntryWrapper wrap(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		return new FinderCachePopulationEntryWrapper(
			finderCachePopulationEntry);
	}

}