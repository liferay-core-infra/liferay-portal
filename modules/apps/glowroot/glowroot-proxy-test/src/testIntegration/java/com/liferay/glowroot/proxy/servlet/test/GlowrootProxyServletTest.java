/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.glowroot.proxy.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.http.util.HttpResponse;
import com.liferay.portal.test.http.util.HttpUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lily Chi
 */
@RunWith(Arquillian.class)
public class GlowrootProxyServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testVisitGlowrootPage() throws Exception {
		_visitGlowrootPage("root", _CUSTOMIZE_CONEXT_NAME);
	}

	private void _assertContent(HttpResponse httpResponse, String key) {
		Assert.assertEquals(200, httpResponse.getStatusCode());

		String httpResponseString = httpResponse.toString();

		Assert.assertTrue(httpResponseString.contains(key));
	}

	private URL _assertRedirect(HttpResponse httpResponse, String redirect)
		throws Exception {

		return _assertRedirect(httpResponse, _createURL(redirect));
	}

	private URL _assertRedirect(HttpResponse httpResponse, URL url)
		throws Exception {

		Assert.assertEquals(url.toString(), httpResponse.getRedirect());
		Assert.assertEquals(302, httpResponse.getStatusCode());

		return url;
	}

	private boolean _clean(File directory) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						_clean(file);
					}
					else {
						if (!file.delete()) {
							return false;
						}
					}
				}
			}
		}

		return directory.delete();
	}

	private URL _createURL(String... strings) throws Exception {
		String file = StringBundler.concat(strings);

		if (_targetBundleNameSuffix.equals(_CUSTOMIZE_CONEXT_NAME)) {
			file = StringPool.FORWARD_SLASH + _CUSTOMIZE_CONEXT_NAME + file;
		}

		return new URL("http", "localhost", _TOMCAT_STARTUP_PORT, file);
	}

	private void _deleteTestJars(File targetRootFile, String... jarNames) {
		for (String jarName : jarNames) {
			File jarFile = new File(
				StringBundler.concat(
					targetRootFile, File.separator, "osgi", File.separator,
					"modules", File.separator, jarName));

			if (jarFile.exists()) {
				jarFile.delete();
			}
		}
	}

	private void _deployBundle() {
		String liferayHome = System.getProperty("liferay.home");

		String parentDir = liferayHome.substring(
			0, liferayHome.lastIndexOf(File.separator));

		String tomcatDirName = liferayHome.substring(
			liferayHome.lastIndexOf(File.separator) + 1);

		File sourceRootFile = new File(liferayHome);

		String targetTomcatName = tomcatDirName + _targetBundleNameSuffix;

		File targetRootFile = new File(parentDir, targetTomcatName);

		_tomcatDir = targetRootFile;

		if (targetRootFile.exists() && !_clean(targetRootFile)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Failed to delete " + targetRootFile);
			}
		}

		targetRootFile.mkdir();

		Path source = sourceRootFile.toPath();
		Path target = targetRootFile.toPath();

		try {
			Files.walk(
				source
			).forEach(
				sourceFile -> {
					Path targetFile = target.resolve(
						source.relativize(sourceFile));

					String pathString = targetFile.toString();

					if (pathString.contains("ROOT") &&
						_targetBundleNameSuffix.equals(
							_CUSTOMIZE_CONEXT_NAME)) {

						targetFile = Paths.get(
							StringUtil.replace(
								pathString, "ROOT", _CUSTOMIZE_CONEXT_NAME));
					}

					if (targetFile.endsWith("ROOT.xml") &&
						_targetBundleNameSuffix.equals(
							_CUSTOMIZE_CONEXT_NAME)) {

						targetFile = Paths.get(
							StringUtil.replace(
								pathString, "ROOT.xml", "myportal.xml"));
					}

					if (targetFile.endsWith("catalina.sh")) {
						_tomcatBin = pathString.substring(
							0, pathString.lastIndexOf(File.separator));
					}

					if (targetFile.endsWith("server.xml")) {
						try (BufferedReader reader = Files.newBufferedReader(
								sourceFile);
							BufferedWriter writer = Files.newBufferedWriter(
								targetFile)) {

							String line;

							while ((line = reader.readLine()) != null) {
								if (line.contains(
										"<Connector maxThreads=\"75\" " +
											"port=\"8080\"")) {

									line = StringUtil.replace(
										line,
										"<Connector maxThreads=\"75\" " +
											"port=\"8080\"",
										StringBundler.concat(
											"<Connector maxThreads=\"75\" ",
											"port=\"", _TOMCAT_STARTUP_PORT,
											"\""));
								}

								if (line.contains(
										"<Server port=\"8005\" " +
											"shutdown=\"SHUTDOWN\">")) {

									line = StringUtil.replace(
										line,
										"<Server port=\"8005\" " +
											"shutdown=\"SHUTDOWN\">",
										"<Server port=\"9005\" " +
											"shutdown=\"SHUTDOWN\">");
								}

								writer.write(line);
								writer.newLine();
							}
						}
						catch (IOException ioException) {
							if (_log.isWarnEnabled()) {
								_log.warn(ioException.getCause());
							}
						}
					}
					else if (targetFile.endsWith("catalina.properties") &&
							 _targetBundleNameSuffix.equals(
								 _CUSTOMIZE_CONEXT_NAME)) {

						try (BufferedReader reader = Files.newBufferedReader(
								sourceFile);
							BufferedWriter writer = Files.newBufferedWriter(
								targetFile)) {

							String line;

							while ((line = reader.readLine()) != null) {
								if (line.contains("ROOT")) {
									line = StringUtil.replace(
										line, "ROOT", _CUSTOMIZE_CONEXT_NAME);
								}

								writer.write(line);
								writer.newLine();
							}
						}
						catch (IOException ioException) {
							if (_log.isWarnEnabled()) {
								_log.warn(ioException.getCause());
							}
						}
					}
					else {
						try {
							Files.copy(
								sourceFile, targetFile,
								StandardCopyOption.REPLACE_EXISTING);
						}
						catch (IOException ioException) {
							if (_log.isWarnEnabled()) {
								_log.warn(ioException.getCause());
							}
						}
					}
				}
			);

			File portalExtPropertiesFile = new File(
				targetRootFile, "portal-ext.properties");

			if (portalExtPropertiesFile.exists()) {
				portalExtPropertiesFile.delete();
			}

			try (BufferedWriter writer = Files.newBufferedWriter(
					portalExtPropertiesFile.toPath())) {

				writer.write("browser.launcher.url=");
				writer.newLine();
				writer.write("setup.wizard.enabled=false");
				writer.newLine();
				writer.write("terms.of.use.required=false");
				writer.newLine();
				writer.write("users.reminder.queries.enabled=false");
				writer.newLine();
				writer.write("passwords.default.policy.change.required=false");
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(ioException.getCause());
				}
			}

			File osgiConfigsFile = new File(
				StringBundler.concat(
					targetRootFile, File.separator, "osgi", File.separator,
					"configs"));

			if (!osgiConfigsFile.exists()) {
				osgiConfigsFile.mkdir();
			}

			File esConfigFile = new File(
				osgiConfigsFile,
				"com.liferay.portal.search.elasticsearch7.configuration." +
					"ElasticsearchConfiguration.config");

			if (esConfigFile.exists()) {
				esConfigFile.delete();
			}

			try (BufferedWriter writer = Files.newBufferedWriter(
					esConfigFile.toPath())) {

				writer.write("sidecarHttpPort=\"AUTO\"");
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(ioException.getCause());
				}
			}

			File osgiStateFolder = new File(
				StringBundler.concat(
					targetRootFile, File.separator, "osgi", File.separator,
					"state"));

			if (osgiStateFolder.exists() && !_clean(osgiStateFolder)) {
				if (_log.isWarnEnabled()) {
					_log.warn("Failed to delete " + osgiStateFolder);
				}
			}

			_deleteTestJars(
				targetRootFile,
				"com.liferay.arquillian.extension.junit.bridge.connector.jar",
				"com.liferay.data.guard.connector.jar");
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException.getCause());
			}
		}
	}

	private boolean _isReachable() {
		try {
			String file = "/web/guest";

			if (_targetBundleNameSuffix.equals(_CUSTOMIZE_CONEXT_NAME)) {
				file = StringPool.FORWARD_SLASH + _CUSTOMIZE_CONEXT_NAME + file;
			}

			URL url = new URL("http", "localhost", _TOMCAT_STARTUP_PORT, file);

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)url.openConnection();

			httpURLConnection.setRequestMethod("HEAD");

			int responseCode = httpURLConnection.getResponseCode();

			if ((responseCode > 0) && (responseCode < 400)) {
				return true;
			}
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException.getCause());
			}
		}

		return false;
	}

	private void _loginWithAdminUser() throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("Start to login with admin user");
		}

		HttpResponse httpResponse1 = HttpUtil.doGet(
			null, _createURL(StringPool.FORWARD_SLASH));

		_assertContent(httpResponse1, "Liferay.currentURL");

		String csrfToken = httpResponse1.getCSRFToken();

		HttpResponse httpResponse2 = HttpUtil.doPost(
			null, csrfToken,
			new String[][] {
				{_P_P_ID_NAMESPACE + "_checkboxNames", "rememberMe"},
				{_P_P_ID_NAMESPACE + "_doActionAfterLogin", StringPool.FALSE},
				{
					_P_P_ID_NAMESPACE + "_formDate",
					String.valueOf(System.currentTimeMillis())
				},
				{_P_P_ID_NAMESPACE + "_login", "test@liferay.com"},
				{_P_P_ID_NAMESPACE + "_password", "test"},
				{_P_P_ID_NAMESPACE + "_redirect", StringPool.BLANK},
				{_P_P_ID_NAMESPACE + "_saveLastPath", StringPool.FALSE}
			},
			_createURL(
				"/home?", _P_P_ID_NAMESPACE,
				"_javax.portlet.action=/login/login&", _P_P_ID_NAMESPACE,
				"_mvcRenderCommandName=/login/login&p_p_id=", _P_P_ID,
				"&p_p_lifecycle=1&p_p_mode=view&p_p_state=normal"));

		_assertRedirect(httpResponse2, "/c");

		HttpResponse httpResponse3 = HttpUtil.doGet(
			csrfToken, _createURL("/c"));

		_assertRedirect(httpResponse3, StringPool.SLASH);

		HttpResponse httpResponse4 = HttpUtil.doGet(
			csrfToken, _createURL(StringPool.SLASH));

		_assertContent(
			httpResponse4, "ProductNavigationUserPersonalBarPortlet");
	}

	private boolean _startPortalWithGlowroot(ProcessBuilder processBuilder)
		throws Exception {

		Process process = processBuilder.start();

		BufferedReader stdErrBufferedReader = new BufferedReader(
			new InputStreamReader(process.getErrorStream()));

		FutureTask<Boolean> stdErrTask = new FutureTask<>(
			() -> {
				String line = null;

				while ((line = stdErrBufferedReader.readLine()) != null) {
					int startIndex = line.indexOf(_STARTED_LINE);

					if (_log.isInfoEnabled()) {
						_log.info(line);
					}

					if ((startIndex != -1) &&
						_waitForPortalStartUp(() -> _isReachable())) {

						_process = process;

						return true;
					}
				}

				throw new IllegalStateException("Tomcat is not available!");
			});

		Thread stdErrThread = new Thread(stdErrTask, "Std Err Thread");

		stdErrThread.start();

		BufferedReader stdOutBufferedReader = new BufferedReader(
			new InputStreamReader(process.getInputStream()));

		Thread stdOutThread = new Thread(
			() -> {
				String line = null;

				try {
					while ((line = stdOutBufferedReader.readLine()) != null) {
						if (_log.isInfoEnabled()) {
							_log.info(line);
						}
					}
				}
				catch (IOException ioException) {
					if (_log.isWarnEnabled()) {
						_log.warn(ioException.getCause());
					}
				}
			},
			"Std Out Thread");

		stdOutThread.start();

		stdErrThread.join();

		return stdErrTask.get();
	}

	private void _stopPortal() throws Exception {
		_process.destroy();

		_process.waitFor();

		_clean(_tomcatDir);

		if (_log.isInfoEnabled()) {
			if (_targetBundleNameSuffix.equals(_CUSTOMIZE_CONEXT_NAME)) {
				_log.info("Stop portal with customize context");
			}
			else {
				_log.info("Stop portal with root context");
			}
		}
	}

	private void _visitGlowrootPage(String... targetBundleNameSuffixes)
		throws Exception {

		for (String targetBundleNameSuffix : targetBundleNameSuffixes) {
			_targetBundleNameSuffix = targetBundleNameSuffix;

			_deployBundle();

			ProcessBuilder processBuilder = new ProcessBuilder(
				"sh", "catalina.sh", "glowroot", "run");

			processBuilder.directory(new File(_tomcatBin));

			String context = "root context";
			String contextPath = _ROOT_CONTEXT_GLOWROOT_PAGE_PATH;

			if (_targetBundleNameSuffix.equals(_CUSTOMIZE_CONEXT_NAME)) {
				context = "customize context";
				contextPath =
					StringPool.FORWARD_SLASH + _CUSTOMIZE_CONEXT_NAME +
						contextPath;
			}

			if (!_startPortalWithGlowroot(processBuilder)) {
				if (_log.isWarnEnabled()) {
					_log.warn("Failed to start portal with " + context + "!");
				}
			}
			else {
				if (_log.isInfoEnabled()) {
					_log.info("Started portal with " + context + "!");
				}

				_loginWithAdminUser();

				if (_log.isInfoEnabled()) {
					_log.info(
						"Start to visit glowroot page with " + context + "!");
				}

				String content = String.valueOf(
					HttpUtil.doGet(
						null, _createURL(_ROOT_CONTEXT_GLOWROOT_PAGE_PATH)));

				Assert.assertTrue(
					"Failed to visit " + contextPath,
					content.contains(contextPath));

				_stopPortal();
			}
		}
	}

	private boolean _waitForPortalStartUp(Callable<Boolean> callable)
		throws Exception {

		long end = System.currentTimeMillis() + (long)30000;

		while (System.currentTimeMillis() < end) {
			if (callable.call()) {
				return true;
			}

			Thread.sleep(10000);
		}

		return false;
	}

	private static final String _CUSTOMIZE_CONEXT_NAME = "myportal";

	private static final String _P_P_ID =
		"com_liferay_login_web_portlet_LoginPortlet";

	private static final String _P_P_ID_NAMESPACE =
		StringPool.UNDERLINE + _P_P_ID;

	private static final String _ROOT_CONTEXT_GLOWROOT_PAGE_PATH =
		"/o/glowroot";

	private static final String _STARTED_LINE =
		"org.apache.catalina.startup.Catalina.start Server startup in [";

	private static final int _TOMCAT_STARTUP_PORT = 9080;

	private static final Log _log = LogFactoryUtil.getLog(
		GlowrootProxyServletTest.class);

	private static Process _process;
	private static String _targetBundleNameSuffix;
	private static String _tomcatBin;
	private static File _tomcatDir;

}