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

package com.liferay.layout.internal.exportimport.staged.model.repository;

import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.layout.set.model.adapter.StagedLayoutSet;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.adapter.ModelAdapterUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(service = StagedLayoutSetStagedModelRepositoryHelper.class)
public class StagedLayoutSetStagedModelRepositoryHelper {

	public List<StagedModel> fetchChildrenStagedModels(
		PortletDataContext portletDataContext,
		StagedLayoutSet stagedLayoutSet) {

		LayoutSet layoutSet = stagedLayoutSet.getLayoutSet();

		return TransformUtil.transform(
			_layoutLocalService.getLayouts(
				stagedLayoutSet.getGroupId(), layoutSet.isPrivateLayout()),
			layout -> {
				if (_exportImportHelper.isLayoutRevisionInReview(layout)) {
					return null;
				}

				return (StagedModel)layout;
			});
	}

	public StagedLayoutSet fetchExistingLayoutSet(
		long groupId, boolean privateLayout) {

		StagedLayoutSet stagedLayoutSet = null;

		try {
			stagedLayoutSet = ModelAdapterUtil.adapt(
				_layoutSetLocalService.getLayoutSet(groupId, privateLayout),
				LayoutSet.class, StagedLayoutSet.class);
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return stagedLayoutSet;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagedLayoutSetStagedModelRepositoryHelper.class);

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

}