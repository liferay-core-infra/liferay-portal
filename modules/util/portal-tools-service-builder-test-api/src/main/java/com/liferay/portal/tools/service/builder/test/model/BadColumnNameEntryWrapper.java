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
 * This class is a wrapper for {@link BadColumnNameEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see BadColumnNameEntry
 * @generated
 */
public class BadColumnNameEntryWrapper
	extends BaseModelWrapper<BadColumnNameEntry>
	implements BadColumnNameEntry, ModelWrapper<BadColumnNameEntry> {

	public BadColumnNameEntryWrapper(BadColumnNameEntry badColumnNameEntry) {
		super(badColumnNameEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("badColumnNameEntryId", getBadColumnNameEntryId());
		attributes.put("type", getType());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long badColumnNameEntryId = (Long)attributes.get(
			"badColumnNameEntryId");

		if (badColumnNameEntryId != null) {
			setBadColumnNameEntryId(badColumnNameEntryId);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}
	}

	@Override
	public BadColumnNameEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the bad column name entry ID of this bad column name entry.
	 *
	 * @return the bad column name entry ID of this bad column name entry
	 */
	@Override
	public long getBadColumnNameEntryId() {
		return model.getBadColumnNameEntryId();
	}

	/**
	 * Returns the primary key of this bad column name entry.
	 *
	 * @return the primary key of this bad column name entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the type of this bad column name entry.
	 *
	 * @return the type of this bad column name entry
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
	 * Sets the bad column name entry ID of this bad column name entry.
	 *
	 * @param badColumnNameEntryId the bad column name entry ID of this bad column name entry
	 */
	@Override
	public void setBadColumnNameEntryId(long badColumnNameEntryId) {
		model.setBadColumnNameEntryId(badColumnNameEntryId);
	}

	/**
	 * Sets the primary key of this bad column name entry.
	 *
	 * @param primaryKey the primary key of this bad column name entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the type of this bad column name entry.
	 *
	 * @param type the type of this bad column name entry
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
	protected BadColumnNameEntryWrapper wrap(
		BadColumnNameEntry badColumnNameEntry) {

		return new BadColumnNameEntryWrapper(badColumnNameEntry);
	}

}