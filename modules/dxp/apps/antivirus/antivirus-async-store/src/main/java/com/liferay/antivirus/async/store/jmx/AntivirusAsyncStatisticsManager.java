/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.antivirus.async.store.jmx;

import com.liferay.antivirus.async.store.constants.AntivirusAsyncDestinationNames;
import com.liferay.antivirus.async.store.event.AntivirusAsyncEvent;
import com.liferay.antivirus.async.store.event.AntivirusAsyncEventListener;
import com.liferay.antivirus.async.store.retry.AntivirusAsyncRetryScheduler;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationStatistics;
import com.liferay.portal.kernel.messaging.Message;

import java.util.concurrent.atomic.AtomicLong;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	property = {
		"destination.target=(destination.name=" + AntivirusAsyncDestinationNames.ANTIVIRUS + ")",
		"jmx.objectname=com.liferay.antivirus:classification=antivirus_async,name=AntivirusAsyncStatistics",
		"jmx.objectname.cache.key=AntivirusAsyncStatistics"
	},
	service = {
		AntivirusAsyncEventListener.class,
		AntivirusAsyncStatisticsManagerMBean.class, DynamicMBean.class
	}
)
public class AntivirusAsyncStatisticsManager
	extends StandardMBean
	implements AntivirusAsyncEventListener,
			   AntivirusAsyncStatisticsManagerMBean {

	@Activate
	public AntivirusAsyncStatisticsManager(
			@Reference(name = "destination") Destination destination)
		throws NotCompliantMBeanException {

		super(AntivirusAsyncStatisticsManagerMBean.class);

		_destination = destination;
	}

	@Override
	public int getActiveScanCount() {
		if (_autoRefresh || (_destinationStatistics == null)) {
			refresh();
		}

		return _destinationStatistics.getActiveThreadCount();
	}

	@Override
	public String getLastRefresh() {
		return String.valueOf(_lastRefresh);
	}

	@Override
	public long getPendingScanCount() {
		if (_autoRefresh || (_destinationStatistics == null)) {
			refresh();
		}

		return _destinationStatistics.getPendingMessageCount() +
			_antivirusAsyncRetryScheduler.getPendingMessageCount();
	}

	@Override
	public long getProcessingErrorCount() {
		return _processingErrorCounter.get();
	}

	@Override
	public long getSizeExceededCount() {
		return _sizeExceededCounter.get();
	}

	@Override
	public long getTotalScannedCount() {
		return _totalScannedCounter.get();
	}

	@Override
	public long getVirusFoundCount() {
		return _virusFoundCounter.get();
	}

	@Override
	public boolean isAutoRefresh() {
		return _autoRefresh;
	}

	@Override
	public void receive(Message message) {
		AntivirusAsyncEvent antivirusAsyncEvent =
			(AntivirusAsyncEvent)message.get("antivirusAsyncEvent");

		if (antivirusAsyncEvent == AntivirusAsyncEvent.PROCESSING_ERROR) {
			_processingErrorCounter.incrementAndGet();
		}
		else if (antivirusAsyncEvent == AntivirusAsyncEvent.SIZE_EXCEEDED) {
			_sizeExceededCounter.incrementAndGet();
		}
		else if (antivirusAsyncEvent == AntivirusAsyncEvent.SUCCESS) {
			_totalScannedCounter.incrementAndGet();
		}
		else if (antivirusAsyncEvent == AntivirusAsyncEvent.VIRUS_FOUND) {
			_totalScannedCounter.incrementAndGet();
			_virusFoundCounter.incrementAndGet();
		}
	}

	@Override
	public void refresh() {
		if (System.currentTimeMillis() > _lastRefresh) {
			_destinationStatistics = _destination.getDestinationStatistics();
			_lastRefresh = System.currentTimeMillis();
			_processingErrorCounter.set(0);
			_sizeExceededCounter.set(0);
			_totalScannedCounter.set(0);
			_virusFoundCounter.set(0);
		}
	}

	@Override
	public void setAutoRefresh(boolean autoRefresh) {
		_autoRefresh = autoRefresh;
	}

	@Reference
	private AntivirusAsyncRetryScheduler _antivirusAsyncRetryScheduler;

	private boolean _autoRefresh;
	private final Destination _destination;
	private DestinationStatistics _destinationStatistics;
	private long _lastRefresh;
	private final AtomicLong _processingErrorCounter = new AtomicLong();
	private final AtomicLong _sizeExceededCounter = new AtomicLong();
	private final AtomicLong _totalScannedCounter = new AtomicLong();
	private final AtomicLong _virusFoundCounter = new AtomicLong();

}