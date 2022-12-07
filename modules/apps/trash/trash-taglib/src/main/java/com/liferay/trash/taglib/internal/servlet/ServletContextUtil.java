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

package com.liferay.trash.taglib.internal.servlet;

import com.liferay.trash.TrashHelper;

import javax.servlet.ServletContext;

/**
 * @author Michael Bradford
 */
public class ServletContextUtil {

	public static ServletContext getServletContext() {
		return _servletContext;
	}

	public static TrashHelper getTrashHelper() {
		return _trashHelper;
	}

	protected static void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	protected static void setTrashHelper(TrashHelper trashHelper) {
		_trashHelper = trashHelper;
	}

	private static ServletContext _servletContext;
	private static TrashHelper _trashHelper;

}