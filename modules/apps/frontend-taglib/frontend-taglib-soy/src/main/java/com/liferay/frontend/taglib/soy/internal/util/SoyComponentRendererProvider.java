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

package com.liferay.frontend.taglib.soy.internal.util;

import com.liferay.frontend.taglib.soy.internal.template.SoyComponentRenderer;
import com.liferay.osgi.util.service.Snapshot;

/**
 * @author Iván Zaera Avellón
 */
public class SoyComponentRendererProvider {

	public static SoyComponentRenderer getSoyComponentRenderer() {
		if (_soyRendererProvider == null) {
			return null;
		}

		return _soyComponentRendererSnapshot.get();
	}

	public SoyComponentRendererProvider() {
		_soyRendererProvider = this;
	}

	private static final Snapshot<SoyComponentRenderer>
		_soyComponentRendererSnapshot = new Snapshot<>(
			SoyComponentRendererProvider.class, SoyComponentRenderer.class);
	private static SoyComponentRendererProvider _soyRendererProvider;

}