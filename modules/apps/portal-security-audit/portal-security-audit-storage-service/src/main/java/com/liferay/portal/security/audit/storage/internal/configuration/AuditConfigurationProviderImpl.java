/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.internal.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.BaseManagedServiceFactory;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.AuditConfigurationProvider;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Christian Moura
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.configuration.AuditConfiguration",
	service = AuditConfigurationProvider.class
)
public class AuditConfigurationProviderImpl
	implements AuditConfigurationProvider {

	@Override
	public boolean isPseudonymizationEnabled(long companyId) {
		if ((companyId == CompanyConstants.SYSTEM) ||
			!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-6417")) {

			return false;
		}

		AuditConfiguration auditConfiguration =
			_auditConfigurations.getOrDefault(
				companyId, _systemAuditConfiguration);

		return auditConfiguration.pseudonymizationEnabled();
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new AuditConfigurationManagedServiceFactory(),
			MapUtil.singletonDictionary(Constants.SERVICE_PID, _FACTORY_PID));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_systemAuditConfiguration = ConfigurableUtil.createConfigurable(
			AuditConfiguration.class, properties);
	}

	private static final String _FACTORY_PID =
		AuditConfiguration.class.getName() + ".scoped";

	private final Map<Long, AuditConfiguration> _auditConfigurations =
		new ConcurrentHashMap<>();
	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;
	private volatile AuditConfiguration _systemAuditConfiguration;

	private class AuditConfigurationManagedServiceFactory
		extends BaseManagedServiceFactory {

		@Override
		public String getName() {
			return _FACTORY_PID;
		}

		@Override
		protected void doDeleted(long companyId, String pid) {
			_auditConfigurations.remove(companyId);
		}

		@Override
		protected void doUpdated(
			long companyId, Dictionary<String, ?> dictionary, String pid) {

			if (companyId == CompanyConstants.SYSTEM) {
				return;
			}

			_auditConfigurations.put(
				companyId,
				ConfigurableUtil.createConfigurable(
					AuditConfiguration.class, dictionary));
		}

	}

}