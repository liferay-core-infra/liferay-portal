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

package com.liferay.dynamic.data.mapping.taglib.servlet.taglib;

import com.liferay.dynamic.data.mapping.taglib.servlet.taglib.base.BaseHTMLFieldTag;
import com.liferay.osgi.util.service.Snapshot;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

/**
 * @author Sergio González
 */
public class HTMLFieldTag extends BaseHTMLFieldTag {

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(_servletContextSnapshot.get());
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div>");

		return EVAL_PAGE;
	}

	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			HTMLFieldTag.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.dynamic.data.mapping.taglib)");

}