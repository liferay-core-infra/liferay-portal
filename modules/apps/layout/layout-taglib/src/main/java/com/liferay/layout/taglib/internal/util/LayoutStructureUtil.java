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

package com.liferay.layout.taglib.internal.util;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.taglib.internal.servlet.ServletContextUtil;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import java.util.Date;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Víctor Galán
 */
public class LayoutStructureUtil {

	public static LayoutStructure getLayoutStructure(
		HttpServletRequest httpServletRequest, long plid) {

		try {
			Layout layout = _getLayout(plid);

			LayoutPageTemplateStructure layoutPageTemplateStructure =
				LayoutPageTemplateStructureLocalServiceUtil.
					fetchLayoutPageTemplateStructure(
						layout.getGroupId(), layout.getPlid(), true);

			long segmentsExperienceId = _getSegmentsExperienceId(
				httpServletRequest);

			String data = layoutPageTemplateStructure.getData(
				segmentsExperienceId);

			if (Validator.isNull(data)) {
				return null;
			}

			String masterLayoutData = null;

			LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchLayoutPageTemplateEntryByPlid(
						layout.getMasterLayoutPlid());

			LayoutPageTemplateStructure masterLayoutPageTemplateStructure =
				null;

			if (masterLayoutPageTemplateEntry != null) {
				masterLayoutPageTemplateStructure =
					LayoutPageTemplateStructureLocalServiceUtil.
						fetchLayoutPageTemplateStructure(
							masterLayoutPageTemplateEntry.getGroupId(),
							masterLayoutPageTemplateEntry.getPlid());

				if (masterLayoutPageTemplateStructure != null) {
					masterLayoutData =
						masterLayoutPageTemplateStructure.
							getDefaultSegmentsExperienceData();
				}
			}

			String dataKey = _getKey(
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				segmentsExperienceId,
				layoutPageTemplateStructure.getModifiedDate());

			if (Validator.isNull(masterLayoutData)) {
				return _getLayoutStructure(
					dataKey, () -> LayoutStructure.of(data));
			}

			String masterLayoutDataKey = _getKey(
				masterLayoutPageTemplateStructure.
					getLayoutPageTemplateStructureId(),
				_getMasterSegmentsExperienceId(
					masterLayoutPageTemplateStructure.getPlid()),
				masterLayoutPageTemplateStructure.getModifiedDate());

			String copyMasterLayoutData = masterLayoutData;

			return _getLayoutStructure(
				dataKey + StringPool.DASH + masterLayoutDataKey,
				() -> _mergeLayoutStructure(data, copyMasterLayoutData));
		}
		catch (Exception exception) {
			_log.error("Unable to get layout structure", exception);

			return null;
		}
	}

	private static String _getKey(
		long layoutPageTemplateStructureId, long segmentsExperienceId,
		Date modifiedDate) {

		StringBundler cacheKeyDSB = new StringBundler(5);

		cacheKeyDSB.append(layoutPageTemplateStructureId);
		cacheKeyDSB.append(StringPool.DASH);
		cacheKeyDSB.append(segmentsExperienceId);
		cacheKeyDSB.append(StringPool.DASH);
		cacheKeyDSB.append(modifiedDate.getTime());

		return cacheKeyDSB.toString();
	}

	private static Layout _getLayout(long plid) {
		Layout layout = LayoutLocalServiceUtil.fetchLayout(plid);

		if (layout instanceof VirtualLayout) {
			VirtualLayout virtualLayout = (VirtualLayout)layout;

			layout = virtualLayout.getSourceLayout();
		}

		return layout;
	}

	private static LayoutStructure _getLayoutStructure(
		String key, Supplier<LayoutStructure> layoutStructureSupplier) {

		LayoutStructure layoutStructure = _portalCache.get(key);

		if (layoutStructure == null) {
			layoutStructure = layoutStructureSupplier.get();

			_portalCache.put(key, layoutStructure);
		}

		return layoutStructure;
	}

	private static long _getMasterSegmentsExperienceId(long plid) {
		return SegmentsExperienceLocalServiceUtil.
			fetchDefaultSegmentsExperienceId(plid);
	}

	private static long _getSegmentsExperienceId(
		HttpServletRequest httpServletRequest) {

		long selectedSegmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId", -1);

		if (selectedSegmentsExperienceId != -1) {
			return selectedSegmentsExperienceId;
		}

		SegmentsExperienceManager segmentsExperienceManager =
			new SegmentsExperienceManager(
				ServletContextUtil.getSegmentsExperienceLocalService());

		return segmentsExperienceManager.getSegmentsExperienceId(
			httpServletRequest);
	}

	private static LayoutStructure _mergeLayoutStructure(
		String data, String masterLayoutData) {

		LayoutStructure masterLayoutStructure = LayoutStructure.of(
			masterLayoutData);

		LayoutStructure layoutStructure = LayoutStructure.of(data);

		for (LayoutStructureItem layoutStructureItem :
				layoutStructure.getLayoutStructureItems()) {

			masterLayoutStructure.addLayoutStructureItem(layoutStructureItem);
		}

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			(DropZoneLayoutStructureItem)
				masterLayoutStructure.getDropZoneLayoutStructureItem();

		dropZoneLayoutStructureItem.addChildrenItem(
			layoutStructure.getMainItemId());

		LayoutStructureItem rootStructureItem =
			masterLayoutStructure.getLayoutStructureItem(
				layoutStructure.getMainItemId());

		rootStructureItem.setParentItemId(
			dropZoneLayoutStructureItem.getItemId());

		return masterLayoutStructure;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutStructureUtil.class);

	private static final PortalCache<String, LayoutStructure> _portalCache =
		PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM,
			LayoutStructureUtil.class.getName());

}