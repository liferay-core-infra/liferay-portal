/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.sql.dsl.spi.expression;

import com.liferay.petra.sql.dsl.ast.ASTNodeListener;
import com.liferay.petra.sql.dsl.spi.ast.BaseASTNode;

import java.util.function.Consumer;

/**
 * @author Carlos Correa
 */
public class EmptyExpression
	extends BaseASTNode implements DefaultExpression<Void> {

	public static final EmptyExpression INSTANCE = new EmptyExpression();

	@Override
	protected void doToSQL(
		Consumer<String> consumer, ASTNodeListener astNodeListener) {
	}

	private EmptyExpression() {
	}

}