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

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.SequenceEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing SequenceEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SequenceEntryCacheModel
	implements CacheModel<SequenceEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SequenceEntryCacheModel)) {
			return false;
		}

		SequenceEntryCacheModel sequenceEntryCacheModel =
			(SequenceEntryCacheModel)object;

		if (sequenceEntryId == sequenceEntryCacheModel.sequenceEntryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, sequenceEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(5);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", sequenceEntryId=");
		sb.append(sequenceEntryId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public SequenceEntry toEntityModel() {
		SequenceEntryImpl sequenceEntryImpl = new SequenceEntryImpl();

		if (uuid == null) {
			sequenceEntryImpl.setUuid("");
		}
		else {
			sequenceEntryImpl.setUuid(uuid);
		}

		sequenceEntryImpl.setSequenceEntryId(sequenceEntryId);

		sequenceEntryImpl.resetOriginalValues();

		return sequenceEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uuid = objectInput.readUTF();

		sequenceEntryId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		if (uuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		objectOutput.writeLong(sequenceEntryId);
	}

	public String uuid;
	public long sequenceEntryId;

}