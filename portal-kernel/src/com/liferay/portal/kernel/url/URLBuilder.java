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

import com.liferay.portal.kernel.util.HttpComponentsUtil;

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
		_url = HttpComponentsUtil.addParameter(_url, name, value);

		return this;
	}

	public String build() {
		return _url;
	}

	public URLBuilder removeParameter(String name) {
		_url = HttpComponentsUtil.removeParameter(_url, name);

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
		removeParameter(name);

		return addParameter(name, value);
	}

	public String toString() {
		return _url;
	}

	private URLBuilder(String url) {
		_url = url;
	}

	private String _url;

}