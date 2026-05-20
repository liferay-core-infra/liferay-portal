/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link IgnoredRule}.
 * </p>
 *
 * @author Lily Chi
 * @see IgnoredRule
 * @generated
 */
public class IgnoredRuleWrapper
	extends BaseModelWrapper<IgnoredRule>
	implements IgnoredRule, ModelWrapper<IgnoredRule> {

	public IgnoredRuleWrapper(IgnoredRule ignoredRule) {
		super(ignoredRule);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ignoredRuleId", getIgnoredRuleId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("ruleKey", getRuleKey());
		attributes.put("reason", getReason());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ignoredRuleId = (Long)attributes.get("ignoredRuleId");

		if (ignoredRuleId != null) {
			setIgnoredRuleId(ignoredRuleId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String ruleKey = (String)attributes.get("ruleKey");

		if (ruleKey != null) {
			setRuleKey(ruleKey);
		}

		String reason = (String)attributes.get("reason");

		if (reason != null) {
			setReason(reason);
		}
	}

	@Override
	public IgnoredRule cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this ignored rule.
	 *
	 * @return the company ID of this ignored rule
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this ignored rule.
	 *
	 * @return the create date of this ignored rule
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ignored rule ID of this ignored rule.
	 *
	 * @return the ignored rule ID of this ignored rule
	 */
	@Override
	public long getIgnoredRuleId() {
		return model.getIgnoredRuleId();
	}

	/**
	 * Returns the modified date of this ignored rule.
	 *
	 * @return the modified date of this ignored rule
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this ignored rule.
	 *
	 * @return the mvcc version of this ignored rule
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this ignored rule.
	 *
	 * @return the primary key of this ignored rule
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the reason of this ignored rule.
	 *
	 * @return the reason of this ignored rule
	 */
	@Override
	public String getReason() {
		return model.getReason();
	}

	/**
	 * Returns the rule key of this ignored rule.
	 *
	 * @return the rule key of this ignored rule
	 */
	@Override
	public String getRuleKey() {
		return model.getRuleKey();
	}

	/**
	 * Returns the user ID of this ignored rule.
	 *
	 * @return the user ID of this ignored rule
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this ignored rule.
	 *
	 * @return the user name of this ignored rule
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this ignored rule.
	 *
	 * @return the user uuid of this ignored rule
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this ignored rule.
	 *
	 * @param companyId the company ID of this ignored rule
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this ignored rule.
	 *
	 * @param createDate the create date of this ignored rule
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ignored rule ID of this ignored rule.
	 *
	 * @param ignoredRuleId the ignored rule ID of this ignored rule
	 */
	@Override
	public void setIgnoredRuleId(long ignoredRuleId) {
		model.setIgnoredRuleId(ignoredRuleId);
	}

	/**
	 * Sets the modified date of this ignored rule.
	 *
	 * @param modifiedDate the modified date of this ignored rule
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this ignored rule.
	 *
	 * @param mvccVersion the mvcc version of this ignored rule
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this ignored rule.
	 *
	 * @param primaryKey the primary key of this ignored rule
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the reason of this ignored rule.
	 *
	 * @param reason the reason of this ignored rule
	 */
	@Override
	public void setReason(String reason) {
		model.setReason(reason);
	}

	/**
	 * Sets the rule key of this ignored rule.
	 *
	 * @param ruleKey the rule key of this ignored rule
	 */
	@Override
	public void setRuleKey(String ruleKey) {
		model.setRuleKey(ruleKey);
	}

	/**
	 * Sets the user ID of this ignored rule.
	 *
	 * @param userId the user ID of this ignored rule
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this ignored rule.
	 *
	 * @param userName the user name of this ignored rule
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this ignored rule.
	 *
	 * @param userUuid the user uuid of this ignored rule
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected IgnoredRuleWrapper wrap(IgnoredRule ignoredRule) {
		return new IgnoredRuleWrapper(ignoredRule);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1565719686