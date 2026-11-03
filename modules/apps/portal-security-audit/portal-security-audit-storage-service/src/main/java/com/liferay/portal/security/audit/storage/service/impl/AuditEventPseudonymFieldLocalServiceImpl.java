/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.constants.AuditEventConstants;
import com.liferay.portal.security.audit.storage.exception.AuditEventPseudonymFieldValueException;
import com.liferay.portal.security.audit.storage.model.AuditEventPseudonymField;
import com.liferay.portal.security.audit.storage.service.base.AuditEventPseudonymFieldLocalServiceBaseImpl;

import java.util.Date;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.audit.storage.model.AuditEventPseudonymField",
	service = AopService.class
)
@CTAware
public class AuditEventPseudonymFieldLocalServiceImpl
	extends AuditEventPseudonymFieldLocalServiceBaseImpl {

	@Override
	public AuditEventPseudonymField addAuditEventPseudonymField(
			long companyId, String contextName, String name, String value)
		throws PortalException {

		if (Validator.isBlank(value)) {
			throw new AuditEventPseudonymFieldValueException("Value is blank");
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			AuditEventPseudonymField.class.getName(), "value");

		if (value.length() > maxLength) {
			throw new AuditEventPseudonymFieldValueException(
				"Maximum length of value exceeded");
		}

		if (Validator.isBlank(contextName)) {
			contextName = AuditEventConstants.CONTEXT_NAME_INSTANCE;
		}

		String valueHash = DigesterUtil.digestHex(DigesterUtil.SHA_256, value);

		AuditEventPseudonymField auditEventPseudonymField =
			auditEventPseudonymFieldPersistence.fetchByC_CN_N_VH(
				companyId, contextName, name, valueHash);

		if (auditEventPseudonymField != null) {
			return auditEventPseudonymField;
		}

		long auditEventPseudonymFieldId = counterLocalService.increment();

		auditEventPseudonymField = auditEventPseudonymFieldPersistence.create(
			auditEventPseudonymFieldId);

		auditEventPseudonymField.setCompanyId(companyId);
		auditEventPseudonymField.setCreateDate(new Date());
		auditEventPseudonymField.setContextName(contextName);
		auditEventPseudonymField.setName(name);
		auditEventPseudonymField.setValue(value);
		auditEventPseudonymField.setValueHash(valueHash);

		return auditEventPseudonymFieldPersistence.update(
			auditEventPseudonymField);
	}

	@Override
	public AuditMessage pseudonymizeAuditMessage(AuditMessage auditMessage) {
		AuditMessage pseudonymizedAuditMessage = new AuditMessage(auditMessage);

		try {
			String clientIP = auditMessage.getClientIP();

			String clientIPPseudonym = _pseudonymize(
				auditMessage, "clientIP", clientIP);

			String clientHost = auditMessage.getClientHost();

			if (!Validator.isBlank(clientHost)) {
				if (clientHost.equals(clientIP)) {
					pseudonymizedAuditMessage.setClientHost(clientIPPseudonym);
				}
				else {
					pseudonymizedAuditMessage.setClientHost(StringPool.BLANK);
				}
			}

			pseudonymizedAuditMessage.setClientIP(clientIPPseudonym);
			pseudonymizedAuditMessage.setImpersonatedUserEmailAddress(
				_pseudonymize(
					auditMessage, "userEmailAddress",
					auditMessage.getImpersonatedUserEmailAddress()));
			pseudonymizedAuditMessage.setImpersonatedUserName(
				_pseudonymize(
					auditMessage, "userName",
					auditMessage.getImpersonatedUserName()));
			pseudonymizedAuditMessage.setObjectName(
				_pseudonymize(
					auditMessage, "objectName", auditMessage.getObjectName()));

			String userEmailAddress = auditMessage.getUserEmailAddress();

			String userEmailAddressPseudonym = _pseudonymize(
				auditMessage, "userEmailAddress", userEmailAddress);

			pseudonymizedAuditMessage.setUserEmailAddress(
				userEmailAddressPseudonym);

			String userLogin = auditMessage.getUserLogin();

			if (!Validator.isBlank(userLogin)) {
				if (userLogin.equals(userEmailAddress)) {
					pseudonymizedAuditMessage.setUserLogin(
						userEmailAddressPseudonym);
				}
				else if (!userLogin.equals(
							String.valueOf(auditMessage.getUserId()))) {

					pseudonymizedAuditMessage.setUserLogin(StringPool.BLANK);
				}
			}

			pseudonymizedAuditMessage.setUserName(
				_pseudonymize(
					auditMessage, "userName", auditMessage.getUserName()));
		}
		catch (Throwable throwable) {
			_log.error("Unable to pseudonymize the audit message", throwable);

			pseudonymizedAuditMessage.setClientHost(StringPool.BLANK);
			pseudonymizedAuditMessage.setClientIP(StringPool.BLANK);
			pseudonymizedAuditMessage.setImpersonatedUserEmailAddress(
				StringPool.BLANK);
			pseudonymizedAuditMessage.setImpersonatedUserName(StringPool.BLANK);
			pseudonymizedAuditMessage.setObjectName(StringPool.BLANK);
			pseudonymizedAuditMessage.setUserEmailAddress(StringPool.BLANK);
			pseudonymizedAuditMessage.setUserLogin(StringPool.BLANK);
			pseudonymizedAuditMessage.setUserName(StringPool.BLANK);
		}

		pseudonymizedAuditMessage.setPseudonymized(true);

		return pseudonymizedAuditMessage;
	}

	private String _pseudonymize(
			AuditMessage auditMessage, String name, String value)
		throws Throwable {

		if (Validator.isBlank(value)) {
			return value;
		}

		AuditEventPseudonymField auditEventPseudonymField = null;

		Callable<AuditEventPseudonymField> callable =
			() -> addAuditEventPseudonymField(
				auditMessage.getCompanyId(), auditMessage.getContextName(),
				name, value);

		try {
			auditEventPseudonymField = TransactionInvokerUtil.invoke(
				_transactionConfig, callable);
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Retrying the pseudonym allocation after a concurrent " +
						"insert",
					throwable);
			}

			auditEventPseudonymField = TransactionInvokerUtil.invoke(
				_transactionConfig, callable);
		}

		return String.valueOf(
			auditEventPseudonymField.getAuditEventPseudonymFieldId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuditEventPseudonymFieldLocalServiceImpl.class);

	private final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

}