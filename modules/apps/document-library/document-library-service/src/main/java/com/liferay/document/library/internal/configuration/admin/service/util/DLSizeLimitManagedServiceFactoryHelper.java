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

package com.liferay.document.library.internal.configuration.admin.service.util;

import com.liferay.document.library.internal.configuration.DLSizeLimitConfiguration;
import com.liferay.document.library.internal.util.MimeTypeSizeLimitUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Validator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(service = DLSizeLimitManagedServiceFactoryHelper.class)
public class DLSizeLimitManagedServiceFactoryHelper {

	public void clearGroupConfigurationBeans() {
		_groupConfigurationBeans.clear();
	}

	public void clearGroupMimeTypeSizeLimitsMap() {
		_groupMimeTypeSizeLimitsMap.clear();
	}

	public long getCompanyFileMaxSize(long companyId) {
		DLSizeLimitConfiguration dlSizeLimitConfiguration =
			_getCompanyDLSizeLimitConfiguration(companyId);

		return dlSizeLimitConfiguration.fileMaxSize();
	}

	public Map<String, Long> getCompanyMimeTypeSizeLimit(long companyId) {
		return _companyMimeTypeSizeLimitsMap.computeIfAbsent(
			companyId, this::_computeCompanyMimeTypeSizeLimit);
	}

	public long getCompanyMimeTypeSizeLimit(long companyId, String mimeType) {
		if (Validator.isNull(mimeType)) {
			return 0;
		}

		Map<String, Long> map = _companyMimeTypeSizeLimitsMap.computeIfAbsent(
			companyId, this::_computeCompanyMimeTypeSizeLimit);

		long sizeLimit = map.getOrDefault(mimeType, 0L);

		if (sizeLimit != 0) {
			return sizeLimit;
		}

		List<String> parts = StringUtil.split(mimeType, CharPool.SLASH);

		return map.getOrDefault(String.format("%s/*", parts.get(0)), 0L);
	}

	public long getGroupFileMaxSize(long groupId) {
		DLSizeLimitConfiguration dlSizeLimitConfiguration =
			_getGroupDLSizeLimitConfiguration(groupId);

		return dlSizeLimitConfiguration.fileMaxSize();
	}

	public Map<String, Long> getGroupMimeTypeSizeLimit(long groupId) {
		return _groupMimeTypeSizeLimitsMap.computeIfAbsent(
			groupId, this::_computeGroupMimeTypeSizeLimit);
	}

	public long getGroupMimeTypeSizeLimit(long groupId, String mimeType) {
		if (Validator.isNull(mimeType)) {
			return 0;
		}

		Map<String, Long> map = _groupMimeTypeSizeLimitsMap.computeIfAbsent(
			groupId, this::_computeGroupMimeTypeSizeLimit);

		long sizeLimit = map.getOrDefault(mimeType, 0L);

		if (sizeLimit != 0) {
			return sizeLimit;
		}

		List<String> parts = StringUtil.split(mimeType, CharPool.SLASH);

		return map.getOrDefault(String.format("%s/*", parts.get(0)), 0L);
	}

	public long getSystemFileMaxSize() {
		return _systemDLSizeLimitConfiguration.fileMaxSize();
	}

	public Map<String, Long> getSystemMimeTypeSizeLimit() {
		return _computeMimeTypeSizeLimit(_systemDLSizeLimitConfiguration);
	}

	public void removeCompanyConfigurationBeans(long companyId) {
		_companyConfigurationBeans.remove(companyId);
	}

	public void removeCompanyMimeTypeSizeLimitsMap(long companyId) {
		_companyMimeTypeSizeLimitsMap.remove(companyId);
	}

	public void removeGroupConfigurationBeans(long groupId) {
		_groupConfigurationBeans.remove(groupId);
	}

	public void removeGroupMimeTypeSizeLimitsMap(long groupId) {
		_groupMimeTypeSizeLimitsMap.remove(groupId);
	}

	public void setCompanyMimeTypeSizeLimitsMap(
		Map<Long, Map<String, Long>> companyMimeTypeSizeLimitsMap) {

		_companyMimeTypeSizeLimitsMap = companyMimeTypeSizeLimitsMap;
	}

	public void setGroupMimeTypeSizeLimitsMap(
		Map<Long, Map<String, Long>> groupMimeTypeSizeLimitsMap) {

		_groupMimeTypeSizeLimitsMap = groupMimeTypeSizeLimitsMap;
	}

	public void setSystemDLSizeLimitConfiguration(
		DLSizeLimitConfiguration systemDLSizeLimitConfiguration) {

		_systemDLSizeLimitConfiguration = systemDLSizeLimitConfiguration;
	}

	public void updateCompanyConfigurationBeans(
		long companyId, DLSizeLimitConfiguration dlSizeLimitConfiguration) {

		_companyConfigurationBeans.put(companyId, dlSizeLimitConfiguration);
	}

	public void updateGroupConfigurationBeans(
		DLSizeLimitConfiguration dlSizeLimitConfiguration, long groupId) {

		_groupConfigurationBeans.put(groupId, dlSizeLimitConfiguration);
	}

	private Map<String, Long> _computeCompanyMimeTypeSizeLimit(long companyId) {
		return _computeMimeTypeSizeLimit(
			_getCompanyDLSizeLimitConfiguration(companyId));
	}

	private Map<String, Long> _computeGroupMimeTypeSizeLimit(long groupId) {
		return _computeMimeTypeSizeLimit(
			_getGroupDLSizeLimitConfiguration(groupId));
	}

	private Map<String, Long> _computeMimeTypeSizeLimit(
		DLSizeLimitConfiguration dlSizeLimitConfiguration) {

		Map<String, Long> mimeTypeSizeLimits = new LinkedHashMap<>();

		for (String mimeTypeSizeLimit :
				dlSizeLimitConfiguration.mimeTypeSizeLimit()) {

			MimeTypeSizeLimitUtil.parseMimeTypeSizeLimit(
				mimeTypeSizeLimit, mimeTypeSizeLimits::put);
		}

		return mimeTypeSizeLimits;
	}

	private DLSizeLimitConfiguration _getCompanyDLSizeLimitConfiguration(
		long companyId) {

		return _getDLSizeLimitConfiguration(
			companyId, _companyConfigurationBeans,
			() -> _systemDLSizeLimitConfiguration);
	}

	private DLSizeLimitConfiguration _getDLSizeLimitConfiguration(
		long key, Map<Long, DLSizeLimitConfiguration> configurationBeans,
		Supplier<DLSizeLimitConfiguration> supplier) {

		if (configurationBeans.containsKey(key)) {
			return configurationBeans.get(key);
		}

		return supplier.get();
	}

	private DLSizeLimitConfiguration _getGroupDLSizeLimitConfiguration(
		long groupId) {

		return _getDLSizeLimitConfiguration(
			groupId, _groupConfigurationBeans,
			() -> {
				Group group = _groupLocalService.fetchGroup(groupId);

				long companyId = CompanyThreadLocal.getCompanyId();

				if (group != null) {
					companyId = group.getCompanyId();
				}

				return _getCompanyDLSizeLimitConfiguration(companyId);
			});
	}

	private final Map<Long, DLSizeLimitConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();
	private volatile Map<Long, Map<String, Long>> _companyMimeTypeSizeLimitsMap;
	private final Map<Long, DLSizeLimitConfiguration> _groupConfigurationBeans =
		new ConcurrentHashMap<>();

	@Reference
	private GroupLocalService _groupLocalService;

	private volatile Map<Long, Map<String, Long>> _groupMimeTypeSizeLimitsMap;
	private volatile DLSizeLimitConfiguration _systemDLSizeLimitConfiguration;

}