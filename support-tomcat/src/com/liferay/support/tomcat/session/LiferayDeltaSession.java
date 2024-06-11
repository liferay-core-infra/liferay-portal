/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.support.tomcat.session;

import org.apache.catalina.Manager;
import org.apache.catalina.ha.session.DeltaRequest;
import org.apache.catalina.ha.session.DeltaSession;

/**
 * @author Shuyang Zhou
 */
public class LiferayDeltaSession extends DeltaSession {

	public LiferayDeltaSession() {
	}

	public LiferayDeltaSession(Manager manager) {
		super(manager);
	}

	@Override
	protected DeltaRequest createRequest(
		String sessionId, boolean recordAllActions) {

		return new LiferayDeltaRequest(sessionId, recordAllActions);
	}

}