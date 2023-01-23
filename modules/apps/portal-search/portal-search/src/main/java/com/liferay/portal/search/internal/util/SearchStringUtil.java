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

package com.liferay.portal.search.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author André de Oliveira
 */
public class SearchStringUtil {

	public static String maybe(String s) {
		s = StringUtil.trim(s);

		if (Validator.isBlank(s)) {
			return null;
		}

		return s;
	}

	public static String requireEquals(String expected, String actual) {
		if (!Objects.equals(expected, actual)) {
			throw new RuntimeException(actual + " != " + expected);
		}

		return actual;
	}

	public static String[] splitAndUnquote(String s) {
		if (s == null) {
			return new String[0];
		}

		List<String> finalStringsList = new ArrayList<>();

		for (String splitString : StringUtil.split(s.trim(), CharPool.COMMA)) {
			finalStringsList.add(StringUtil.unquote(splitString.trim()));
		}

		String[] finalStringsArray = new String[finalStringsList.size()];

		for (int i = 0; i < finalStringsArray.length; i++) {
			finalStringsArray[i] = finalStringsList.get(i);
		}

		return finalStringsArray;
	}

}