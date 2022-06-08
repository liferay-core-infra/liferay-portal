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

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.URLCodec;

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
		if (_url == null) {
			return this;
		}

		String[] urlArray = PortalUtil.stripURLAnchor(_url, StringPool.POUND);

		String url = urlArray[0];

		String anchor = urlArray[1];

		StringBundler sb = new StringBundler(6);

		sb.append(url);

		if (url.indexOf(CharPool.QUESTION) == -1) {
			sb.append(StringPool.QUESTION);
		}
		else if (!url.endsWith(StringPool.QUESTION) &&
				 !url.endsWith(StringPool.AMPERSAND)) {

			sb.append(StringPool.AMPERSAND);
		}

		sb.append(name);
		sb.append(StringPool.EQUAL);
		sb.append(URLCodec.encodeURL(value));
		sb.append(anchor);

		_url = HttpComponentsUtil.shortenURL(sb.toString());

		return this;
	}

	public String build() {
		return _url;
	}

	public String toString() {
		return _url;
	}

	private URLBuilder(String url) {
		_url = url;
	}

	private String _url;

}