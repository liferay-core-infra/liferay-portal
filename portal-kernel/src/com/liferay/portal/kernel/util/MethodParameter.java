/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Igor Spasic
 */
public class MethodParameter {

	public MethodParameter(String name, String signatures, Class<?> type) {
		_name = name;
		_type = type;

		try {
			_genericTypes = _getGenericTypes(signatures);
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new IllegalArgumentException(classNotFoundException);
		}
	}

	public Class<?>[] getGenericTypes() {
		return _genericTypes;
	}

	public String getName() {
		return _name;
	}

	public Class<?> getType() {
		return _type;
	}

	private Class<?> _getGenericType(String signature)
		throws ClassNotFoundException {

		if (signature.endsWith("[]")) {
			String baseClassName = signature.substring(
				0, signature.length() - 2);

			Class<?> baseClass = Class.forName(baseClassName);

			return Array.newInstance(
				baseClass, 0
			).getClass();
		}

		return Class.forName(signature);
	}

	private Class<?>[] _getGenericTypes(String signatures)
		throws ClassNotFoundException {

		if (signatures == null) {
			return null;
		}

		int leftBracketIndex = signatures.indexOf(CharPool.LESS_THAN);

		if (leftBracketIndex == -1) {
			return null;
		}

		int rightBracketIndex = signatures.lastIndexOf(CharPool.GREATER_THAN);

		if (rightBracketIndex == -1) {
			return null;
		}

		String generics = signatures.substring(
			leftBracketIndex + 1, rightBracketIndex);

		List<Class<?>> genericTypeslist = new ArrayList<>();

		String[] genericTypeNames = StringUtil.split(generics, CharPool.COMMA);

		for (String genericTypeName : genericTypeNames) {
			String className = StringUtil.removeSubstring(
				genericTypeName, StringPool.SPACE);

			genericTypeslist.add(_getGenericType(className));
		}

		if (genericTypeslist.isEmpty()) {
			return null;
		}

		return genericTypeslist.toArray(new Class<?>[0]);
	}

	private final Class<?>[] _genericTypes;
	private final String _name;
	private final Class<?> _type;

}