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

package com.liferay.bean.portlet.registration;

import com.liferay.bean.portlet.extension.BeanFilterMethodFactory;
import com.liferay.bean.portlet.extension.BeanFilterMethodInvoker;
import com.liferay.bean.portlet.extension.BeanPortletMethodFactory;
import com.liferay.bean.portlet.extension.BeanPortletMethodInvoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.osgi.framework.ServiceRegistration;

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

	public void addServiceRegistration(
		ServiceRegistration<?> serviceRegistration) {

		_serviceRegistrations.add(serviceRegistration);
	}

	public void addServiceRegistrations(
		List<ServiceRegistration<?>> serviceRegistrations) {

		_serviceRegistrations.addAll(serviceRegistrations);
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

	public List<ServiceRegistration<?>> getServiceRegistrations() {
		return _serviceRegistrations;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	private final BeanFilterMethodFactory _beanFilterMethodFactory;
	private final BeanFilterMethodInvoker _beanFilterMethodInvoker;
	private final BeanPortletMethodFactory _beanPortletMethodFactory;
	private final BeanPortletMethodInvoker _beanPortletMethodInvoker;
	private final Set<Class<?>> _discoveredClasses;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();
	private final ServletContext _servletContext;

}