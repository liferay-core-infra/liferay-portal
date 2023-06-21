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
import com.liferay.portal.cluster.multiple.internal.jgroups.JGroupsClusterChannelFactory;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Hai Yu
 */
@Component(
	configurationPid = "com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration",
	service = {}
)
public class ClusterExecutorConfigurator {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		if (GetterUtil.getBoolean(_props.get(PropsKeys.CLUSTER_LINK_ENABLED))) {
			_bundleContext = bundleContext;

			_jGroupsClusterChannelFactory = new JGroupsClusterChannelFactory(
				_props);

			_jGroupsClusterChannelFactory.start(bundleContext, properties);

			_clusterChannelFactoryServiceRegistration =
				bundleContext.registerService(
					ClusterChannelFactory.class, _jGroupsClusterChannelFactory,
					null);

			_clusterExecutorImpl = new ClusterExecutorImpl(
				_jGroupsClusterChannelFactory, _portalExecutorManager, _props);

			_clusterExecutorImpl.start(bundleContext, properties);

			_clusterExecutorServiceRegistration = bundleContext.registerService(
				ClusterExecutor.class, _clusterExecutorImpl, null);
			_clusterExecutorImplServiceRegistration =
				bundleContext.registerService(
					ClusterExecutorImpl.class, _clusterExecutorImpl, null);
		}
		else {
			_clusterChannelFactoryServiceRegistration =
				bundleContext.registerService(
					ClusterChannelFactory.class,
					ProxyFactory.newDummyInstance(ClusterChannelFactory.class),
					null);
			_clusterExecutorServiceRegistration = bundleContext.registerService(
				ClusterExecutor.class,
				ProxyFactory.newDummyInstance(ClusterExecutor.class), null);
		}
	}

	@Deactivate
	protected void deactivate() {
		_clusterExecutorServiceRegistration.unregister();

		if (_clusterExecutorImpl != null) {
			_clusterExecutorImplServiceRegistration.unregister();

			_clusterExecutorImpl.stop();
		}

		_clusterChannelFactoryServiceRegistration.unregister();

		if (_jGroupsClusterChannelFactory != null) {
			_jGroupsClusterChannelFactory.stop();
		}
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		if (_jGroupsClusterChannelFactory != null) {
			_jGroupsClusterChannelFactory.start(_bundleContext, properties);
		}

		if (_clusterExecutorImpl != null) {
			_clusterExecutorImpl.update(properties);
		}
	}

	private BundleContext _bundleContext;
	private ServiceRegistration<ClusterChannelFactory>
		_clusterChannelFactoryServiceRegistration;
	private ClusterExecutorImpl _clusterExecutorImpl;
	private ServiceRegistration<ClusterExecutorImpl>
		_clusterExecutorImplServiceRegistration;
	private ServiceRegistration<ClusterExecutor>
		_clusterExecutorServiceRegistration;
	private JGroupsClusterChannelFactory _jGroupsClusterChannelFactory;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	@Reference
	private Props _props;

}