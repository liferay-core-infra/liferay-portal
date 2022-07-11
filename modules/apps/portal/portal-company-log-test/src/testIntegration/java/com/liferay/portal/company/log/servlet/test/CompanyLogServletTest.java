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

package com.liferay.portal.company.log.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.log4j.Log4JUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchCompanyException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.nio.file.Files;

import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Hai Yu
 */
@RunWith(Arquillian.class)
public class CompanyLogServletTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_newCompany = CompanyTestUtil.addCompany();

		_adminUser = UserTestUtil.addCompanyAdminUser(_newCompany);

		File logFilesDir = new File(
			Log4JUtil.getCompanyLogDirectory(_newCompany.getCompanyId()));

		for (File file : logFilesDir.listFiles()) {
			_logFile = file;

			break;
		}

		_defaultCompany = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		File logFilesDir = _logFile.getParentFile();

		_logFile.delete();

		logFilesDir.delete();

		_companyLocalService.deleteCompany(_newCompany);
	}

	@Test
	public void testListCompaniesLogFilesWithCompanyAdminUser()
		throws Exception {

		_servlet.service(
			_createMockHttpServletRequest("/", _adminUser),
			_mockHttpServletResponse);

		String responseContent = _mockHttpServletResponse.getContentAsString();

		Assert.assertFalse(
			"Response content should not include webId " +
				_defaultCompany.getWebId(),
			responseContent.contains(_defaultCompany.getWebId()));
		Assert.assertTrue(
			"Response content should include webId " + _newCompany.getWebId(),
			responseContent.contains(_newCompany.getWebId()));

		_assertCompanyLogFilesDisplay(_newCompany, responseContent);
	}

	@Test
	public void testListCompaniesLogFilesWithCompanyUser() throws Exception {
		User user = UserTestUtil.addUser(_newCompany);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.company.log.internal.servlet." +
					"CompanyLogServlet",
				LoggerTestUtil.WARN)) {

			_servlet.service(
				_createMockHttpServletRequest("/", user),
				_mockHttpServletResponse);

			Assert.assertEquals(
				HttpServletResponse.SC_FORBIDDEN,
				_mockHttpServletResponse.getStatus());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				PrincipalException.MustBeCompanyAdmin.class,
				throwable.getClass());
		}
	}

	@Test
	public void testListCompaniesLogFilesWithOmniAdminUser() throws Exception {
		User omniAdminUser = null;

		try {
			omniAdminUser = UserTestUtil.addOmniAdminUser();

			_servlet.service(
				_createMockHttpServletRequest("/", omniAdminUser),
				_mockHttpServletResponse);

			String responseContent =
				_mockHttpServletResponse.getContentAsString();

			Assert.assertTrue(
				"Response content should include webId " +
					_defaultCompany.getWebId(),
				responseContent.contains(_defaultCompany.getWebId()));
			Assert.assertTrue(
				"Response content should include webId " +
					_newCompany.getWebId(),
				responseContent.contains(_newCompany.getWebId()));

			_assertCompanyLogFilesDisplay(_defaultCompany, responseContent);
			_assertCompanyLogFilesDisplay(_newCompany, responseContent);
		}
		finally {
			if (omniAdminUser != null) {
				_userLocalService.deleteUser(omniAdminUser);
			}
		}
	}

	@Test
	public void testSendFileWithDownloadForFalse() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest(
				StringBundler.concat(
					"/", _newCompany.getCompanyId(), "/", _logFile.getName()),
				_adminUser);

		mockHttpServletRequest.setParameter("download", "false");

		_servlet.service(mockHttpServletRequest, _mockHttpServletResponse);

		Assert.assertEquals(
			"text/html", _mockHttpServletResponse.getContentType());

		String responseContent = _mockHttpServletResponse.getContentAsString();

		String startIndexInput = "<input name = \"startIndex\"";
		String endIndexInput = "<input name = \"endIndex\"";
		String submitInput = "<input type = \"submit\"";
		String formTag = "<form action = \"";

		Assert.assertTrue(
			"Response content should include " + startIndexInput,
			responseContent.contains(startIndexInput));
		Assert.assertTrue(
			"Response content should include " + endIndexInput,
			responseContent.contains(endIndexInput));
		Assert.assertTrue(
			"Response content should include " + submitInput,
			responseContent.contains(submitInput));
		Assert.assertTrue(
			"Response content should include " + formTag,
			responseContent.contains(formTag));

		String logFileContent = new String(
			Files.readAllBytes(_logFile.toPath()));

		Assert.assertTrue(
			"Response content should include " + logFileContent,
			responseContent.contains(logFileContent));
	}

	@Test
	public void testSendFileWithDownloadForTrue() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest(
				StringBundler.concat(
					StringPool.SLASH, _newCompany.getCompanyId(),
					StringPool.SLASH, _logFile.getName()),
				_adminUser);

		mockHttpServletRequest.setParameter("download", "true");

		_servlet.service(mockHttpServletRequest, _mockHttpServletResponse);

		_assertHttpServletResponse(0, (int)_logFile.length());
	}

	@Test
	public void testSendFileWithNoSuchCompany() throws Exception {
		long companyId = 1;

		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest(
				StringBundler.concat(
					StringPool.SLASH, companyId, StringPool.SLASH,
					_logFile.getName()),
				_adminUser);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.company.log.internal.servlet." +
					"CompanyLogServlet",
				LoggerTestUtil.WARN)) {

			_servlet.service(mockHttpServletRequest, _mockHttpServletResponse);

			Assert.assertEquals(
				HttpServletResponse.SC_NOT_FOUND,
				_mockHttpServletResponse.getStatus());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				NoSuchCompanyException.class, throwable.getClass());
			Assert.assertEquals(
				"No Company exists with the primary key " + companyId,
				throwable.getMessage());
		}
	}

	@Test
	public void testSendFileWithStartIndexGreaterThanOrEqualsToEndIndex()
		throws Exception {

		_assertSendFileWithInvalidData(0, 0);
		_assertSendFileWithInvalidData(10, 10);
		_assertSendFileWithInvalidData(2, 1);
		_assertSendFileWithInvalidData(10, 5);
	}

	@Test
	public void testSendFileWithStartIndexLessThanEndIndex() throws Exception {
		_assertSendFileWithStartIndexLessThanEndIndex(2, 5);
		_assertSendFileWithStartIndexLessThanEndIndex(0, 10);
		_assertSendFileWithStartIndexLessThanEndIndex(1, Integer.MAX_VALUE);
		_assertSendFileWithStartIndexLessThanEndIndex(2, 0);
	}

	@Test
	public void testSendFileWithStartIndexOrEndIndexLessThanZero()
		throws Exception {

		_assertSendFileWithInvalidData(-3, 3);
		_assertSendFileWithInvalidData(-3, -2);
		_assertSendFileWithInvalidData(3, -3);
	}

	@Test
	public void testSendFileWithUnauthorizedAccess() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest(
				StringBundler.concat(
					StringPool.SLASH, _newCompany.getCompanyId(), "/../"),
				_adminUser);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.company.log.internal.servlet." +
					"CompanyLogServlet",
				LoggerTestUtil.WARN)) {

			_servlet.service(mockHttpServletRequest, _mockHttpServletResponse);

			Assert.assertEquals(
				HttpServletResponse.SC_FORBIDDEN,
				_mockHttpServletResponse.getStatus());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(PrincipalException.class, throwable.getClass());
			Assert.assertEquals("Unauthorized access", throwable.getMessage());
		}
	}

	private void _assertCompanyLogFilesDisplay(
			Company company, String responseContent)
		throws Exception {

		String logFilesDirPath = Log4JUtil.getCompanyLogDirectory(
			company.getCompanyId());

		File logFilesDir = new File(logFilesDirPath);

		File[] files = logFilesDir.listFiles();

		Assert.assertTrue(
			"The directory " + logFilesDirPath + " must have log files",
			files.length > 0);

		for (File file : logFilesDir.listFiles()) {
			Assert.assertTrue(
				"Response content should include fileName " + file.getName(),
				responseContent.contains(file.getName()));
		}
	}

	private void _assertHttpServletResponse(int startIndex, int endIndex)
		throws Exception {

		StringBundler sb = new StringBundler(4);

		sb.append(HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		sb.append("; filename=\"");
		sb.append(_logFile.getName());
		sb.append("\"");

		Assert.assertEquals(
			sb.toString(),
			_mockHttpServletResponse.getHeader(
				HttpHeaders.CONTENT_DISPOSITION));

		Assert.assertEquals(
			String.valueOf(endIndex - startIndex),
			_mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_LENGTH));

		Assert.assertEquals(
			_mimeTypes.getContentType(_logFile),
			_mockHttpServletResponse.getContentType());

		String logFileContent = new String(
			Files.readAllBytes(_logFile.toPath()));

		logFileContent = logFileContent.substring(startIndex, endIndex);

		Assert.assertEquals(
			logFileContent, _mockHttpServletResponse.getContentAsString());
	}

	private void _assertSendFileWithInvalidData(int startIndex, int endIndex)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.company.log.internal.servlet." +
					"CompanyLogServlet",
				LoggerTestUtil.WARN)) {

			_servlet.service(
				_createMockHttpServletRequest(startIndex, endIndex),
				_mockHttpServletResponse);

			Assert.assertEquals(
				HttpServletResponse.SC_FORBIDDEN,
				_mockHttpServletResponse.getStatus());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(PrincipalException.class, throwable.getClass());
			Assert.assertEquals(
				"startIndex or endIndex can not be less than 0, and " +
					"startIndex can not be greater than or equal to endIndex",
				throwable.getMessage());
		}
		finally {
			_mockHttpServletResponse.setCommitted(false);
			_mockHttpServletResponse.reset();
		}
	}

	private void _assertSendFileWithStartIndexLessThanEndIndex(
			int startIndex, int endIndex)
		throws Exception {

		try {
			_servlet.service(
				_createMockHttpServletRequest(startIndex, endIndex),
				_mockHttpServletResponse);

			int logFileLength = (int)_logFile.length();

			if ((endIndex == 0) || (endIndex > logFileLength)) {
				endIndex = logFileLength;
			}

			if (startIndex != 0) {
				--startIndex;
			}

			_assertHttpServletResponse(startIndex, endIndex);
		}
		finally {
			_mockHttpServletResponse.setCommitted(false);
			_mockHttpServletResponse.reset();
		}
	}

	private MockHttpServletRequest _createMockHttpServletRequest(
		int startIndex, int endIndex) {

		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest(
				StringBundler.concat(
					StringPool.SLASH, _newCompany.getCompanyId(),
					StringPool.SLASH, _logFile.getName(), "/view-file-content"),
				_adminUser);

		mockHttpServletRequest.setParameter(
			"startIndex", String.valueOf(startIndex));
		mockHttpServletRequest.setParameter(
			"endIndex", String.valueOf(endIndex));

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _createMockHttpServletRequest(
		String path, User user) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(Method.GET, "/company-log" + path);

		mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		mockHttpServletRequest.setContextPath("/company-log");
		mockHttpServletRequest.setPathInfo(path);

		return mockHttpServletRequest;
	}

	private static User _adminUser;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static Company _defaultCompany;
	private static File _logFile;
	private static Company _newCompany;

	@Inject
	private MimeTypes _mimeTypes;

	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.portal.company.log.internal.servlet.CompanyLogServlet"
	)
	private Servlet _servlet;

	@Inject
	private UserLocalService _userLocalService;

}