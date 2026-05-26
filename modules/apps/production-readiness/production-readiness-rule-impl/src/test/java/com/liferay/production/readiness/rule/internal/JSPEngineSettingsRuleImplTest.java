/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class JSPEngineSettingsRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_originalCatalinaBase = System.getProperty("catalina.base");

		File confDir = temporaryFolder.newFolder("conf");

		File rootDir = temporaryFolder.getRoot();

		File webXmlFile = new File(confDir, "web.xml");

		webXmlFile.createNewFile();

		System.setProperty("catalina.base", rootDir.getAbsolutePath());
	}

	@After
	public void tearDown() {
		if (_originalCatalinaBase == null) {
			System.clearProperty("catalina.base");
		}
		else {
			System.setProperty("catalina.base", _originalCatalinaBase);
		}
	}

	@Test
	public void testCheckReturnsFailWhenBothAreTrue() {
		_assertFailWithValues(_initParams(true, true), "development=true");
	}

	@Test
	public void testCheckReturnsFailWhenBothInitParamsAreMissing() {
		_assertFailNotSet(Collections.emptyMap());
	}

	@Test
	public void testCheckReturnsFailWhenDevelopmentInitParamIsMissing() {
		_assertFailNotSet(Collections.singletonMap("mappedFile", "false"));
	}

	@Test
	public void testCheckReturnsFailWhenDevelopmentIsTrue() {
		_assertFailWithValues(_initParams(true, false), "development=true");
	}

	@Test
	public void testCheckReturnsFailWhenMappedFileInitParamIsMissing() {
		_assertFailNotSet(Collections.singletonMap("development", "false"));
	}

	@Test
	public void testCheckReturnsFailWhenMappedFileIsTrue() {
		_assertFailWithValues(_initParams(false, true), "mappedfile=true");
	}

	@Test
	public void testCheckReturnsPassWhenDevelopmentAndMappedFileAreFalse() {
		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class);
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class);
			MockedStatic<SAXReaderUtil> saxReaderUtilMockedStatic =
				Mockito.mockStatic(SAXReaderUtil.class)) {

			_mockTomcat(serverDetectorMockedStatic);
			_mockFileRead(fileUtilMockedStatic);
			_mockSAXReader(
				saxReaderUtilMockedStatic,
				Arrays.asList(
					_mockOtherElement("filter"),
					_mockOtherServletElement("default"),
					_mockJspServletElement(_initParams(false, false))));

			Collection<Result> results = _jspEngineSettingsRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
			Assert.assertEquals(
				"jvm-and-infrastructure-validation", result.getCategory());
			Assert.assertEquals(_MESSAGE_KEY_PASS, result.getMessageKey());
			Assert.assertEquals(
				"development=false, mappedfile=false",
				result.getRecommendedValue());
			Assert.assertEquals(
				"development=false, mappedfile=false",
				result.getCurrentValue());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _assertFailNotSet(Map<String, String> initParams) {
		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class);
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class);
			MockedStatic<SAXReaderUtil> saxReaderUtilMockedStatic =
				Mockito.mockStatic(SAXReaderUtil.class)) {

			_mockTomcat(serverDetectorMockedStatic);
			_mockFileRead(fileUtilMockedStatic);
			_mockSAXReader(
				saxReaderUtilMockedStatic,
				Collections.singletonList(_mockJspServletElement(initParams)));

			Collection<Result> results = _jspEngineSettingsRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
			Assert.assertEquals(_MESSAGE_KEY_FAIL, result.getMessageKey());
			Assert.assertTrue(
				result.getCurrentValue(),
				result.getCurrentValue(
				).contains(
					"is not set"
				));
		}
	}

	private void _assertFailWithValues(
		Map<String, String> initParams, String expectedValueFragment) {

		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class);
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class);
			MockedStatic<SAXReaderUtil> saxReaderUtilMockedStatic =
				Mockito.mockStatic(SAXReaderUtil.class)) {

			_mockTomcat(serverDetectorMockedStatic);
			_mockFileRead(fileUtilMockedStatic);
			_mockSAXReader(
				saxReaderUtilMockedStatic,
				Collections.singletonList(_mockJspServletElement(initParams)));

			Collection<Result> results = _jspEngineSettingsRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
			Assert.assertEquals(_MESSAGE_KEY_FAIL, result.getMessageKey());
			Assert.assertTrue(
				result.getCurrentValue(),
				result.getCurrentValue(
				).contains(
					expectedValueFragment
				));
		}
	}

	private Map<String, String> _initParams(
		boolean development, boolean mappedFile) {

		return LinkedHashMapBuilder.put(
			"development", String.valueOf(development)
		).put(
			"mappedFile", String.valueOf(mappedFile)
		).build();
	}

	private void _mockFileRead(MockedStatic<FileUtil> fileUtilMockedStatic) {
		fileUtilMockedStatic.when(
			() -> FileUtil.read(Mockito.any(File.class))
		).thenReturn(
			"<web-app/>"
		);
	}

	private Element _mockInitParamElement(String paramName, String paramValue) {
		Element element = Mockito.mock(Element.class);

		Mockito.when(
			element.elementText("param-name")
		).thenReturn(
			paramName
		);

		Mockito.when(
			element.elementText("param-value")
		).thenReturn(
			paramValue
		);

		return element;
	}

	private Element _mockJspServletElement(Map<String, String> initParams) {
		Element element = Mockito.mock(Element.class);

		Mockito.when(
			element.getName()
		).thenReturn(
			"servlet"
		);

		Mockito.when(
			element.elementText("servlet-name")
		).thenReturn(
			"jsp"
		);

		List<Element> initParamElements = new ArrayList<>();

		for (Map.Entry<String, String> entry : initParams.entrySet()) {
			initParamElements.add(
				_mockInitParamElement(entry.getKey(), entry.getValue()));
		}

		Mockito.when(
			element.elements("init-param")
		).thenReturn(
			initParamElements
		);

		return element;
	}

	private Element _mockOtherElement(String elementName) {
		Element element = Mockito.mock(Element.class);

		Mockito.when(
			element.getName()
		).thenReturn(
			elementName
		);

		return element;
	}

	private Element _mockOtherServletElement(String servletName) {
		Element element = Mockito.mock(Element.class);

		Mockito.when(
			element.getName()
		).thenReturn(
			"servlet"
		);

		Mockito.when(
			element.elementText("servlet-name")
		).thenReturn(
			servletName
		);

		return element;
	}

	private void _mockSAXReader(
		MockedStatic<SAXReaderUtil> saxReaderUtilMockedStatic,
		List<Element> rootChildren) {

		Element rootElement = Mockito.mock(Element.class);

		Mockito.when(
			rootElement.elements()
		).thenReturn(
			rootChildren
		);

		Document document = Mockito.mock(Document.class);

		Mockito.when(
			document.getRootElement()
		).thenReturn(
			rootElement
		);

		saxReaderUtilMockedStatic.when(
			() -> SAXReaderUtil.read(Mockito.anyString())
		).thenReturn(
			document
		);
	}

	private void _mockTomcat(
		MockedStatic<ServerDetector> serverDetectorMockedStatic) {

		serverDetectorMockedStatic.when(
			ServerDetector::isTomcat
		).thenReturn(
			true
		);
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-jsp-engine-settings-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-jsp-engine-settings-pass";

	private final JSPEngineSettingsRuleImpl _jspEngineSettingsRuleImpl =
		new JSPEngineSettingsRuleImpl();
	private String _originalCatalinaBase;

}