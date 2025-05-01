/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scripting.executor.internal.messaging;

import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.scripting.Scripting;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.scripting.executor.internal.constants.ScriptingExecutorMessagingConstants;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "destination.name=" + ScriptingExecutorMessagingConstants.DESTINATION_NAME,
	service = DestinationDefinition.class
)
public class ScriptingExecutorMessagingConfigurator
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return ScriptingExecutorMessagingConstants.DESTINATION_NAME;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_PARALLEL;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			MessageListener.class,
			new ScriptingExecutorMessageListener(_scripting),
			HashMapDictionaryBuilder.<String, Object>put(
				"destination.name",
				ScriptingExecutorMessagingConstants.DESTINATION_NAME
			).build());
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Reference
	private Scripting _scripting;

	private ServiceRegistration<MessageListener> _serviceRegistration;

}