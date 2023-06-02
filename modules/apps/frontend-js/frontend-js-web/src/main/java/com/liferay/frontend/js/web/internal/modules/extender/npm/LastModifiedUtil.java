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

package com.liferay.frontend.js.web.internal.modules.extender.npm;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Joao Victor Alves
 */
public class LastModifiedUtil {

	public static void accumulateAndGetLastModified(long lastModified) {
		_lastModified.accumulateAndGet(lastModified, Math::max);
	}

	public static long getLastModified() {
		return _lastModified.get();
	}

	public static void setLastModified(long lastModified) {
		_lastModified.set(lastModified);
	}

	private static final AtomicLong _lastModified = new AtomicLong();

}