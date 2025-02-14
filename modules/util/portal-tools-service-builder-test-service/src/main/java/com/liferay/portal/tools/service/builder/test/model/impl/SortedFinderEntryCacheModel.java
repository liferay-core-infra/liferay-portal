/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing SortedFinderEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SortedFinderEntryCacheModel
	implements CacheModel<SortedFinderEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SortedFinderEntryCacheModel)) {
			return false;
		}

		SortedFinderEntryCacheModel sortedFinderEntryCacheModel =
			(SortedFinderEntryCacheModel)object;

		if (sortedFinderEntryId ==
				sortedFinderEntryCacheModel.sortedFinderEntryId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, sortedFinderEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(7);

		sb.append("{sortedFinderEntryId=");
		sb.append(sortedFinderEntryId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public SortedFinderEntry toEntityModel() {
		SortedFinderEntryImpl sortedFinderEntryImpl =
			new SortedFinderEntryImpl();

		sortedFinderEntryImpl.setSortedFinderEntryId(sortedFinderEntryId);

		if (name == null) {
			sortedFinderEntryImpl.setName("");
		}
		else {
			sortedFinderEntryImpl.setName(name);
		}

		sortedFinderEntryImpl.setGroupId(groupId);

		sortedFinderEntryImpl.resetOriginalValues();

		return sortedFinderEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		sortedFinderEntryId = objectInput.readLong();
		name = objectInput.readUTF();

		groupId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(sortedFinderEntryId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeLong(groupId);
	}

	public long sortedFinderEntryId;
	public String name;
	public long groupId;

}