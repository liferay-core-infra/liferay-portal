/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.production.readiness.ignore.model.IgnoredRule;
import com.liferay.production.readiness.ignore.service.base.IgnoredRuleLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(
	property = "model.class.name=com.liferay.production.readiness.ignore.model.IgnoredRule",
	service = AopService.class
)
public class IgnoredRuleLocalServiceImpl
	extends IgnoredRuleLocalServiceBaseImpl {

	public IgnoredRule addIgnoredRule(
			long userId, long companyId, String ruleKey, String reason)
		throws PortalException {

		_checkAdmin(userId, companyId);

		User user = _userLocalService.getUser(userId);

		Date now = new Date();

		IgnoredRule ignoredRule = ignoredRulePersistence.create(
			counterLocalService.increment());

		ignoredRule.setCompanyId(companyId);
		ignoredRule.setUserId(userId);
		ignoredRule.setUserName(user.getFullName());
		ignoredRule.setCreateDate(now);
		ignoredRule.setModifiedDate(now);
		ignoredRule.setRuleKey(ruleKey);
		ignoredRule.setReason(reason);

		return ignoredRulePersistence.update(ignoredRule);
	}

	public IgnoredRule deleteIgnoredRule(
			long userId, long companyId, String ruleKey)
		throws PortalException {

		_checkAdmin(userId, companyId);

		return ignoredRulePersistence.removeByC_R(companyId, ruleKey);
	}

	public IgnoredRule fetchIgnoredRule(long companyId, String ruleKey) {
		return ignoredRulePersistence.fetchByC_R(companyId, ruleKey);
	}

	public List<IgnoredRule> getIgnoredRules(long companyId) {
		return ignoredRulePersistence.findByCompanyId(companyId);
	}

	private void _checkAdmin(long userId, long companyId)
		throws PortalException {

		if (!_roleLocalService.hasUserRole(
				userId, companyId, RoleConstants.ADMINISTRATOR, true)) {

			throw new PrincipalException.MustBeCompanyAdmin(userId);
		}
	}

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}