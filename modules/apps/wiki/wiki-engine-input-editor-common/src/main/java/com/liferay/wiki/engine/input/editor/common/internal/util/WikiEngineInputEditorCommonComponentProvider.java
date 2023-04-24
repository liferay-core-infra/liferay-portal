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

package com.liferay.wiki.engine.input.editor.common.internal.util;

import com.liferay.osgi.util.service.Snapshot;

import javax.servlet.ServletContext;

/**
 * @author Iván Zaera
 */
public class WikiEngineInputEditorCommonComponentProvider {

	public static WikiEngineInputEditorCommonComponentProvider
		getWikiEngineInputEditorCommonComponentProvider() {

		return _wikiEngineInputEditorCommonComponentProvider;
	}

	public ServletContext getServletContext() {
		return _servletContextSnapshot.get();
	}

	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			WikiEngineInputEditorCommonComponentProvider.class,
			ServletContext.class);
	private static WikiEngineInputEditorCommonComponentProvider
		_wikiEngineInputEditorCommonComponentProvider;

}