/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.portal.kernel.format.PhoneNumberFormat;
import com.liferay.portal.kernel.util.ServiceProxyFactory;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Brian Wing Shun Chan
 * @author Manuel de la Peña
 * @author Peter Fellwock
 */
public class PhoneNumberFormatUtil {

	public static String format(String phoneNumber) {
		PhoneNumberFormat phoneNumberFormat = _phoneNumberFormat;

		if (phoneNumberFormat == null) {
			return phoneNumber;
		}

		return phoneNumberFormat.format(phoneNumber);
	}

	public static String strip(String phoneNumber) {
		PhoneNumberFormat phoneNumberFormat = _phoneNumberFormat;

		if (phoneNumberFormat == null) {
			return phoneNumber;
		}

		return phoneNumberFormat.strip(phoneNumber);
	}

	public static boolean validate(String phoneNumber) {
		PhoneNumberFormat phoneNumberFormat = _phoneNumberFormat;

		if (phoneNumberFormat != null) {
			return phoneNumberFormat.validate(phoneNumber);
		}

		if (Validator.isNull(phoneNumber)) {
			return false;
		}

		return true;
	}

	private PhoneNumberFormatUtil() {
	}

	private static volatile PhoneNumberFormat _phoneNumberFormat =
		ServiceProxyFactory.newServiceTrackedInstance(
			PhoneNumberFormat.class, PhoneNumberFormatUtil.class,
			"_phoneNumberFormat", false, true);

}