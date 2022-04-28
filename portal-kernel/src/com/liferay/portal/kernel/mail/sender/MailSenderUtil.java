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

package com.liferay.portal.kernel.mail.sender;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.portal.kernel.util.ServiceProxyFactory;

import javax.mail.Session;

/**
 * @author Julius Lee
 */
public class MailSenderUtil {

	public static void addUser(
		long companyId, long userId, String password, String firstName,
		String middleName, String lastName, String emailAddress) {

		_mailSender.addUser(
			companyId, userId, password, firstName, middleName, lastName,
			emailAddress);
	}

	public static void clearSession() {
		_mailSender.clearSession();
	}

	public static void deleteEmailAddress(long companyId, long userId) {
		_mailSender.deleteEmailAddress(companyId, userId);
	}

	public static void deleteUser(long companyId, long userId) {
		_mailSender.deleteUser(companyId, userId);
	}

	public static Session getSession() {
		return _mailSender.getSession();
	}

	public static void sendEmail(MailMessage mailMessage) {
		_mailSender.sendEmail(mailMessage);
	}

	public static void updateEmailAddress(
		long companyId, long userId, String emailAddress) {

		_mailSender.updateEmailAddress(companyId, userId, emailAddress);
	}

	public static void updatePassword(
		long companyId, long userId, String password) {

		_mailSender.updatePassword(companyId, userId, password);
	}

	private static volatile MailSender _mailSender =
		ServiceProxyFactory.newServiceTrackedInstance(
			MailSender.class, MailSenderUtil.class, "_mailSender", true);

}