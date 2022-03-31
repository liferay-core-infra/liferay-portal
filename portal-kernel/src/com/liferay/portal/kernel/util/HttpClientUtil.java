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

package com.liferay.portal.kernel.util;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

/**
 * @author Janis Zhang
 */
public class HttpClientUtil {

	public static HttpClient getHttpClient() {
		return _httpClient;
	}

	public static byte[] urlToByteArray(HttpClient.Options options)
		throws IOException {

		return _httpClient.urlToByteArray(options);
	}

	public static byte[] urlToByteArray(String location) throws IOException {
		return _httpClient.urlToByteArray(location);
	}

	public static byte[] urlToByteArray(String location, boolean post)
		throws IOException {

		return _httpClient.urlToByteArray(location, post);
	}

	public static InputStream urlToInputStream(HttpClient.Options options)
		throws IOException {

		return _httpClient.urlToInputStream(options);
	}

	public static InputStream urlToInputStream(String location)
		throws IOException {

		return _httpClient.urlToInputStream(location);
	}

	public static InputStream urlToInputStream(String location, boolean post)
		throws IOException {

		return _httpClient.urlToInputStream(location, post);
	}

	public static String urlToString(HttpClient.Options options)
		throws IOException {

		return _httpClient.urlToString(options);
	}

	public static String urlToString(String location) throws IOException {
		return _httpClient.urlToString(location);
	}

	public static String urlToString(String location, boolean post)
		throws IOException {

		return _httpClient.urlToString(location, post);
	}

	/**
	 * This method only uses the default Commons HttpClient implementation when
	 * the URL object represents a HTTP resource. The URL object could also
	 * represent a file or some JNDI resource. In that case, the default Java
	 * implementation is used.
	 *
	 * @param  url the URL
	 * @return A string representation of the resource referenced by the URL
	 *         object
	 * @throws IOException if an IO Exception occurred
	 */
	public static String urlToString(URL url) throws IOException {
		return _httpClient.urlToString(url);
	}

	public void setHttpClient(HttpClient httpClient) {
		_httpClient = httpClient;
	}

	private static HttpClient _httpClient;

}