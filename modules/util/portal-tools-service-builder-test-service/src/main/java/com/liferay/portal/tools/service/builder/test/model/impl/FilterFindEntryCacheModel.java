/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.FilterFindEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing FilterFindEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class FilterFindEntryCacheModel
	implements CacheModel<FilterFindEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FilterFindEntryCacheModel)) {
			return false;
		}

		FilterFindEntryCacheModel filterFindEntryCacheModel =
			(FilterFindEntryCacheModel)object;

		if (filterFindEntryId == filterFindEntryCacheModel.filterFindEntryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, filterFindEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(9);

		sb.append("{filterFindEntryId=");
		sb.append(filterFindEntryId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", type=");
		sb.append(type);
		sb.append(", integer=");
		sb.append(integer);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public FilterFindEntry toEntityModel() {
		FilterFindEntryImpl filterFindEntryImpl = new FilterFindEntryImpl();

		filterFindEntryImpl.setFilterFindEntryId(filterFindEntryId);
		filterFindEntryImpl.setGroupId(groupId);

		if (type == null) {
			filterFindEntryImpl.setType("");
		}
		else {
			filterFindEntryImpl.setType(type);
		}

		filterFindEntryImpl.setInteger(integer);

		filterFindEntryImpl.resetOriginalValues();

		return filterFindEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		filterFindEntryId = objectInput.readLong();

		groupId = objectInput.readLong();
		type = objectInput.readUTF();

		integer = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(filterFindEntryId);

		objectOutput.writeLong(groupId);

		if (type == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(type);
		}

		objectOutput.writeInt(integer);
	}

	public long filterFindEntryId;
	public long groupId;
	public String type;
	public int integer;

}