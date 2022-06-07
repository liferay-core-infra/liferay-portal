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

package com.liferay.portal.mail.sender.internal.messaging;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.util.HookFactory;
import com.liferay.petra.mail.MailEngine;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.security.auth.EmailAddressGenerator;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.mail.sender.internal.constants.MailSenderMessagesDestinationNames;
import com.liferay.portal.security.auth.EmailAddressGeneratorFactory;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 * @author Julius Lee
 */
@Component(
	immediate = true,
	property = "destination.name=" + MailSenderMessagesDestinationNames.MAIL_SENDER_MESSAGES_PROCESSOR,
	service = MessageListener.class
)
public class MailSenderMessageListener extends BaseMessageListener {

	@Override
	protected void doReceive(Message message) throws Exception {
		Object payload = message.getPayload();

		if (payload instanceof MailMessage) {
			_processMailMessage((MailMessage)payload);
		}
		else if (payload instanceof MethodHandler) {
			_processMethodHandler((MethodHandler)payload);
		}
	}

	private InternetAddress _filterInternetAddress(
		InternetAddress internetAddress) {

		if (PortalRunMode.isTestMode()) {
			return internetAddress;
		}

		EmailAddressGenerator emailAddressGenerator =
			EmailAddressGeneratorFactory.getInstance();

		String emailAddress = internetAddress.getAddress();

		if (emailAddressGenerator.isFake(emailAddress)) {
			return null;
		}

		if (_mailSendBlacklist.contains(emailAddress)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Email ", emailAddress, " will be ignored because it ",
						"is included in ", PropsKeys.MAIL_SEND_BLACKLIST));
			}

			return null;
		}

		return internetAddress;
	}

	private InternetAddress[] _filterInternetAddresses(
		InternetAddress[] internetAddresses) {

		if (internetAddresses == null) {
			return null;
		}

		List<InternetAddress> filteredInternetAddresses = new ArrayList<>(
			internetAddresses.length);

		for (InternetAddress internetAddress : internetAddresses) {
			InternetAddress filteredInternetAddress = _filterInternetAddress(
				internetAddress);

			if (filteredInternetAddress != null) {
				filteredInternetAddresses.add(filteredInternetAddress);
			}
		}

		return filteredInternetAddresses.toArray(new InternetAddress[0]);
	}

	private void _processMailMessage(MailMessage mailMessage) throws Exception {
		InternetAddress from = _filterInternetAddress(mailMessage.getFrom());

		if (from == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Skipping email because the sender is not specified");
			}

			return;
		}

		mailMessage.setFrom(from);

		InternetAddress[] to = _filterInternetAddresses(mailMessage.getTo());

		mailMessage.setTo(to);

		InternetAddress[] cc = _filterInternetAddresses(mailMessage.getCC());

		mailMessage.setCC(cc);

		InternetAddress[] bcc = _filterInternetAddresses(mailMessage.getBCC());

		InternetAddress[] auditTrail = InternetAddress.parse(
			PropsValues.MAIL_AUDIT_TRAIL);

		if (auditTrail.length > 0) {
			if (ArrayUtil.isNotEmpty(bcc)) {
				for (InternetAddress internetAddress : auditTrail) {
					bcc = ArrayUtil.append(bcc, internetAddress);
				}
			}
			else {
				bcc = auditTrail;
			}
		}

		mailMessage.setBCC(bcc);

		InternetAddress[] bulkAddresses = _filterInternetAddresses(
			mailMessage.getBulkAddresses());

		mailMessage.setBulkAddresses(bulkAddresses);

		InternetAddress[] replyTo = _filterInternetAddresses(
			mailMessage.getReplyTo());

		mailMessage.setReplyTo(replyTo);

		if (ArrayUtil.isNotEmpty(to) || ArrayUtil.isNotEmpty(cc) ||
			ArrayUtil.isNotEmpty(bcc) || ArrayUtil.isNotEmpty(bulkAddresses)) {

			MailEngine.send(mailMessage);
		}
	}

	private void _processMethodHandler(MethodHandler methodHandler)
		throws Exception {

		methodHandler.invoke(HookFactory.getInstance());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MailSenderMessageListener.class);

	private static final Set<String> _mailSendBlacklist = new HashSet<>(
		Arrays.asList(PropsValues.MAIL_SEND_BLACKLIST));

}