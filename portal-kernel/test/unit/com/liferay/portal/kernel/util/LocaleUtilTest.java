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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wesley Gong
 */
public class LocaleUtilTest {

	@Test
	public void testFromLanguageId() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.isAvailableLocale(Locale.US)
		).thenReturn(
			true
		);

		Mockito.when(
			language.isAvailableLanguageCode("en")
		).thenReturn(
			false
		);

		Mockito.when(
			language.isAvailableLanguageCode("fr")
		).thenReturn(
			true
		);

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				LocaleUtil.class.getName(), Level.WARNING)) {

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(Locale.US, LocaleUtil.fromLanguageId("en_US"));
			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			logEntries.clear();

			LocaleUtil.fromLanguageId("en");

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"en is not a valid language id", logEntry.getMessage());

			logEntries.clear();

			Assert.assertEquals(Locale.FRENCH, LocaleUtil.fromLanguageId("fr"));
			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testFromLanguageIdBCP47() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.isAvailableLocale(Locale.US)
		).thenReturn(
			true
		);

		Assert.assertEquals(Locale.US, LocaleUtil.fromLanguageId("en-US"));

		Mockito.when(
			language.isAvailableLocale(Locale.SIMPLIFIED_CHINESE)
		).thenReturn(
			true
		);

		Assert.assertEquals(
			Locale.SIMPLIFIED_CHINESE, LocaleUtil.fromLanguageId("zh-Hans-CN"));

		Mockito.when(
			language.isAvailableLocale(Locale.TRADITIONAL_CHINESE)
		).thenReturn(
			true
		);

		Assert.assertEquals(
			Locale.TRADITIONAL_CHINESE,
			LocaleUtil.fromLanguageId("zh-Hant-TW"));
	}

	@Test
	public void testFromLanguageIdConsistency() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.isAvailableLocale(Locale.GERMANY)
		).thenReturn(
			false
		);

		Assert.assertNull(LocaleUtil.fromLanguageId("de_DE", true, false));

		Assert.assertEquals(
			Locale.GERMANY, LocaleUtil.fromLanguageId("de_DE", false));

		Assert.assertNull(LocaleUtil.fromLanguageId("de_DE", true, false));
	}

	@Test
	public void testFromLanguageIdLocaleIsCreatedAndRetrievableWhenNoValidationDone() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.isAvailableLocale(Locale.ITALY)
		).thenReturn(
			false
		);

		Assert.assertSame(
			LocaleUtil.fromLanguageId("it_IT", false),
			LocaleUtil.fromLanguageId("it_IT", false));
	}

}