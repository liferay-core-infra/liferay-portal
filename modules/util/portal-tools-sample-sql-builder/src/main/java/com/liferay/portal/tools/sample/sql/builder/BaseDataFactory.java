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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.util.SimpleCounter;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public abstract class BaseDataFactory {

	public long getClassNameId(Class<?> clazz) {
		ClassNameModel classNameModel = classNameModels.get(clazz.getName());

		return classNameModel.getClassNameId();
	}

	protected InputStream getResourceInputStream(String resourceName) {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	protected String readFile(InputStream inputStream) throws Exception {
		List<String> lines = new ArrayList<>();

		StringUtil.readLines(inputStream, lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	protected String readFile(String resourceName) throws Exception {
		return readFile(getResourceInputStream(resourceName));
	}

	protected static final long COMPANY_ID;

	protected static final long SAMPLE_USER_ID;

	protected static final String SAMPLE_USER_NAME = "Sample";

	protected static final Map<String, ClassNameModel> classNameModels =
		new HashMap<>();
	protected static final SimpleCounter counter;
	protected static final Map<String, SimpleCounter> layoutIdCounters =
		new HashMap<>();
	protected static final SimpleCounter layoutPlidCounter;

	static {
		counter = new SimpleCounter(
			BenchmarksPropsValues.MAX_GROUP_COUNT +
				BenchmarksPropsValues.MAX_COMMERCE_GROUP_COUNT + 1);

		layoutPlidCounter = new SimpleCounter();

		COMPANY_ID = counter.get();

		SAMPLE_USER_ID = counter.get();
	}

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/data/";

}