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

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.cluster.ClusterLink;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Hai Yu
 */
@Component(service = {})
public class ClusterLinkConfigurator {

	@Activate
	protected void activate(BundleContext bundleContext) {
		if (GetterUtil.getBoolean(_props.get(PropsKeys.CLUSTER_LINK_ENABLED))) {
			_clusterLinkImpl = new ClusterLinkImpl(
				_clusterChannelFactory, _messageBus, _portalExecutorManager,
				_props);

			_clusterLinkImpl.start();

			_serviceRegistration = bundleContext.registerService(
				ClusterLink.class, _clusterLinkImpl, null);
		}
		else {
			_serviceRegistration = bundleContext.registerService(
				ClusterLink.class,
				ProxyFactory.newDummyInstance(ClusterLink.class), null);
		}
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();

		if (_clusterLinkImpl != null) {
			_clusterLinkImpl.stop();
		}
	}

	@Reference
	private ClusterChannelFactory _clusterChannelFactory;

	private ClusterLinkImpl _clusterLinkImpl;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	@Reference
	private Props _props;

	private ServiceRegistration<ClusterLink> _serviceRegistration;

}