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

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.io.File;
import java.io.IOException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Simon Jiang
 */
public class ComponentAnnotationCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<String> importNames = getImportNames(detailAST);

		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST != null) ||
			!importNames.contains(
				"org.osgi.service.component.annotations.Component")) {

			return;
		}

		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			detailAST, "Component");

		if (annotationDetailAST == null) {
			return;
		}

		_checkConfigurationPolicy(detailAST, annotationDetailAST);
		_checkConfigurationPid(annotationDetailAST, importNames);
		_checkOSGiJaxrsName(annotationDetailAST, importNames);
	}

	private void _checkConfigurationPid(
		DetailAST annotationDetailAST, List<String> importNames) {

		DetailAST annotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "configurationPid");

		if (annotationMemberValuePairDetailAST == null) {
			return;
		}

		Set<String> configurationClassNames = _getConfigurationClassNames();

		for (DetailAST expressionDetailAST :
				getAllChildTokens(
					annotationMemberValuePairDetailAST, true,
					TokenTypes.EXPR)) {

			FullIdent expressionFullIdent = FullIdent.createFullIdentBelow(
				expressionDetailAST);

			String annotationMemberValue = StringUtil.unquote(
				expressionFullIdent.getText());

			if (!annotationMemberValue.startsWith("com.liferay")) {
				continue;
			}

			int pos = annotationMemberValue.lastIndexOf(".scoped");

			if (pos != -1) {
				annotationMemberValue = annotationMemberValue.substring(
					0, annotationMemberValue.lastIndexOf(CharPool.PERIOD));
			}

			if (importNames.contains(annotationMemberValue) ||
				configurationClassNames.contains(annotationMemberValue)) {

				continue;
			}

			log(
				annotationMemberValuePairDetailAST,
				_MSG_INCORRECT_CONFIGURATION_PID, annotationMemberValue);
		}
	}

	private void _checkConfigurationPolicy(
		DetailAST detailAST, DetailAST annotationDetailAST) {

		String extendsClassName = null;

		DetailAST extendsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.EXTENDS_CLAUSE);

		if (extendsClauseDetailAST != null) {
			DetailAST firstChildDetailAST =
				extendsClauseDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.DOT) {
				FullIdent fullIdent = FullIdent.createFullIdent(
					firstChildDetailAST);

				String[] parts = StringUtil.split(fullIdent.getText(), "\\.");

				extendsClassName = parts[parts.length - 1];
			}
			else if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
				extendsClassName = getName(extendsClauseDetailAST);
			}
		}

		DetailAST annotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "configurationPolicy");

		if (annotationMemberValuePairDetailAST == null) {
			if (Objects.equals(
					extendsClassName, "BaseAuthVerifierPipelineConfigurator")) {

				log(annotationDetailAST, _MSG_INCORRECT_CONFIGURATION_POLICY);
			}

			return;
		}

		DetailAST expressionDetailAST =
			annotationMemberValuePairDetailAST.findFirstToken(TokenTypes.EXPR);

		FullIdent expressionFullIdent = FullIdent.createFullIdentBelow(
			expressionDetailAST);

		String annotationMemberValue = expressionFullIdent.getText();

		if (Objects.equals(
				extendsClassName, "BaseAuthVerifierPipelineConfigurator") &&
			!annotationMemberValue.equals("ConfigurationPolicy.REQUIRE")) {

			log(annotationDetailAST, _MSG_INCORRECT_CONFIGURATION_POLICY);
		}
		else if (annotationMemberValue.equals("ConfigurationPolicy.OPTIONAL")) {
			log(
				annotationDetailAST, _MSG_UNNECESSARY_CONFIGURATION_POLICY,
				annotationMemberValue);
		}
	}

	private void _checkOSGiJaxrsName(
		DetailAST annotationDetailAST, List<String> importNames) {

		if (!importNames.contains("javax.ws.rs.ext.ExceptionMapper") ||
			!_isExceptionMapperService(annotationDetailAST)) {

			return;
		}

		DetailAST propertyAnnotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "property");

		if (propertyAnnotationMemberValuePairDetailAST == null) {
			return;
		}

		DetailAST annotationArrayInitDetailAST =
			propertyAnnotationMemberValuePairDetailAST.findFirstToken(
				TokenTypes.ANNOTATION_ARRAY_INIT);

		if (annotationArrayInitDetailAST == null) {
			return;
		}

		String osgiJaxrsName = _getOSGiJaxrsName(annotationArrayInitDetailAST);

		if (Validator.isNull(osgiJaxrsName)) {
			return;
		}

		if (!osgiJaxrsName.endsWith(_OSGI_SERVICE_NAME)) {
			log(
				annotationArrayInitDetailAST, _MSG_INCORRECT_OSGI_JAXRS_MAME,
				_OSGI_SERVICE_NAME);
		}
	}

	private synchronized Set<String> _getConfigurationClassNames() {
		if (_configurationClasses != null) {
			return _configurationClasses;
		}

		_configurationClasses = new HashSet<>();

		try {
			List<String> configurationClasses =
				SourceFormatterUtil.scanForFiles(
					JavaSourceUtil.getRootDirName(getAbsolutePath()),
					new String[0],
					new String[] {
						"**/com/liferay/**/*Configuration.java",
						"**/com/liferay/**/configuration/*.java",
						"**/com/liferay/**/configuration/**/*.java"
					},
					new SourceFormatterExcludes(), false);

			for (String configurationClass : configurationClasses) {
				configurationClass = configurationClass.substring(
					0, configurationClass.lastIndexOf(CharPool.PERIOD));

				configurationClass = StringUtil.replace(
					configurationClass, File.separatorChar, CharPool.PERIOD);

				_configurationClasses.add(
					configurationClass.substring(
						configurationClass.indexOf("com.liferay")));
			}
		}
		catch (IOException ioException) {
			return _configurationClasses;
		}

		return _configurationClasses;
	}

	private String _getOSGiJaxrsName(DetailAST annotationArrayInitDetailAST) {
		List<DetailAST> expressionDetailASTList = getAllChildTokens(
			annotationArrayInitDetailAST, false, TokenTypes.EXPR);

		for (DetailAST expressionDetailAST : expressionDetailASTList) {
			DetailAST firstChildDetailAST = expressionDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
				continue;
			}

			String value = firstChildDetailAST.getText();

			if (value.startsWith("\"osgi.jaxrs.name=")) {
				return value.substring(17, value.length() - 1);
			}
		}

		return null;
	}

	private boolean _isExceptionMapperService(DetailAST annotationDetailAST) {
		DetailAST serviceAnnotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "service");

		if (serviceAnnotationMemberValuePairDetailAST == null) {
			return false;
		}

		DetailAST exprDetailAST =
			serviceAnnotationMemberValuePairDetailAST.findFirstToken(
				TokenTypes.EXPR);

		if (exprDetailAST == null) {
			return false;
		}

		DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.DOT)) {

			return false;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		if (!Objects.equals(
				fullIdent.getText(), _OSGI_SERVICE_NAME + ".class")) {

			return false;
		}

		return true;
	}

	private static final String _MSG_INCORRECT_CONFIGURATION_PID =
		"configuration.pid.incorrect";

	private static final String _MSG_INCORRECT_CONFIGURATION_POLICY =
		"configuration.policy.incorrect";

	private static final String _MSG_INCORRECT_OSGI_JAXRS_MAME =
		"osgi.jaxrs.name.incorrect";

	private static final String _MSG_UNNECESSARY_CONFIGURATION_POLICY =
		"configuration.policy.unnecessary";

	private static final String _OSGI_SERVICE_NAME = "ExceptionMapper";

	private static Set<String> _configurationClasses;

}