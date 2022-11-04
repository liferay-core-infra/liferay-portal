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

package com.liferay.portal.workflow.kaleo.runtime.internal.node;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.workflow.kaleo.definition.NodeTypeDependentObjectRegistry;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.runtime.node.NodeExecutor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(service = NodeExecutorFactory.class)
public class NodeExecutorFactory {

	public NodeExecutor getNodeExecutor(String nodeTypeString)
		throws KaleoDefinitionValidationException {

		return _nodeExecutors.getNodeTypeDependentObjects(nodeTypeString);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, NodeExecutor.class,
			new ServiceTrackerCustomizer<NodeExecutor, NodeExecutor>() {

				@Override
				public NodeExecutor addingService(
					ServiceReference<NodeExecutor> serviceReference) {

					NodeExecutor nodeExecutor = bundleContext.getService(
						serviceReference);

					String nodeType = (String)serviceReference.getProperty(
						"node.type");

					if (nodeType == null) {
						throw new IllegalArgumentException(
							"The property \"node.type\" is null");
					}

					_nodeExecutors.addNodeTypeDependentObject(
						nodeType, nodeExecutor);

					return nodeExecutor;
				}

				@Override
				public void modifiedService(
					ServiceReference<NodeExecutor> serviceReference,
					NodeExecutor service) {
				}

				@Override
				public void removedService(
					ServiceReference<NodeExecutor> serviceReference,
					NodeExecutor service) {

					String nodeType = (String)serviceReference.getProperty(
						"node.type");

					if (nodeType == null) {
						throw new IllegalArgumentException(
							"The property \"node.type\" is null");
					}

					_nodeExecutors.removeNodeTypeDependentObjects(nodeType);

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	private final NodeTypeDependentObjectRegistry<NodeExecutor> _nodeExecutors =
		new NodeTypeDependentObjectRegistry<>();
	private ServiceTracker<NodeExecutor, NodeExecutor> _serviceTracker;

}