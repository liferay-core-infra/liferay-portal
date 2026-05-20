/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.production.readiness.ignore.model.IgnoredRule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing IgnoredRule in entity cache.
 *
 * @author Lily Chi
 * @generated
 */
public class IgnoredRuleCacheModel
	implements CacheModel<IgnoredRule>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IgnoredRuleCacheModel)) {
			return false;
		}

		IgnoredRuleCacheModel ignoredRuleCacheModel =
			(IgnoredRuleCacheModel)object;

		if ((ignoredRuleId == ignoredRuleCacheModel.ignoredRuleId) &&
			(mvccVersion == ignoredRuleCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, ignoredRuleId);

		return HashUtil.hash(hashCode, mvccVersion);
	}

	@Override
	public long getMvccVersion() {
		return mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		this.mvccVersion = mvccVersion;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(19);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ignoredRuleId=");
		sb.append(ignoredRuleId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", ruleKey=");
		sb.append(ruleKey);
		sb.append(", reason=");
		sb.append(reason);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public IgnoredRule toEntityModel() {
		IgnoredRuleImpl ignoredRuleImpl = new IgnoredRuleImpl();

		ignoredRuleImpl.setMvccVersion(mvccVersion);
		ignoredRuleImpl.setIgnoredRuleId(ignoredRuleId);
		ignoredRuleImpl.setCompanyId(companyId);
		ignoredRuleImpl.setUserId(userId);

		if (userName == null) {
			ignoredRuleImpl.setUserName("");
		}
		else {
			ignoredRuleImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			ignoredRuleImpl.setCreateDate(null);
		}
		else {
			ignoredRuleImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			ignoredRuleImpl.setModifiedDate(null);
		}
		else {
			ignoredRuleImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (ruleKey == null) {
			ignoredRuleImpl.setRuleKey("");
		}
		else {
			ignoredRuleImpl.setRuleKey(ruleKey);
		}

		if (reason == null) {
			ignoredRuleImpl.setReason("");
		}
		else {
			ignoredRuleImpl.setReason(reason);
		}

		ignoredRuleImpl.resetOriginalValues();

		return ignoredRuleImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ignoredRuleId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		ruleKey = objectInput.readUTF();
		reason = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ignoredRuleId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		if (ruleKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(ruleKey);
		}

		if (reason == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(reason);
		}
	}

	public long mvccVersion;
	public long ignoredRuleId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String ruleKey;
	public String reason;

}
// LIFERAY-SERVICE-BUILDER-HASH:1443555811