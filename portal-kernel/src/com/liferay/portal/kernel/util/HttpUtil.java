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

import java.net.URI;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class HttpUtil {

	public static String addParameter(String url, String name, boolean value) {
		return _http.addParameter(url, name, value);
	}

	public static String addParameter(String url, String name, double value) {
		return _http.addParameter(url, name, value);
	}

	public static String addParameter(String url, String name, int value) {
		return _http.addParameter(url, name, value);
	}

	public static String addParameter(String url, String name, long value) {
		return _http.addParameter(url, name, value);
	}

	public static String addParameter(String url, String name, short value) {
		return _http.addParameter(url, name, value);
	}

	public static String addParameter(String url, String name, String value) {
		return _http.addParameter(url, name, value);
	}

	public static String decodePath(String path) {
		return _http.decodePath(path);
	}

	public static String decodeURL(String url) {
		return _http.decodeURL(url);
	}

	public static String encodeParameters(String url) {
		return _http.encodeParameters(url);
	}

	public static String encodePath(String path) {
		return _http.encodePath(path);
	}

	public static String fixPath(String path) {
		return _http.fixPath(path);
	}

	public static String fixPath(
		String path, boolean leading, boolean trailing) {

		return _http.fixPath(path, leading, trailing);
	}

	public static String getCompleteURL(HttpServletRequest httpServletRequest) {
		return _http.getCompleteURL(httpServletRequest);
	}

	public static Cookie[] getCookies() {
		return _http.getCookies();
	}

	public static String getDomain(String url) {
		return _http.getDomain(url);
	}

	public static Http getHttp() {
		return _http;
	}

	public static String getIpAddress(String url) {
		return _http.getIpAddress(url);
	}

	public static String getParameter(String url, String name) {
		return _http.getParameter(url, name);
	}

	public static String getParameter(
		String url, String name, boolean escaped) {

		return _http.getParameter(url, name, escaped);
	}

	public static Map<String, String[]> getParameterMap(String queryString) {
		return _http.getParameterMap(queryString);
	}

	public static String getPath(String url) {
		return _http.getPath(url);
	}

	public static String getProtocol(ActionRequest actionRequest) {
		return _http.getProtocol(actionRequest);
	}

	public static String getProtocol(boolean secure) {
		return _http.getProtocol(secure);
	}

	public static String getProtocol(HttpServletRequest httpServletRequest) {
		return _http.getProtocol(httpServletRequest);
	}

	public static String getProtocol(RenderRequest renderRequest) {
		return _http.getProtocol(renderRequest);
	}

	public static String getProtocol(String url) {
		return _http.getProtocol(url);
	}

	public static String getQueryString(HttpServletRequest httpServletRequest) {
		return _http.getQueryString(httpServletRequest);
	}

	public static String getQueryString(String url) {
		return _http.getQueryString(url);
	}

	public static String getRequestURL(HttpServletRequest httpServletRequest) {
		return _http.getRequestURL(httpServletRequest);
	}

	public static URI getURI(String uriString) {
		return _http.getURI(uriString);
	}

	public static boolean hasDomain(String url) {
		return _http.hasDomain(url);
	}

	public static boolean hasProtocol(String url) {
		return _http.hasProtocol(url);
	}

	public static boolean hasProxyConfig() {
		return _http.hasProxyConfig();
	}

	public static boolean isForwarded(HttpServletRequest httpServletRequest) {
		return _http.isForwarded(httpServletRequest);
	}

	public static boolean isNonProxyHost(String host) {
		return _http.isNonProxyHost(host);
	}

	public static boolean isProxyHost(String host) {
		return _http.isProxyHost(host);
	}

	public static boolean isSecure(String url) {
		return _http.isSecure(url);
	}

	public static String normalizePath(String uri) {
		return _http.normalizePath(uri);
	}

	public static Map<String, String[]> parameterMapFromString(
		String queryString) {

		return _http.parameterMapFromString(queryString);
	}

	public static String parameterMapToString(
		Map<String, String[]> parameterMap) {

		return _http.parameterMapToString(parameterMap);
	}

	public static String parameterMapToString(
		Map<String, String[]> parameterMap, boolean addQuestion) {

		return _http.parameterMapToString(parameterMap, addQuestion);
	}

	public static String protocolize(String url, ActionRequest actionRequest) {
		return _http.protocolize(url, actionRequest);
	}

	public static String protocolize(String url, boolean secure) {
		return _http.protocolize(url, secure);
	}

	public static String protocolize(
		String url, HttpServletRequest httpServletRequest) {

		return _http.protocolize(url, httpServletRequest);
	}

	public static String protocolize(String url, int port, boolean secure) {
		return _http.protocolize(url, port, secure);
	}

	public static String protocolize(String url, RenderRequest renderRequest) {
		return _http.protocolize(url, renderRequest);
	}

	public static String removeDomain(String url) {
		return _http.removeDomain(url);
	}

	public static String removeParameter(String url, String name) {
		return _http.removeParameter(url, name);
	}

	public static String removePathParameters(String uri) {
		return _http.removePathParameters(uri);
	}

	public static String removeProtocol(String url) {
		return _http.removeProtocol(url);
	}

	public static String sanitizeHeader(String header) {
		return _http.sanitizeHeader(header);
	}

	public static String setParameter(String url, String name, boolean value) {
		return _http.setParameter(url, name, value);
	}

	public static String setParameter(String url, String name, double value) {
		return _http.setParameter(url, name, value);
	}

	public static String setParameter(String url, String name, int value) {
		return _http.setParameter(url, name, value);
	}

	public static String setParameter(String url, String name, long value) {
		return _http.setParameter(url, name, value);
	}

	public static String setParameter(String url, String name, short value) {
		return _http.setParameter(url, name, value);
	}

	public static String setParameter(String url, String name, String value) {
		return _http.setParameter(url, name, value);
	}

	public static String shortenURL(String url) {
		return _http.shortenURL(url);
	}

	public void setHttp(Http http) {
		_http = http;
	}

	private static Http _http;

}