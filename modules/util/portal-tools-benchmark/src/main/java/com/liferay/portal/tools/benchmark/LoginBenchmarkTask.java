/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.benchmark;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.tools.benchmark.http.HttpResponse;
import com.liferay.portal.tools.benchmark.http.HttpUtil;
import com.liferay.portal.tools.benchmark.http.SimpleCookieStore;
import com.liferay.portal.tools.benchmark.http.ThreadLocalCookieStore;

import java.net.URL;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Tina Tian
 */
public class LoginBenchmarkTask implements BenchmarkTask {

	public LoginBenchmarkTask(
		String hostName, int port, String email, String password) {

		_hostName = hostName;
		_port = port;
		_email = email;
		_password = password;
	}

	public List<Map.Entry<String, Long>> execute() throws Exception {
		ThreadLocalCookieStore.setCookieStore(new SimpleCookieStore());

		HttpResponse httpResponse = _homePage();

		List<Map.Entry<String, Long>> result = new ArrayList<>();

		result.add(
			new AbstractMap.SimpleEntry<>(
				"homePage", httpResponse.getDuration()));
		result.add(
			new AbstractMap.SimpleEntry<>(
				"viewLoginPage", _viewLoginPage(httpResponse.getCSRFToken())));
		result.add(
			new AbstractMap.SimpleEntry<>(
				"login",
				_login(_email, _password, httpResponse.getCSRFToken())));
		result.add(new AbstractMap.SimpleEntry<>("logout", _logout()));

		ThreadLocalCookieStore.removeCookieStore();

		return result;
	}

	private void _assertResult(HttpResponse httpResponse, String key) {
		if (httpResponse.getStatusCode() != 200) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Response status code is wrong! Expect 200 but actual is ",
					httpResponse.getStatusCode(), " full response is : ",
					httpResponse));
		}

		if (key != null) {
			String httpResponseString = httpResponse.toString();

			if (!httpResponseString.contains(key)) {
				throw new IllegalStateException(
					StringBundler.concat(
						"Unable to find key ", key, " in response : \\n",
						httpResponseString));
			}
		}
	}

	private HttpResponse _homePage() throws Exception {
		HttpResponse httpResponse = HttpUtil.doGet(_newURL("/"), null);

		_assertResult(httpResponse, _KEY_HOME_PAGE);

		return httpResponse;
	}

	private long _login(String userEmail, String password, String csrfToken)
		throws Exception {

		HttpResponse httpResponse = HttpUtil.doPost(
			_newURL(_URL_LOGIN_POST),
			new String[][] {
				{
					_P_P_ID_PREFIX + "_formDate",
					String.valueOf(System.currentTimeMillis())
				},
				{_P_P_ID_PREFIX + "_saveLastPath", "false"},
				{_P_P_ID_PREFIX + "_redirect", ""},
				{_P_P_ID_PREFIX + "_doActionAfterLogin", "false"},
				{_P_P_ID_PREFIX + "_login", userEmail},
				{_P_P_ID_PREFIX + "_password", password},
				{_P_P_ID_PREFIX + "_checkboxNames", "rememberMe"}
			},
			csrfToken, null);

		_assertResult(httpResponse, _KEY_LOGIN);

		return httpResponse.getDuration();
	}

	private long _logout() throws Exception {
		HttpResponse httpResponse = HttpUtil.doGet(_newURL(_URL_LOGOUT), null);

		return httpResponse.getDuration();
	}

	private URL _newURL(String path) throws Exception {
		return new URL("http", _hostName, _port, path);
	}

	private long _viewLoginPage(String csrfToken) throws Exception {
		HttpResponse httpResponse = HttpUtil.doGet(
			_newURL(_URL_LOGIN_POPUP), csrfToken);

		_assertResult(httpResponse, _KEY_LOGIN_POPUP);

		return httpResponse.getDuration();
	}

	private static final String _KEY_HOME_PAGE = "Liferay.currentURL";

	private static final String _KEY_LOGIN =
		"ProductNavigationUserPersonalBarPortlet";

	private static final String _KEY_LOGIN_POPUP = "Remember Me";

	private static final String _P_P_ID =
		"com_liferay_login_web_portlet_LoginPortlet";

	private static final String _P_P_ID_PREFIX = "_" + _P_P_ID;

	private static final String _URL_LOGIN_POPUP =
		"/c/portal/login?windowState=exclusive";

	private static final String _URL_LOGIN_POST = StringBundler.concat(
		"/home?p_p_id=", _P_P_ID, "&p_p_lifecycle=1&",
		"p_p_state=normal&p_p_mode=view&", _P_P_ID_PREFIX,
		"_javax.portlet.action=/login/login&", _P_P_ID_PREFIX,
		"_mvcRenderCommandName=/login/login");

	private static final String _URL_LOGOUT = "/c/portal/logout";

	private final String _email;
	private final String _hostName;
	private final String _password;
	private final int _port;

}