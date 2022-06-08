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

package com.liferay.portal.kernel.url;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Julius Lee
 */
public class URLBuilder {

	public static URLBuilder create(String url) {
		return new URLBuilder(url);
	}

	public URLBuilder addParameter(String name, boolean value) {
		addParameter(name, String.valueOf(value));

		return this;
	}

	public URLBuilder addParameter(String name, double value) {
		addParameter(name, String.valueOf(value));

		return this;
	}

	public URLBuilder addParameter(String name, int value) {
		addParameter(name, String.valueOf(value));

		return this;
	}

	public URLBuilder addParameter(String name, long value) {
		addParameter(name, String.valueOf(value));

		return this;
	}

	public URLBuilder addParameter(String name, short value) {
		addParameter(name, String.valueOf(value));

		return this;
	}

	public URLBuilder addParameter(String name, String value) {
		_urlOperations.add(new URLOperation(OperationType.ADD, name, value));

		return this;
	}

	public String build() {
		if (_url == null) {
			return null;
		}

		while (!_urlOperations.isEmpty()) {
			URLOperation urlOperation = _urlOperations.remove(0);

			OperationType operationType = urlOperation.getType();
			String name = urlOperation.getName();
			String value = urlOperation.getValue();

			if (operationType == OperationType.ADD) {
				_url = HttpComponentsUtil.addParameter(_url, name, value);
			}
			else if (operationType == OperationType.SET) {
				_url = HttpComponentsUtil.setParameter(_url, name, value);
			}
			else if (operationType == OperationType.REMOVE) {
				_url = HttpComponentsUtil.removeParameter(_url, name);
			}
		}

		return _url;
	}

	public URLBuilder removeParameter(String name) {
		_urlOperations.add(new URLOperation(OperationType.REMOVE, name, null));

		return this;
	}

	public URLBuilder setParameter(String name, boolean value) {
		return setParameter(name, String.valueOf(value));
	}

	public URLBuilder setParameter(String name, double value) {
		return setParameter(name, String.valueOf(value));
	}

	public URLBuilder setParameter(String name, int value) {
		return setParameter(name, String.valueOf(value));
	}

	public URLBuilder setParameter(String name, long value) {
		return setParameter(name, String.valueOf(value));
	}

	public URLBuilder setParameter(String name, short value) {
		return setParameter(name, String.valueOf(value));
	}

	public URLBuilder setParameter(String name, String value) {
		_urlOperations.add(new URLOperation(OperationType.SET, name, value));

		return this;
	}

	public String toString() {
		return _url + _urlOperations;
	}

	private URLBuilder(String url) {
		_url = url;
	}

	private String _url;
	private final List<URLOperation> _urlOperations = new LinkedList<>();

	private static class URLOperation {

		public URLOperation(
			OperationType operationType, String name, String value) {

			_operationType = operationType;
			_name = name;
			_value = value;
		}

		public String getName() {
			return _name;
		}

		public OperationType getType() {
			return _operationType;
		}

		public String getValue() {
			return _value;
		}

		public String toString() {
			if ((_value == null) && (_operationType == OperationType.REMOVE)) {
				return StringUtil.toLowerCase(_operationType.name()) +
					StringPool.COLON + _name;
			}

			return StringBundler.concat(
				StringUtil.toLowerCase(_operationType.name()), StringPool.COLON,
				_name, StringPool.AMPERSAND, _value);
		}

		private final String _name;
		private final OperationType _operationType;
		private final String _value;

	}

	private enum OperationType {

		ADD, REMOVE, SET

	}

}