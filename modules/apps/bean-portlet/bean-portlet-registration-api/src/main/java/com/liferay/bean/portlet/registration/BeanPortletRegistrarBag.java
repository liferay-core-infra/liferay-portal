/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration;

import com.liferay.bean.portlet.extension.BeanFilterMethodFactory;
import com.liferay.bean.portlet.extension.BeanFilterMethodInvoker;
import com.liferay.bean.portlet.extension.BeanPortletMethodFactory;
import com.liferay.bean.portlet.extension.BeanPortletMethodInvoker;

import java.util.Set;

import javax.servlet.ServletContext;

/**
 * @author Jiaxu Wei
 */
public class BeanPortletRegistrarBag {

	public BeanPortletRegistrarBag(
		BeanFilterMethodFactory beanFilterMethodFactory,
		BeanFilterMethodInvoker beanFilterMethodInvoker,
		BeanPortletMethodFactory beanPortletMethodFactory,
		BeanPortletMethodInvoker beanPortletMethodInvoker,
		Set<Class<?>> discoveredClasses, ServletContext servletContext) {

		_beanFilterMethodFactory = beanFilterMethodFactory;
		_beanFilterMethodInvoker = beanFilterMethodInvoker;
		_beanPortletMethodFactory = beanPortletMethodFactory;
		_beanPortletMethodInvoker = beanPortletMethodInvoker;
		_discoveredClasses = discoveredClasses;
		_servletContext = servletContext;
	}

	public BeanFilterMethodFactory getBeanFilterMethodFactory() {
		return _beanFilterMethodFactory;
	}

	public BeanFilterMethodInvoker getBeanFilterMethodInvoker() {
		return _beanFilterMethodInvoker;
	}

	public BeanPortletMethodFactory getBeanPortletMethodFactory() {
		return _beanPortletMethodFactory;
	}

	public BeanPortletMethodInvoker getBeanPortletMethodInvoker() {
		return _beanPortletMethodInvoker;
	}

	public Set<Class<?>> getDiscoveredClasses() {
		return _discoveredClasses;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	private final BeanFilterMethodFactory _beanFilterMethodFactory;
	private final BeanFilterMethodInvoker _beanFilterMethodInvoker;
	private final BeanPortletMethodFactory _beanPortletMethodFactory;
	private final BeanPortletMethodInvoker _beanPortletMethodInvoker;
	private final Set<Class<?>> _discoveredClasses;
	private final ServletContext _servletContext;

}