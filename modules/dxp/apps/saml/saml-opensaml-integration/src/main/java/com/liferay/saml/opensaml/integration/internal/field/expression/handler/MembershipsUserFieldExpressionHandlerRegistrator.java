/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.field.expression.handler;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.saml.opensaml.integration.field.expression.handler.UserFieldExpressionHandler;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(service = {})
public class MembershipsUserFieldExpressionHandlerRegistrator {

	@Activate
	protected void activate(BundleContext bundleContext) {
		if (!FeatureFlagManagerUtil.isEnabled("LPS-180198")) {
			return;
		}

		_serviceRegistration = bundleContext.registerService(
			UserFieldExpressionHandler.class,
			new MembershipsUserFieldExpressionHandler(_userGroupLocalService),
			HashMapDictionaryBuilder.<String, Object>put(
				"display.index:Integer", 200
			).put(
				"prefix", "membership"
			).build());
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private ServiceRegistration<?> _serviceRegistration;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}