/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link SortedFinderEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SortedFinderEntry
 * @generated
 */
public class SortedFinderEntryWrapper
	extends BaseModelWrapper<SortedFinderEntry>
	implements ModelWrapper<SortedFinderEntry>, SortedFinderEntry {

	public SortedFinderEntryWrapper(SortedFinderEntry sortedFinderEntry) {
		super(sortedFinderEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("sortedFinderEntryId", getSortedFinderEntryId());
		attributes.put("name", getName());
		attributes.put("groupId", getGroupId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long sortedFinderEntryId = (Long)attributes.get("sortedFinderEntryId");

		if (sortedFinderEntryId != null) {
			setSortedFinderEntryId(sortedFinderEntryId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}
	}

	@Override
	public SortedFinderEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the group ID of this sorted finder entry.
	 *
	 * @return the group ID of this sorted finder entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the name of this sorted finder entry.
	 *
	 * @return the name of this sorted finder entry
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this sorted finder entry.
	 *
	 * @return the primary key of this sorted finder entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the sorted finder entry ID of this sorted finder entry.
	 *
	 * @return the sorted finder entry ID of this sorted finder entry
	 */
	@Override
	public long getSortedFinderEntryId() {
		return model.getSortedFinderEntryId();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the group ID of this sorted finder entry.
	 *
	 * @param groupId the group ID of this sorted finder entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the name of this sorted finder entry.
	 *
	 * @param name the name of this sorted finder entry
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this sorted finder entry.
	 *
	 * @param primaryKey the primary key of this sorted finder entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the sorted finder entry ID of this sorted finder entry.
	 *
	 * @param sortedFinderEntryId the sorted finder entry ID of this sorted finder entry
	 */
	@Override
	public void setSortedFinderEntryId(long sortedFinderEntryId) {
		model.setSortedFinderEntryId(sortedFinderEntryId);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected SortedFinderEntryWrapper wrap(
		SortedFinderEntry sortedFinderEntry) {

		return new SortedFinderEntryWrapper(sortedFinderEntry);
	}

}