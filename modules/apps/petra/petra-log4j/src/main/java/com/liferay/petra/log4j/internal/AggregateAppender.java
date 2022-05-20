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

package com.liferay.petra.log4j.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;

/**
 * @author Hai Yu
 */
public class AggregateAppender extends AbstractAppender {

	public AggregateAppender() {
		super("AggregateAppender", null, null, true, null);

		start();
	}

	@Override
	public void append(LogEvent logEvent) {
		String prefix = CompanyThreadLocal.getCompanyId() + "-";

		RollingFileAppender textFileRollingFileAppender =
			_companyAppenders.computeIfAbsent(
				prefix + _TEXT_FILE,
				key -> _createFileAppender(_getAppender(_TEXT_FILE), key));

		if (textFileRollingFileAppender != null) {
			textFileRollingFileAppender.append(logEvent);
		}

		RollingFileAppender xmFileRollingFileAppender =
			_companyAppenders.computeIfAbsent(
				prefix + _XML_FILE,
				key -> _createFileAppender(_getAppender(_XML_FILE), key));

		if (xmFileRollingFileAppender != null) {
			xmFileRollingFileAppender.append(logEvent);
		}
	}

	private RollingFileAppender _createFileAppender(
		Appender appender, String appendName) {

		if (appender == null) {
			return null;
		}

		long companyId = CompanyThreadLocal.getCompanyId();

		RollingFileAppender portalRollingFileAppender =
			(RollingFileAppender)appender;

		String testFilePattern = StringBundler.concat(
			StringUtil.replace(
				PropsUtil.get(PropsKeys.LIFERAY_HOME), '\\', '/'),
			"/logs/companies/", companyId, StringPool.SLASH,
			StringUtil.extractLast(
				portalRollingFileAppender.getFilePattern(), StringPool.SLASH));

		LoggerContext loggerContext = (LoggerContext)LogManager.getContext();

		RollingFileAppender rollingFileAppender =
			RollingFileAppender.createAppender(
				null, testFilePattern, Boolean.TRUE.toString(), appendName,
				Boolean.TRUE.toString(), String.valueOf(_BUFFER_SIZE),
				Boolean.TRUE.toString(),
				portalRollingFileAppender.getTriggeringPolicy(), null,
				portalRollingFileAppender.getLayout(), null,
				Boolean.FALSE.toString(), null, null,
				loggerContext.getConfiguration());

		rollingFileAppender.start();

		return rollingFileAppender;
	}

	private Appender _getAppender(String appendName) {
		Logger rootLogger = (Logger)LogManager.getRootLogger();

		Map<String, Appender> appenders = rootLogger.getAppenders();

		for (Appender appender : appenders.values()) {
			if ((appender instanceof RollingFileAppender) &&
				Objects.equals(appendName, appender.getName())) {

				return appender;
			}
		}

		return null;
	}

	private static final int _BUFFER_SIZE = 8192;

	private static final String _TEXT_FILE = "TEXT_FILE";

	private static final String _XML_FILE = "XML_FILE";

	private final Map<String, RollingFileAppender> _companyAppenders =
		new HashMap<>();

}