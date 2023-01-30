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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

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
		Set<AuditMessageProcessor> globalAuditMessageProcessors =
			_auditMessageProcessors.get(_GLOBAL_AUDIT_MESSAGE_PROCESSORS_KEY);

		if (globalAuditMessageProcessors == null) {
			if (_auditMessageProcessors.isEmpty()) {
				return false;
			}
		}
		else if (globalAuditMessageProcessors.isEmpty() &&
				 (_auditMessageProcessors.size() == 1)) {

			return false;
		}

		return true;
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

		Set<AuditMessageProcessor> globalAuditMessageProcessors =
			_auditMessageProcessors.get(_GLOBAL_AUDIT_MESSAGE_PROCESSORS_KEY);

		if (globalAuditMessageProcessors != null) {
			for (AuditMessageProcessor globalAuditMessageProcessor :
					globalAuditMessageProcessors) {

				globalAuditMessageProcessor.process(auditMessage);
			}
		}

		Set<AuditMessageProcessor> auditMessageProcessors =
			_auditMessageProcessors.get(auditMessage.getEventType());

		if (auditMessageProcessors != null) {
			for (AuditMessageProcessor auditMessageProcessor :
					auditMessageProcessors) {

				auditMessageProcessor.process(auditMessage);
			}
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		AuditConfiguration auditConfiguration =
			ConfigurableUtil.createConfigurable(
				AuditConfiguration.class, properties);

		_auditEnabled = auditConfiguration.enabled();
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void setAuditMessageProcessor(
		AuditMessageProcessor auditMessageProcessor) {

		String[] eventTypes = auditMessageProcessor.getEventTypes();

		if ((eventTypes.length == 1) && eventTypes[0].equals(StringPool.STAR)) {
			Set<AuditMessageProcessor> auditMessageProcessorsSet =
				_auditMessageProcessors.get(
					_GLOBAL_AUDIT_MESSAGE_PROCESSORS_KEY);

			if (auditMessageProcessorsSet == null) {
				auditMessageProcessorsSet = new HashSet<>();

				_auditMessageProcessors.put(
					_GLOBAL_AUDIT_MESSAGE_PROCESSORS_KEY,
					auditMessageProcessorsSet);
			}

			auditMessageProcessorsSet.add(auditMessageProcessor);

			return;
		}

		for (String eventType : eventTypes) {
			Set<AuditMessageProcessor> auditMessageProcessorsSet =
				_auditMessageProcessors.get(eventType);

			if (auditMessageProcessorsSet == null) {
				auditMessageProcessorsSet = new HashSet<>();

				_auditMessageProcessors.put(
					eventType, auditMessageProcessorsSet);
			}

			auditMessageProcessorsSet.add(auditMessageProcessor);
		}
	}

	protected void unsetAuditMessageProcessor(
		AuditMessageProcessor auditMessageProcessor) {

		String[] eventTypes = auditMessageProcessor.getEventTypes();

		if ((eventTypes.length == 1) && eventTypes[0].equals(StringPool.STAR)) {
			Set<AuditMessageProcessor> auditMessageProcessorsSet =
				_auditMessageProcessors.get(
					_GLOBAL_AUDIT_MESSAGE_PROCESSORS_KEY);

			if (auditMessageProcessorsSet == null) {
				return;
			}

			auditMessageProcessorsSet.remove(auditMessageProcessor);

			return;
		}

		for (String eventType : eventTypes) {
			Set<AuditMessageProcessor> auditMessageProcessorsSet =
				_auditMessageProcessors.get(eventType);

			if (auditMessageProcessorsSet == null) {
				continue;
			}

			auditMessageProcessorsSet.remove(auditMessageProcessor);
		}
	}

	private static final String _GLOBAL_AUDIT_MESSAGE_PROCESSORS_KEY =
		"globalAuditMessageProcessors";

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultAuditRouter.class);

	private volatile boolean _auditEnabled;
	private final Map<String, Set<AuditMessageProcessor>>
		_auditMessageProcessors = new ConcurrentHashMap<>();

}