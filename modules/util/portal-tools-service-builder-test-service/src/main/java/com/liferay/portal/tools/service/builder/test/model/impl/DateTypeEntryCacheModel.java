/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.DateTypeEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing DateTypeEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DateTypeEntryCacheModel
	implements CacheModel<DateTypeEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DateTypeEntryCacheModel)) {
			return false;
		}

		DateTypeEntryCacheModel dateTypeEntryCacheModel =
			(DateTypeEntryCacheModel)object;

		if (dateTypeEntryId == dateTypeEntryCacheModel.dateTypeEntryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, dateTypeEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(5);

		sb.append("{dateTypeEntryId=");
		sb.append(dateTypeEntryId);
		sb.append(", dateValue=");
		sb.append(dateValue);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DateTypeEntry toEntityModel() {
		DateTypeEntryImpl dateTypeEntryImpl = new DateTypeEntryImpl();

		dateTypeEntryImpl.setDateTypeEntryId(dateTypeEntryId);

		if (dateValue == Long.MIN_VALUE) {
			dateTypeEntryImpl.setDateValue(null);
		}
		else {
			dateTypeEntryImpl.setDateValue(new Date(dateValue));
		}

		dateTypeEntryImpl.resetOriginalValues();

		return dateTypeEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		dateTypeEntryId = objectInput.readLong();
		dateValue = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(dateTypeEntryId);
		objectOutput.writeLong(dateValue);
	}

	public long dateTypeEntryId;
	public long dateValue;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1885484163