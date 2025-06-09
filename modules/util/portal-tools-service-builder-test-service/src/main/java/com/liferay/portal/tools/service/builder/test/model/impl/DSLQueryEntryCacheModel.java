/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.math.BigDecimal;

/**
 * The cache model class for representing DSLQueryEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DSLQueryEntryCacheModel
	implements CacheModel<DSLQueryEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DSLQueryEntryCacheModel)) {
			return false;
		}

		DSLQueryEntryCacheModel dslQueryEntryCacheModel =
			(DSLQueryEntryCacheModel)object;

		if (dslQueryEntryId == dslQueryEntryCacheModel.dslQueryEntryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, dslQueryEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(13);

		sb.append("{dslQueryEntryId=");
		sb.append(dslQueryEntryId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", bigDecimalValue=");
		sb.append(bigDecimalValue);
		sb.append(", doubleValue=");
		sb.append(doubleValue);
		sb.append(", intValue=");
		sb.append(intValue);
		sb.append(", longValue=");
		sb.append(longValue);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DSLQueryEntry toEntityModel() {
		DSLQueryEntryImpl dslQueryEntryImpl = new DSLQueryEntryImpl();

		dslQueryEntryImpl.setDslQueryEntryId(dslQueryEntryId);

		if (name == null) {
			dslQueryEntryImpl.setName("");
		}
		else {
			dslQueryEntryImpl.setName(name);
		}

		dslQueryEntryImpl.setBigDecimalValue(bigDecimalValue);
		dslQueryEntryImpl.setDoubleValue(doubleValue);
		dslQueryEntryImpl.setIntValue(intValue);
		dslQueryEntryImpl.setLongValue(longValue);

		dslQueryEntryImpl.resetOriginalValues();

		return dslQueryEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		dslQueryEntryId = objectInput.readLong();
		name = objectInput.readUTF();
		bigDecimalValue = (BigDecimal)objectInput.readObject();

		doubleValue = objectInput.readDouble();

		intValue = objectInput.readInt();

		longValue = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(dslQueryEntryId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeObject(bigDecimalValue);

		objectOutput.writeDouble(doubleValue);

		objectOutput.writeInt(intValue);

		objectOutput.writeLong(longValue);
	}

	public long dslQueryEntryId;
	public String name;
	public BigDecimal bigDecimalValue;
	public double doubleValue;
	public int intValue;
	public long longValue;

}