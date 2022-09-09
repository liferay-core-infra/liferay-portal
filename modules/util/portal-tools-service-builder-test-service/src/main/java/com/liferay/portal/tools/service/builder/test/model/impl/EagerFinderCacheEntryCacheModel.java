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
import com.liferay.portal.tools.service.builder.test.model.EagerFinderCacheEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing EagerFinderCacheEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class EagerFinderCacheEntryCacheModel
	implements CacheModel<EagerFinderCacheEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EagerFinderCacheEntryCacheModel)) {
			return false;
		}

		EagerFinderCacheEntryCacheModel eagerFinderCacheEntryCacheModel =
			(EagerFinderCacheEntryCacheModel)object;

		if (eagerFinderCacheEntryId ==
				eagerFinderCacheEntryCacheModel.eagerFinderCacheEntryId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, eagerFinderCacheEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(9);

		sb.append("{eagerFinderCacheEntryId=");
		sb.append(eagerFinderCacheEntryId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", uniqueName=");
		sb.append(uniqueName);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public EagerFinderCacheEntry toEntityModel() {
		EagerFinderCacheEntryImpl eagerFinderCacheEntryImpl =
			new EagerFinderCacheEntryImpl();

		eagerFinderCacheEntryImpl.setEagerFinderCacheEntryId(
			eagerFinderCacheEntryId);
		eagerFinderCacheEntryImpl.setGroupId(groupId);
		eagerFinderCacheEntryImpl.setCompanyId(companyId);

		if (uniqueName == null) {
			eagerFinderCacheEntryImpl.setUniqueName("");
		}
		else {
			eagerFinderCacheEntryImpl.setUniqueName(uniqueName);
		}

		eagerFinderCacheEntryImpl.resetOriginalValues();

		return eagerFinderCacheEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		eagerFinderCacheEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();
		uniqueName = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(eagerFinderCacheEntryId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		if (uniqueName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uniqueName);
		}
	}

	public long eagerFinderCacheEntryId;
	public long groupId;
	public long companyId;
	public String uniqueName;

}