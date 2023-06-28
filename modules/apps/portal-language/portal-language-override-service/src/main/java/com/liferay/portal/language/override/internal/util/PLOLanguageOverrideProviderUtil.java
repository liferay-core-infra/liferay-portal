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

package com.liferay.portal.language.override.internal.util;

import com.liferay.osgi.util.service.Snapshot;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalServiceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author Regisson Cesar
 */
public class PLOLanguageOverrideProviderUtil {

	public static void add(PLOEntry ploEntry) {
		_add(_ploEntriesMapDCLSingleton.getSingleton(_supplier), ploEntry);
	}

	public static String encodeKey(long companyId, String languageId) {
		return StringBundler.concat(companyId, StringPool.POUND, languageId);
	}

	public static DCLSingleton<Map<String, HashMap<String, String>>>
		getPloEntriesMapDCLSingleton() {

		return _ploEntriesMapDCLSingleton;
	}

	public static Supplier<Map<String, HashMap<String, String>>> getSupplier() {
		return _supplier;
	}

	public static void remove(PLOEntry ploEntry) {
		Map<String, HashMap<String, String>> ploEntriesMap =
			_ploEntriesMapDCLSingleton.getSingleton(_supplier);

		ploEntriesMap.computeIfPresent(
			encodeKey(ploEntry.getCompanyId(), ploEntry.getLanguageId()),
			(key, value) -> {
				value.remove(ploEntry.getKey());

				if (value.isEmpty()) {
					return null;
				}

				return value;
			});
	}

	public static void update(PLOEntry ploEntry) {
		Map<String, HashMap<String, String>> ploEntriesMap =
			_ploEntriesMapDCLSingleton.getSingleton(_supplier);

		ploEntriesMap.computeIfPresent(
			encodeKey(ploEntry.getCompanyId(), ploEntry.getLanguageId()),
			(key, value) -> {
				value.put(ploEntry.getKey(), ploEntry.getValue());

				return value;
			});
	}

	private static void _add(
		Map<String, HashMap<String, String>> ploEntriesMap, PLOEntry ploEntry) {

		ploEntriesMap.compute(
			encodeKey(ploEntry.getCompanyId(), ploEntry.getLanguageId()),
			(key, value) -> {
				if (value == null) {
					value = new HashMap<>();
				}

				value.put(ploEntry.getKey(), ploEntry.getValue());

				return value;
			});
	}

	private static Map<String, HashMap<String, String>> _createPLOEntriesMap() {
		Map<String, HashMap<String, String>> ploEntriesMap =
			new ConcurrentHashMap<>();

		CompanyLocalService companyLocalService =
			_companyLocalServiceSnapshot.get();

		companyLocalService.forEachCompanyId(
			companyId -> {
				for (PLOEntry ploEntry :
						PLOEntryLocalServiceUtil.getPLOEntries(companyId)) {

					_add(ploEntriesMap, ploEntry);
				}
			});

		return ploEntriesMap;
	}

	private static final Snapshot<CompanyLocalService>
		_companyLocalServiceSnapshot = new Snapshot<>(
			PLOLanguageOverrideProviderUtil.class, CompanyLocalService.class);
	private static final DCLSingleton<Map<String, HashMap<String, String>>>
		_ploEntriesMapDCLSingleton = new DCLSingleton<>();
	private static final Supplier<Map<String, HashMap<String, String>>>
		_supplier = PLOLanguageOverrideProviderUtil::_createPLOEntriesMap;

}