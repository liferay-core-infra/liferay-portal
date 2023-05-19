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

package com.liferay.asset.link.internal.exportimport.staged.model.repository;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetLink;
import com.liferay.asset.kernel.model.adapter.StagedAssetLink;
import com.liferay.asset.kernel.service.AssetLinkLocalService;
import com.liferay.asset.util.StagingAssetEntryHelper;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.adapter.ModelAdapterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(service = StagedAssetLinkStagedModelRepositoryHelper.class)
public class StagedAssetLinkStagedModelRepositoryHelper {

	public StagedAssetLink fetchExistingAssetLink(
			long groupId, String assetEntry1Uuid, String assetEntry2Uuid)
		throws PortalException {

		AssetEntry assetEntry1 = _stagingAssetEntryHelper.fetchAssetEntry(
			groupId, assetEntry1Uuid);
		AssetEntry assetEntry2 = _stagingAssetEntryHelper.fetchAssetEntry(
			groupId, assetEntry2Uuid);

		if ((assetEntry1 == null) || (assetEntry2 == null)) {
			return null;
		}

		DynamicQuery dynamicQuery = _getAssetLinkDynamicQuery(
			assetEntry1.getEntryId(), assetEntry2.getEntryId());

		List<AssetLink> assetLinks = _assetLinkLocalService.dynamicQuery(
			dynamicQuery);

		if (ListUtil.isNotEmpty(assetLinks)) {
			return ModelAdapterUtil.adapt(
				assetLinks.get(0), AssetLink.class, StagedAssetLink.class);
		}

		return null;
	}

	private DynamicQuery _getAssetLinkDynamicQuery(
		long entryId1, long entryId2) {

		DynamicQuery dynamicQuery = _assetLinkLocalService.dynamicQuery();

		Property entryId1IdProperty = PropertyFactoryUtil.forName("entryId1");

		dynamicQuery.add(entryId1IdProperty.eq(entryId1));

		Property entryId2IdProperty = PropertyFactoryUtil.forName("entryId2");

		dynamicQuery.add(entryId2IdProperty.eq(entryId2));

		return dynamicQuery;
	}

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private StagingAssetEntryHelper _stagingAssetEntryHelper;

}