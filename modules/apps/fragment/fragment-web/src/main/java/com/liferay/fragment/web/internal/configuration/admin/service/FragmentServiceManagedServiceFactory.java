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

package com.liferay.fragment.web.internal.configuration.admin.service;

import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.web.internal.configuration.admin.service.util.FragmentServiceManagedServiceFactoryHelper;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.fragment.configuration.FragmentServiceConfiguration",
	property = Constants.SERVICE_PID + "=com.liferay.fragment.configuration.FragmentServiceConfiguration.scoped",
	service = ManagedServiceFactory.class
)
public class FragmentServiceManagedServiceFactory
	implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		_unmapPid(pid);
	}

	@Override
	public String getName() {
		return "com.liferay.fragment.configuration." +
			"FragmentServiceConfiguration.scoped";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> dictionary)
		throws ConfigurationException {

		_unmapPid(pid);

		long companyId = GetterUtil.getLong(
			dictionary.get("companyId"), CompanyConstants.SYSTEM);

		if (companyId != CompanyConstants.SYSTEM) {
			_updateCompanyConfiguration(companyId, pid, dictionary);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_fragmentServiceManagedServiceFactoryHelper.
			setSystemFragmentServiceConfiguration(
				ConfigurableUtil.createConfigurable(
					FragmentServiceConfiguration.class, properties));
	}

	private void _unmapPid(String pid) {
		Long companyId = _companyIds.remove(pid);

		if (companyId != null) {
			_fragmentServiceManagedServiceFactoryHelper.
				removeCompanyConfigurationBeans(companyId);
		}
	}

	private void _updateCompanyConfiguration(
		long companyId, String pid, Dictionary<String, ?> dictionary) {

		_fragmentServiceManagedServiceFactoryHelper.
			putCompanyConfigurationBeans(
				companyId,
				ConfigurableUtil.createConfigurable(
					FragmentServiceConfiguration.class, dictionary));
		_companyIds.put(pid, companyId);
	}

	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();

	@Reference
	private FragmentServiceManagedServiceFactoryHelper
		_fragmentServiceManagedServiceFactoryHelper;

}