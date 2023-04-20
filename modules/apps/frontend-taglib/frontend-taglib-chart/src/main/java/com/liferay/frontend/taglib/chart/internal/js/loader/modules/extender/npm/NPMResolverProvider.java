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

package com.liferay.frontend.taglib.chart.internal.js.loader.modules.extender.npm;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.osgi.util.service.Snapshot;

/**
 * @author Iván Zaera Avellón
 */
public class NPMResolverProvider {

	public static NPMResolver getNPMResolver() {
		if (_npmResolverProvider == null) {
			return null;
		}

		return _npmResolverSnapshot.get();
	}

	public NPMResolverProvider() {
		_npmResolverProvider = this;
	}

	private static NPMResolverProvider _npmResolverProvider;
	private static final Snapshot<NPMResolver> _npmResolverSnapshot =
		new Snapshot<>(NPMResolverProvider.class, NPMResolver.class);

}