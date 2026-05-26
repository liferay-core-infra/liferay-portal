/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.Collection;
import java.util.Iterator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Lily Chi
 */
@RunWith(Arquillian.class)
public class JSPEngineSettingsRuleImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_originalCatalinaBase = System.getProperty("catalina.base");

		_confDir = temporaryFolder.newFolder("conf");

		System.setProperty(
			"catalina.base",
			temporaryFolder.getRoot(
			).getAbsolutePath());
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
	public void testCheckReturnsFailWhenDevelopmentAndMappedFileAreNotSet()
		throws Exception {

		_writeWebXml(_buildWebXml());

		_assertStatus(Result.Status.FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenDevelopmentIsFalseAndMappedFileIsNotSet()
		throws Exception {

		_writeWebXml(_buildWebXml(_initParam("development", "false")));

		_assertStatus(Result.Status.FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenDevelopmentIsFalseAndMappedFileIsTrue()
		throws Exception {

		_writeWebXml(
			_buildWebXml(
				_initParam("development", "false"),
				_initParam("mappedFile", "true")));

		_assertStatus(Result.Status.FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenDevelopmentIsNotSetAndMappedFileIsFalse()
		throws Exception {

		_writeWebXml(_buildWebXml(_initParam("mappedFile", "false")));

		_assertStatus(Result.Status.FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenDevelopmentIsNotSetAndMappedFileIsTrue()
		throws Exception {

		_writeWebXml(_buildWebXml(_initParam("mappedFile", "true")));

		_assertStatus(Result.Status.FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenDevelopmentIsTrueAndMappedFileIsFalse()
		throws Exception {

		_writeWebXml(
			_buildWebXml(
				_initParam("development", "true"),
				_initParam("mappedFile", "false")));

		_assertStatus(Result.Status.FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenDevelopmentIsTrueAndMappedFileIsNotSet()
		throws Exception {

		_writeWebXml(_buildWebXml(_initParam("development", "true")));

		_assertStatus(Result.Status.FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenDevelopmentIsTrueAndMappedFileIsTrue()
		throws Exception {

		_writeWebXml(
			_buildWebXml(
				_initParam("development", "true"),
				_initParam("mappedFile", "true")));

		_assertStatus(Result.Status.FAIL);
	}

	@Test
	public void testCheckReturnsPassWhenDevelopmentAndMappedFileAreFalse()
		throws Exception {

		_writeWebXml(
			_buildWebXml(
				_initParam("development", "false"),
				_initParam("mappedFile", "false")));

		_assertStatus(Result.Status.PASS);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _assertStatus(Result.Status expectedStatus) throws Exception {
		BundleContext bundleContext = _getBundleContext();

		ServiceReference<ProductionReadinessRule> serviceReference =
			_getJSPEngineSettingsRuleServiceReference(bundleContext);

		try {
			ProductionReadinessRule productionReadinessRule =
				bundleContext.getService(serviceReference);

			Collection<Result> results = productionReadinessRule.check(
				PortalUtil.getDefaultCompanyId());

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(expectedStatus, result.getStatus());
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	private String _buildWebXml(String... initParams) {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<web-app>\n");
		sb.append("\t<servlet>\n");
		sb.append("\t\t<servlet-name>jsp</servlet-name>\n");
		sb.append(
			"\t\t<servlet-class>org.apache.jasper.servlet.JspServlet" +
				"</servlet-class>\n");

		for (String initParam : initParams) {
			sb.append(initParam);
		}

		sb.append("\t</servlet>\n");
		sb.append("</web-app>\n");

		return sb.toString();
	}

	private BundleContext _getBundleContext() {
		return FrameworkUtil.getBundle(
			JSPEngineSettingsRuleImplTest.class
		).getBundleContext();
	}

	private ServiceReference<ProductionReadinessRule>
			_getJSPEngineSettingsRuleServiceReference(
				BundleContext bundleContext)
		throws Exception {

		Collection<ServiceReference<ProductionReadinessRule>>
			serviceReferences = bundleContext.getServiceReferences(
				ProductionReadinessRule.class, null);

		Iterator<ServiceReference<ProductionReadinessRule>> iterator =
			serviceReferences.iterator();

		while (iterator.hasNext()) {
			ServiceReference<ProductionReadinessRule> serviceReference =
				iterator.next();

			ProductionReadinessRule productionReadinessRule =
				bundleContext.getService(serviceReference);

			try {
				if (_RULE_KEY.equals(productionReadinessRule.getKey())) {
					return serviceReference;
				}
			}
			finally {
				bundleContext.ungetService(serviceReference);
			}
		}

		throw new AssertionError(
			"The production-readiness-rule-impl bundle is not deployed: no " +
				"ProductionReadinessRule was registered with key " + _RULE_KEY);
	}

	private String _initParam(String name, String value) {
		StringBuilder sb = new StringBuilder();

		sb.append("\t\t<init-param>\n");
		sb.append("\t\t\t<param-name>");
		sb.append(name);
		sb.append("</param-name>\n");
		sb.append("\t\t\t<param-value>");
		sb.append(value);
		sb.append("</param-value>\n");
		sb.append("\t\t</init-param>\n");

		return sb.toString();
	}

	private void _writeWebXml(String content) throws Exception {
		File webXmlFile = new File(_confDir, "web.xml");

		Files.write(
			webXmlFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	private static final String _RULE_KEY = "jsp-engine-settings";

	private File _confDir;
	private String _originalCatalinaBase;

}