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

package com.liferay.portal.kernel.language;

import com.liferay.portal.kernel.util.LocaleUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jiefeng Wu
 */
public class LanguageUtilTest {

	@Test
	public void testformatStorageSizeOneB() throws Exception {
		long bytes = 1;

		Assert.assertEquals(
			"1 B", LanguageUtil.formatStorageSize(bytes, LocaleUtil.SPAIN));
		Assert.assertEquals(
			"1 B", LanguageUtil.formatStorageSize(bytes, LocaleUtil.US));
	}

	@Test
	public void testformatStorageSizeOneGB() throws Exception {
		long bytes = 1024 * 1024 * 1024;

		Assert.assertEquals(
			"1 GB", LanguageUtil.formatStorageSize(bytes, LocaleUtil.SPAIN));
		Assert.assertEquals(
			"1 GB", LanguageUtil.formatStorageSize(bytes, LocaleUtil.US));
	}

	@Test
	public void testformatStorageSizeOneKB() throws Exception {
		long bytes = 1024;

		Assert.assertEquals(
			"1 KB", LanguageUtil.formatStorageSize(bytes, LocaleUtil.SPAIN));
		Assert.assertEquals(
			"1 KB", LanguageUtil.formatStorageSize(bytes, LocaleUtil.US));
	}

	@Test
	public void testformatStorageSizeOneMB() throws Exception {
		long bytes = 1024 * 1024;

		Assert.assertEquals(
			"1 MB", LanguageUtil.formatStorageSize(bytes, LocaleUtil.SPAIN));
		Assert.assertEquals(
			"1 MB", LanguageUtil.formatStorageSize(bytes, LocaleUtil.US));
	}

}