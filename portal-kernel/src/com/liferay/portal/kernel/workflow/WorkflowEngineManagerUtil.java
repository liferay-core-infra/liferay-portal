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

package com.liferay.portal.kernel.workflow;

import java.util.Collections;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class WorkflowEngineManagerUtil {

	public static String getKey() {
		return _KEY;
	}

	public static String getName() {
		return _NAME;
	}

	public static Map<String, Object> getOptionalAttributes() {
		return _optionalAttributes;
	}

	public static String getVersion() {
		return _VERSION;
	}

	public static boolean isDeployed() {
		return true;
	}

	private static final String _KEY = "liferay";

	private static final String _NAME = "Liferay Kaleo Workflow Engine";

	private static final String _VERSION = "6.0.0";

	private static final Map<String, Object> _optionalAttributes =
		Collections.emptyMap();

}