/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.internal.jmx;

import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.DestinationStatistics;
import com.liferay.portal.kernel.messaging.MessageBus;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
public class DestinationStatisticsManager
	extends StandardMBean implements DestinationStatisticsManagerMBean {

	public DestinationStatisticsManager(
			DestinationDefinition destinationDefinition, MessageBus messageBus)
		throws NotCompliantMBeanException {

		super(DestinationStatisticsManagerMBean.class);

		_destinationDefinition = destinationDefinition;
		_messageBus = messageBus;
	}

	@Override
	public int getActiveThreadCount() {
		if (_autoRefresh) {
			refresh();
		}

		return _destinationStatistics.getActiveThreadCount();
	}

	@Override
	public int getCurrentThreadCount() {
		if (_autoRefresh || (_destinationStatistics == null)) {
			refresh();
		}

		return _destinationStatistics.getCurrentThreadCount();
	}

	@Override
	public int getLargestThreadCount() {
		if (_autoRefresh || (_destinationStatistics == null)) {
			refresh();
		}

		return _destinationStatistics.getLargestThreadCount();
	}

	@Override
	public String getLastRefresh() {
		return String.valueOf(_lastRefresh);
	}

	@Override
	public int getMaxThreadPoolSize() {
		if (_autoRefresh || (_destinationStatistics == null)) {
			refresh();
		}

		return _destinationStatistics.getMaxThreadPoolSize();
	}

	@Override
	public int getMinThreadPoolSize() {
		if (_autoRefresh || (_destinationStatistics == null)) {
			refresh();
		}

		return _destinationStatistics.getMinThreadPoolSize();
	}

	public String getObjectName() {
		return _OBJECT_NAME_PREFIX +
			_destinationDefinition.getDestinationName();
	}

	public String getObjectNameCacheKey() {
		return _OBJECT_NAME_CACHE_KEY_PREFIX +
			_destinationDefinition.getDestinationName();
	}

	@Override
	public long getPendingMessageCount() {
		if (_autoRefresh || (_destinationStatistics == null)) {
			refresh();
		}

		return _destinationStatistics.getPendingMessageCount();
	}

	@Override
	public long getRejectedMessageCount() {
		if (_autoRefresh || (_destinationStatistics == null)) {
			refresh();
		}

		return _destinationStatistics.getRejectedMessageCount();
	}

	@Override
	public long getSentMessageCount() {
		if (_autoRefresh || (_destinationStatistics == null)) {
			refresh();
		}

		return _destinationStatistics.getSentMessageCount();
	}

	@Override
	public boolean isAutoRefresh() {
		return _autoRefresh;
	}

	@Override
	public void refresh() {
		if (System.currentTimeMillis() > _lastRefresh) {
			_lastRefresh = System.currentTimeMillis();

			Destination destination = _messageBus.getDestination(
				_destinationDefinition.getDestinationName());

			if (destination == null) {
				_destinationStatistics = _emptyDestinationStatistics;

				return;
			}

			_destinationStatistics = destination.getDestinationStatistics();
		}
	}

	@Override
	public void setAutoRefresh(boolean autoRefresh) {
		_autoRefresh = autoRefresh;
	}

	private static final String _OBJECT_NAME_CACHE_KEY_PREFIX =
		"MessagingDestinationStatistics-";

	private static final String _OBJECT_NAME_PREFIX =
		"com.liferay.portal.messaging:classification=messaging_destination," +
			"name=MessagingDestinationStatistics-";

	private static final DestinationStatistics _emptyDestinationStatistics =
		new DestinationStatistics();

	private boolean _autoRefresh;
	private final DestinationDefinition _destinationDefinition;
	private DestinationStatistics _destinationStatistics;
	private long _lastRefresh;
	private final MessageBus _messageBus;

}