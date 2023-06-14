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

package com.liferay.document.library.preview.pdf.internal.configuration.admin.service;

import com.liferay.document.library.preview.pdf.internal.configuration.PDFPreviewConfiguration;
import com.liferay.document.library.preview.pdf.internal.configuration.admin.service.util.PDFPreviewManagedServiceFactoryHelper;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.GroupConstants;
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
 * @author Alicia García
 */
@Component(
	configurationPid = "com.liferay.document.library.preview.pdf.internal.configuration.PDFPreviewConfiguration",
	property = Constants.SERVICE_PID + "=com.liferay.document.library.preview.pdf.internal.configuration.PDFPreviewConfiguration.scoped",
	service = ManagedServiceFactory.class
)
public class PDFPreviewManagedServiceFactory implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		_unmapPid(pid);
	}

	@Override
	public String getName() {
		return "com.liferay.document.library.preview.pdf.internal." +
			"configuration.PDFPreviewConfiguration.scoped";
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

		long groupId = GetterUtil.getLong(
			dictionary.get("groupId"), GroupConstants.DEFAULT_PARENT_GROUP_ID);

		if (groupId != GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			_updateGroupConfiguration(groupId, pid, dictionary);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_pdfPreviewManagedServiceFactoryHelper.setSystemPDFPreviewConfiguration(
			ConfigurableUtil.createConfigurable(
				PDFPreviewConfiguration.class, properties));
	}

	private void _unmapPid(String pid) {
		Long companyId = _companyIds.remove(pid);

		if (companyId != null) {
			_pdfPreviewManagedServiceFactoryHelper.
				removeCompanyConfigurationBeans(companyId);
		}

		Long groupId = _groupIds.remove(pid);

		if (groupId != null) {
			_pdfPreviewManagedServiceFactoryHelper.
				removeGroupConfigurationBeans(groupId);
		}
	}

	private void _updateCompanyConfiguration(
		long companyId, String pid, Dictionary<String, ?> dictionary) {

		_pdfPreviewManagedServiceFactoryHelper.updateCompanyConfigurationBeans(
			companyId,
			ConfigurableUtil.createConfigurable(
				PDFPreviewConfiguration.class, dictionary));
		_companyIds.put(pid, companyId);
	}

	private void _updateGroupConfiguration(
		long groupId, String pid, Dictionary<String, ?> dictionary) {

		_pdfPreviewManagedServiceFactoryHelper.updateGroupConfigurationBeans(
			groupId,
			ConfigurableUtil.createConfigurable(
				PDFPreviewConfiguration.class, dictionary));
		_groupIds.put(pid, groupId);
	}

	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();
	private final Map<String, Long> _groupIds = new ConcurrentHashMap<>();

	@Reference
	private PDFPreviewManagedServiceFactoryHelper
		_pdfPreviewManagedServiceFactoryHelper;

}