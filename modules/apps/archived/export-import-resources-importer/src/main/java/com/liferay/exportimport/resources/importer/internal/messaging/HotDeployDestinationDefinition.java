/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.messaging;

import com.liferay.portal.kernel.deploy.hot.HotDeployListener;
import com.liferay.portal.kernel.deploy.hot.HotDeployUtil;
import com.liferay.portal.kernel.messaging.DestinationDefinition;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = "destination.name=" + DestinationNames.HOT_DEPLOY,
	service = DestinationDefinition.class
)
public class HotDeployDestinationDefinition implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return DestinationNames.HOT_DEPLOY;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SERIAL;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		HotDeployUtil.registerListener(_hotDeployListener);
	}

	@Deactivate
	protected void deactivate() {
		HotDeployUtil.unregisterListener(_hotDeployListener);
	}

	private final HotDeployListener _hotDeployListener =
		new MessagingHotDeployListener();

}