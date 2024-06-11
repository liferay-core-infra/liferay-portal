/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.support.tomcat.session;

import org.apache.catalina.Session;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.session.DeltaManager;
import org.apache.catalina.ha.session.DeltaSession;

/**
 * @author Shuyang Zhou
 */
public class LiferayDeltaManager extends DeltaManager {

	@Override
	public ClusterManager cloneFromTemplate() {
		LiferayDeltaManager liferayDeltaManager = new LiferayDeltaManager();

		clone(liferayDeltaManager);

		liferayDeltaManager.setExpireSessionsOnShutdown(
			isExpireSessionsOnShutdown());
		liferayDeltaManager.setNotifySessionListenersOnReplication(
			isNotifyContainerListenersOnReplication());
		liferayDeltaManager.setNotifyContainerListenersOnReplication(
			isNotifyContainerListenersOnReplication());
		liferayDeltaManager.setStateTransferTimeout(getStateTransferTimeout());
		liferayDeltaManager.setSendAllSessions(isSendAllSessions());
		liferayDeltaManager.setSendAllSessionsSize(getSendAllSessionsSize());
		liferayDeltaManager.setSendAllSessionsWaitTime(
			getSendAllSessionsWaitTime());
		liferayDeltaManager.setStateTimestampDrop(isStateTimestampDrop());

		return liferayDeltaManager;
	}

	@Override
	public Session createEmptySession() {
		return new LiferayDeltaSession(this);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected DeltaSession getNewDeltaSession() {
		return new LiferayDeltaSession(this);
	}

}