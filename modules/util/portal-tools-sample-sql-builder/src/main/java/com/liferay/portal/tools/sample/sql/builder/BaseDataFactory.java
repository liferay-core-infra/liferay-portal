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

import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.util.SimpleCounter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lily Chi
 */
public abstract class BaseDataFactory {

	public long getClassNameId(Class<?> clazz) {
		ClassNameModel classNameModel = classNameModels.get(clazz.getName());

		return classNameModel.getClassNameId();
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

}