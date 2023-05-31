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

package com.liferay.portal.search.test.util;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.index.IndexStatusManager;

import java.util.concurrent.TimeUnit;

/**
 * @author André de Oliveira
 */
public class SearchFixture {

	public static void setUp(IndexStatusManager indexStatusManager) {
		SearchFixture searchFixture = new SearchFixture(indexStatusManager);

		searchFixture.setUp();
	}

	public static void tearDown(IndexStatusManager indexStatusManager) {
		SearchFixture searchFixture = new SearchFixture(indexStatusManager);

		searchFixture.tearDown();
	}

	public SearchFixture(IndexStatusManager indexStatusManager) {
		_indexStatusManager = indexStatusManager;
	}

	public void setUp() {
		retry(
			() ->
				_originalIndexReadOnlyValue =
					ReflectionTestUtil.getAndSetFieldValue(
						_indexStatusManager, "_indexReadOnly", false));
	}

	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			_indexStatusManager, "_indexReadOnly",
			_originalIndexReadOnlyValue);
	}

	protected void retry(Runnable runnable) {
		SearchRetryFixture.builder(
		).timeout(
			10, TimeUnit.SECONDS
		).build(
		).assertSearch(
			runnable
		);
	}

	private final IndexStatusManager _indexStatusManager;
	private Boolean _originalIndexReadOnlyValue;

}