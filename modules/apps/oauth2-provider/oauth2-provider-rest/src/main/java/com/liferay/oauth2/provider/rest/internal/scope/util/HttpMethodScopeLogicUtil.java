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

package com.liferay.oauth2.provider.rest.internal.scope.util;

import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.osgi.util.StringPlus;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Valmir Junior
 */
public class HttpMethodScopeLogicUtil {

	public static boolean check(
		Function<String, Object> propertyAccessorFunction, String requestMethod,
		ScopeChecker scopeChecker) {

		try {
			String applicationName = GetterUtil.getString(
				propertyAccessorFunction.apply("osgi.jaxrs.name"));

			Object ignoreMissingScopesObject = propertyAccessorFunction.apply(
				"ignore.missing.scopes");

			Set<String> ignoreMissingScopes = _ignoreMissingScopes;

			if (ignoreMissingScopesObject != null) {
				ignoreMissingScopes = new HashSet<>(
					StringPlus.asList(ignoreMissingScopesObject));
			}

			ScopeFinder scopeFinder = _bundleContext.getService(
				_getServiceReference(applicationName, ScopeFinder.class));

			Collection<String> scopes = scopeFinder.findScopes();

			if ((!scopes.contains(requestMethod) &&
				 ignoreMissingScopes.contains(requestMethod)) ||
				scopeChecker.checkScope(requestMethod)) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return false;
	}

	private static <T> ServiceReference<? extends T> _getServiceReference(
			String applicationName, Class<? extends T> clazz)
		throws Exception {

		List<ServiceReference<T>> serviceReferences =
			(List<ServiceReference<T>>)_bundleContext.<T>getServiceReferences(
				(Class<T>)clazz, "(osgi.jaxrs.name=" + applicationName + ")");

		if (ListUtil.isNotEmpty(serviceReferences)) {
			return serviceReferences.get(0);
		}

		throw new UnsupportedOperationException(
			"Invalid JAX-RS application " + applicationName);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HttpMethodScopeLogicUtil.class);

	private static BundleContext _bundleContext;
	private static Set<String> _ignoreMissingScopes;

}