/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing BadColumnNameEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class BadColumnNameEntryCacheModel
	implements CacheModel<BadColumnNameEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BadColumnNameEntryCacheModel)) {
			return false;
		}

		BadColumnNameEntryCacheModel badColumnNameEntryCacheModel =
			(BadColumnNameEntryCacheModel)object;

		if (badColumnNameEntryId ==
				badColumnNameEntryCacheModel.badColumnNameEntryId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, badColumnNameEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(5);

		sb.append("{badColumnNameEntryId=");
		sb.append(badColumnNameEntryId);
		sb.append(", type=");
		sb.append(type);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public BadColumnNameEntry toEntityModel() {
		BadColumnNameEntryImpl badColumnNameEntryImpl =
			new BadColumnNameEntryImpl();

		badColumnNameEntryImpl.setBadColumnNameEntryId(badColumnNameEntryId);

		if (type == null) {
			badColumnNameEntryImpl.setType("");
		}
		else {
			badColumnNameEntryImpl.setType(type);
		}

		badColumnNameEntryImpl.resetOriginalValues();

		return badColumnNameEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		badColumnNameEntryId = objectInput.readLong();
		type = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(badColumnNameEntryId);

		if (type == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(type);
		}
	}

	public long badColumnNameEntryId;
	public String type;

}