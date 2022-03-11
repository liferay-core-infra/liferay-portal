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

package com.liferay.info.internal.formatter;

import com.liferay.info.formatter.InfoTextFormatter;
import com.liferay.portal.kernel.util.FastDateFormatFactory;

import java.text.Format;

import java.util.Date;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = InfoTextFormatter.class)
public class DateInfoTextFormatter implements InfoTextFormatter<Date> {

	@Override
	public String format(Date date, Locale locale) {
		Format dateFormatDateTime = _fastDateFormatFactory.getDateTime(locale);

		return dateFormatDateTime.format(date);
	}

	@Reference
	private FastDateFormatFactory _fastDateFormatFactory;

}