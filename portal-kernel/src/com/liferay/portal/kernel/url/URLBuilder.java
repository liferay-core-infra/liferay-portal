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

		for (URLOperation operation : _urlOperations) {
			OperationType type = operation.getType();
			String name = operation.getName();
			String value = operation.getValue();

			if (type == OperationType.ADD) {
				_url = HttpComponentsUtil.addParameter(_url, name, value);
			}
			else if (type == OperationType.SET) {
				_url = HttpComponentsUtil.setParameter(_url, name, value);
			}
			else if (type == OperationType.REMOVE) {
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

		public URLOperation(OperationType type, String name, String value) {
			_name = name;
			_value = value;

			_operationType = type;
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
			String type = _operationType.name();

			if ((_value == null) && (_operationType == OperationType.REMOVE)) {
				return type.toLowerCase() + StringPool.COLON + _name;
			}

			return StringBundler.concat(
				type.toLowerCase(), StringPool.COLON, _name,
				StringPool.AMPERSAND, _value);
		}

		private final String _name;
		private final OperationType _operationType;
		private final String _value;

	}

	private enum OperationType {

		ADD, REMOVE, SET

	}

}