/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public class UnusedVariableCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.VARIABLE_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.OBJBLOCK) {
			parentDetailAST = parentDetailAST.getParent();

			if (parentDetailAST.getParent() != null) {
				return;
			}
		}
		else if (parentDetailAST.getType() != TokenTypes.SLIST) {
			return;
		}

		DetailAST typeDetailAST = detailAST.findFirstToken(TokenTypes.TYPE);

		DetailAST firstChildDetailAST = typeDetailAST.getFirstChild();

		if (firstChildDetailAST == null) {
			return;
		}

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			String variableTypeName = getTypeName(typeDetailAST, false);

			List<String> allowedUnusedVariableTypeNames = getAttributeValues(
				_ALLOWED_UNUSED_VARIABLE_TYPE_NAMES_KEY);

			if (allowedUnusedVariableTypeNames.contains(variableTypeName)) {
				return;
			}
		}

		if (_hasSuppressUnusedDeclarationWarning(parentDetailAST) ||
			_hasSuppressUnusedDeclarationWarning(detailAST)) {

			return;
		}

		DetailAST modifiersDetailAST = detailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PROTECTED) ||
			modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC)) {

			return;
		}

		if (modifiersDetailAST.branchContains(TokenTypes.ANNOTATION)) {
			if (!isAttributeValue(_CHECK_UNUSED_REFERENCE_VARIABLE_KEY)) {
				return;
			}

			String absolutePath = getAbsolutePath();

			int x = absolutePath.indexOf("/modules/");

			if (x == -1) {
				return;
			}

			String modulePath = absolutePath.substring(x + 1);

			if ((!modulePath.startsWith("modules/apps") &&
				 !modulePath.startsWith("modules/dxp")) ||
				!AnnotationUtil.containsAnnotation(detailAST, "Reference")) {

				return;
			}

			List<String> checkUnusedReferenceVariableDirNames =
				getAttributeValues(
					_CHECK_UNUSED_REFERENCE_VARIABLE_DIR_NAMES_KEY);

			if (!checkUnusedReferenceVariableDirNames.isEmpty()) {
				int i = 0;

				while (i < checkUnusedReferenceVariableDirNames.size()) {
					if (modulePath.startsWith(
							checkUnusedReferenceVariableDirNames.get(i))) {

						break;
					}

					i++;
				}

				if (i == checkUnusedReferenceVariableDirNames.size()) {
					return;
				}
			}
		}

		String variableName = getName(detailAST);

		if (variableName.equals("serialVersionUID")) {
			return;
		}

		List<DetailAST> variableCallerDetailASTList =
			getVariableCallerDetailASTList(detailAST, variableName);

		if (variableCallerDetailASTList.isEmpty()) {
			log(detailAST, _MSG_UNUSED_VARIABLE, variableName);

			return;
		}

		boolean fieldVariable = _isFieldVariable(detailAST);

		for (DetailAST variableCallerDetailAST : variableCallerDetailASTList) {
			DetailAST previousSiblingDetailAST =
				variableCallerDetailAST.getPreviousSibling();

			if (previousSiblingDetailAST != null) {
				return;
			}

			parentDetailAST = variableCallerDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.ASSIGN) {
				return;
			}

			parentDetailAST = parentDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.EXPR) {
				return;
			}

			if (_isInsideConstructor(variableCallerDetailAST)) {
				if (fieldVariable &&
					!_isConstructorParameter(
						variableCallerDetailAST.getNextSibling())) {

					continue;
				}

				return;
			}

			if (!_isInsidePrivateMethod(variableCallerDetailAST)) {
				return;
			}
		}

		log(detailAST, _MSG_UNUSED_VARIABLE_VALUE, variableName);
	}

	private boolean _hasSuppressUnusedDeclarationWarning(DetailAST detailAST) {
		if ((detailAST.getType() != TokenTypes.VARIABLE_DEF) &&
			(detailAST.getType() != TokenTypes.CLASS_DEF)) {

			return false;
		}

		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			detailAST, SuppressWarnings.class.getSimpleName());

		if (annotationDetailAST == null) {
			return false;
		}

		for (DetailAST exprDetailAST :
				getAllChildTokens(annotationDetailAST, true, TokenTypes.EXPR)) {

			DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() == TokenTypes.STRING_LITERAL) &&
				Objects.equals(
					firstChildDetailAST.getText(), "\"UnusedDeclaration\"")) {

				return true;
			}
		}

		return false;
	}

	private boolean _isConstructorParameter(DetailAST detailAST) {
		String variableName = null;

		if (detailAST.getType() == TokenTypes.IDENT) {
			variableName = detailAST.getText();
		}

		if (variableName == null) {
			return false;
		}

		DetailAST parameterDefinitionDetailAST = getVariableDefinitionDetailAST(
			detailAST, variableName);

		if ((parameterDefinitionDetailAST == null) ||
			(parameterDefinitionDetailAST.getType() !=
				TokenTypes.PARAMETER_DEF)) {

			return false;
		}

		DetailAST parentDetailAST = parameterDefinitionDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.PARAMETERS) {
			return false;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.CTOR_DEF) {
			return true;
		}

		return false;
	}

	private boolean _isFieldVariable(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.OBJBLOCK) {
			return false;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.CLASS_DEF) {
			return false;
		}

		return true;
	}

	private boolean _isInsideConstructor(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		while (parentDetailAST != null) {
			if (parentDetailAST.getType() == TokenTypes.CTOR_DEF) {
				return true;
			}

			parentDetailAST = parentDetailAST.getParent();
		}

		return false;
	}

	private boolean _isInsidePrivateMethod(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		while (parentDetailAST != null) {
			if (parentDetailAST.getType() == TokenTypes.METHOD_DEF) {
				DetailAST modifiersDetailAST = parentDetailAST.findFirstToken(
					TokenTypes.MODIFIERS);

				if (modifiersDetailAST.branchContains(
						TokenTypes.LITERAL_PRIVATE)) {

					return true;
				}
			}

			parentDetailAST = parentDetailAST.getParent();
		}

		return false;
	}

	private static final String _ALLOWED_UNUSED_VARIABLE_TYPE_NAMES_KEY =
		"allowedUnusedVariableTypeNames";

	private static final String _CHECK_UNUSED_REFERENCE_VARIABLE_DIR_NAMES_KEY =
		"checkUnusedReferenceVariableDirNames";

	private static final String _CHECK_UNUSED_REFERENCE_VARIABLE_KEY =
		"checkUnusedReferenceVariable";

	private static final String _MSG_UNUSED_VARIABLE = "variable.unused";

	private static final String _MSG_UNUSED_VARIABLE_VALUE =
		"variable.value.unused";

}