/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.messaging;

import java.io.Serializable;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * @author Dante Wang
 */
public interface DestinationDefinition extends Serializable {

	public static String DESTINATION_TYPE_PARALLEL = "parallel";

	public static String DESTINATION_TYPE_SERIAL = "serial";

	public static String DESTINATION_TYPE_SYNCHRONOUS = "synchronous";

	public String getDestinationName();

	public String getDestinationType();

	public default int getMaximumQueueSize() {
		return Integer.MAX_VALUE;
	}

	public default RejectedExecutionHandler getRejectedExecutionHandler() {
		return null;
	}

	public default int getWorkersCoreSize() {
		return 2;
	}

	public default int getWorkersMaxSize() {
		return 5;
	}

}