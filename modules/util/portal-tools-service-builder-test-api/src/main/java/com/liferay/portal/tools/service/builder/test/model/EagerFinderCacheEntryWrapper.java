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
 * This class is a wrapper for {@link EagerFinderCacheEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see EagerFinderCacheEntry
 * @generated
 */
public class EagerFinderCacheEntryWrapper
	extends BaseModelWrapper<EagerFinderCacheEntry>
	implements EagerFinderCacheEntry, ModelWrapper<EagerFinderCacheEntry> {

	public EagerFinderCacheEntryWrapper(
		EagerFinderCacheEntry eagerFinderCacheEntry) {

		super(eagerFinderCacheEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("eagerFinderCacheEntryId", getEagerFinderCacheEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("uniqueName", getUniqueName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long eagerFinderCacheEntryId = (Long)attributes.get(
			"eagerFinderCacheEntryId");

		if (eagerFinderCacheEntryId != null) {
			setEagerFinderCacheEntryId(eagerFinderCacheEntryId);
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
	public EagerFinderCacheEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this eager finder cache entry.
	 *
	 * @return the company ID of this eager finder cache entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the eager finder cache entry ID of this eager finder cache entry.
	 *
	 * @return the eager finder cache entry ID of this eager finder cache entry
	 */
	@Override
	public long getEagerFinderCacheEntryId() {
		return model.getEagerFinderCacheEntryId();
	}

	/**
	 * Returns the group ID of this eager finder cache entry.
	 *
	 * @return the group ID of this eager finder cache entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the primary key of this eager finder cache entry.
	 *
	 * @return the primary key of this eager finder cache entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the unique name of this eager finder cache entry.
	 *
	 * @return the unique name of this eager finder cache entry
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
	 * Sets the company ID of this eager finder cache entry.
	 *
	 * @param companyId the company ID of this eager finder cache entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the eager finder cache entry ID of this eager finder cache entry.
	 *
	 * @param eagerFinderCacheEntryId the eager finder cache entry ID of this eager finder cache entry
	 */
	@Override
	public void setEagerFinderCacheEntryId(long eagerFinderCacheEntryId) {
		model.setEagerFinderCacheEntryId(eagerFinderCacheEntryId);
	}

	/**
	 * Sets the group ID of this eager finder cache entry.
	 *
	 * @param groupId the group ID of this eager finder cache entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the primary key of this eager finder cache entry.
	 *
	 * @param primaryKey the primary key of this eager finder cache entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the unique name of this eager finder cache entry.
	 *
	 * @param uniqueName the unique name of this eager finder cache entry
	 */
	@Override
	public void setUniqueName(String uniqueName) {
		model.setUniqueName(uniqueName);
	}

	@Override
	protected EagerFinderCacheEntryWrapper wrap(
		EagerFinderCacheEntry eagerFinderCacheEntry) {

		return new EagerFinderCacheEntryWrapper(eagerFinderCacheEntry);
	}

}