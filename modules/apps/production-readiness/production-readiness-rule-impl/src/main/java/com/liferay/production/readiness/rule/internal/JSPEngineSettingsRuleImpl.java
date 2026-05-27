/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class JSPEngineSettingsRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		if (!ServerDetector.isTomcat()) {
			return Collections.emptyList();
		}

		String catalinaBase = System.getProperty("catalina.base");

		if (Validator.isNull(catalinaBase)) {
			catalinaBase = System.getProperty("catalina.home");
		}

		if (Validator.isNull(catalinaBase)) {
			return Collections.emptyList();
		}

		File webXmlFile = new File(catalinaBase, "conf/web.xml");

		if (!webXmlFile.exists()) {
			return Collections.emptyList();
		}

		try {
			String content = FileUtil.read(webXmlFile);

			Document document = SAXReaderUtil.read(content);

			Element rootElement = document.getRootElement();

			Object development = null;
			Object mappedFile = null;

			List<Element> allElements = rootElement.elements();

			for (Element element : allElements) {
				String elementName = element.getName();

				if (!elementName.equals("servlet")) {
					continue;
				}

				String servletName = element.elementText("servlet-name");

				if (!servletName.equals("jsp")) {
					continue;
				}

				List<Element> initParams = element.elements("init-param");

				for (Element param : initParams) {
					String paramName = param.elementText("param-name");
					String paramValue = param.elementText("param-value");

					if (paramName.equals("development")) {
						development = GetterUtil.getBoolean(paramValue);
					}
					else if (paramName.equals("mappedFile")) {
						mappedFile = GetterUtil.getBoolean(paramValue);
					}
				}
			}

			if (Validator.isNotNull(development) &&
				Validator.isNotNull(mappedFile)) {

				if (!(boolean)development && !(boolean)mappedFile) {
					return Collections.singletonList(
						new Result(
							getCategory(),
							StringBundler.concat(
								"development=", development, ", mappedfile=",
								mappedFile),
							null, getKey(),
							"production-readiness-rule-jsp-engine-settings-" +
								"pass",
							new Object[0],
							"development=false, mappedfile=false",
							Result.Severity.LOW, Result.Status.PASS));
				}

				return Collections.singletonList(
					new Result(
						getCategory(),
						StringBundler.concat(
							"development=", development, ", mappedfile=",
							mappedFile),
						null, getKey(),
						"production-readiness-rule-jsp-engine-settings-fail",
						new Object[0], "development=false, mappedfile=false",
						Result.Severity.LOW, Result.Status.FAIL));
			}
			else if (Validator.isNull(development) ||
					 Validator.isNull(mappedFile)) {

				return Collections.singletonList(
					new Result(
						getCategory(),
						"development or mappedfile is not set, Tomcat will " +
							"use the default value development=true or " +
								"mappedfile=true",
						null, getKey(),
						"production-readiness-rule-jsp-engine-settings-fail",
						new Object[0], "development=false, mappedfile=false",
						Result.Severity.LOW, Result.Status.FAIL));
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return Collections.emptyList();
	}

	@Override
	public String getCategory() {
		return "jvm-and-infrastructure-validation";
	}

	@Override
	public String getKey() {
		return "jsp-engine-settings";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSPEngineSettingsRuleImpl.class);

}