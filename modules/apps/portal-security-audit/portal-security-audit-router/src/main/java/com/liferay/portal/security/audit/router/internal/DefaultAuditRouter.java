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

package com.liferay.portal.security.audit.router.internal;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 * @author Brian Greenwald
 * @author Prathima Shreenath
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.configuration.AuditConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL,
	service = AuditRouter.class
)
public class DefaultAuditRouter implements AuditRouter {

	@Override
	public boolean isDeployed() {
		int auditMessageProcessorsCount = _serviceTrackerList.size();

		if (auditMessageProcessorsCount > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void route(AuditMessage auditMessage) throws AuditException {
		if (!_auditEnabled) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Audit disabled, not processing message: " + auditMessage);
			}

			return;
		}

		for (AuditMessageProcessor globalAuditMessageProcessor :
				_serviceTrackerList) {

			globalAuditMessageProcessor.process(auditMessage);
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, AuditMessageProcessor.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		AuditConfiguration auditConfiguration =
			ConfigurableUtil.createConfigurable(
				AuditConfiguration.class, properties);

		_auditEnabled = auditConfiguration.enabled();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultAuditRouter.class);

	private volatile boolean _auditEnabled;
	private ServiceTrackerList<AuditMessageProcessor> _serviceTrackerList;

}