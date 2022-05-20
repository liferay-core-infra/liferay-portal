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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;

/**
 * @author Hai Yu
 */
public class CompanyLogRoutingAppender extends AbstractAppender {

	public CompanyLogRoutingAppender() {
		super("CompanyLogRoutingAppender", null, null, true, null);

		start();
	}

	@Override
	public void append(LogEvent logEvent) {
		long companyId = CompanyThreadLocal.getCompanyId();

		Appender textFileAppender = _textFileAppenders.computeIfAbsent(
			companyId,
			key -> _createFileAppender(_portalTextFileAppender, key));

		if (textFileAppender != null) {
			textFileAppender.append(logEvent);
		}

		Appender xmlFileAppender = _xmlFileAppenders.computeIfAbsent(
			companyId, key -> _createFileAppender(_portalXmlFileAppender, key));

		if (xmlFileAppender != null) {
			xmlFileAppender.append(logEvent);
		}
	}

	public void reset(Collection<Appender> appenders) {
		Appender portalTextFileAppender = null;
		Appender portalXmlFileAppender = null;

		for (Appender appender : appenders) {
			if (Objects.equals(_TEXT_FILE, appender.getName())) {
				portalTextFileAppender = appender;
			}
			else if (Objects.equals(_XML_FILE, appender.getName())) {
				portalXmlFileAppender = appender;
			}
		}

		Appender originalPortalTextFileAppender = _portalTextFileAppender;

		if (originalPortalTextFileAppender != portalTextFileAppender) {
			_portalTextFileAppender = portalTextFileAppender;

			_textFileAppenders.clear();
		}

		Appender originalPortalXmlFileAppender = _portalXmlFileAppender;

		if (originalPortalXmlFileAppender != portalXmlFileAppender) {
			_portalXmlFileAppender = portalXmlFileAppender;

			_xmlFileAppenders.clear();
		}
	}

	private Appender _createFileAppender(Appender appender, long companyId) {
		if (appender == null) {
			return null;
		}

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
				null, testFilePattern, Boolean.TRUE.toString(),
				companyId + StringPool.DASH + appender.getName(),
				Boolean.TRUE.toString(), String.valueOf(_BUFFER_SIZE),
				Boolean.TRUE.toString(),
				portalRollingFileAppender.getTriggeringPolicy(), null,
				portalRollingFileAppender.getLayout(), null,
				Boolean.FALSE.toString(), null, null,
				loggerContext.getConfiguration());

		rollingFileAppender.start();

		return rollingFileAppender;
	}

	private static final int _BUFFER_SIZE = 8192;

	private static final String _TEXT_FILE = "TEXT_FILE";

	private static final String _XML_FILE = "XML_FILE";

	private volatile Appender _portalTextFileAppender;
	private volatile Appender _portalXmlFileAppender;
	private final Map<Long, Appender> _textFileAppenders =
		new ConcurrentHashMap<>();
	private final Map<Long, Appender> _xmlFileAppenders =
		new ConcurrentHashMap<>();

}