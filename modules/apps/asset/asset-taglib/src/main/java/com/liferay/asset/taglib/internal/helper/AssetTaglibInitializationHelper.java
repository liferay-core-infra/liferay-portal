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

package com.liferay.asset.taglib.internal.helper;

import com.liferay.asset.taglib.servlet.taglib.AssetAddonEntrySelectorTag;
import com.liferay.asset.taglib.servlet.taglib.AssetCategoriesErrorTag;
import com.liferay.asset.taglib.servlet.taglib.AssetCategoriesNavigationTag;
import com.liferay.asset.taglib.servlet.taglib.AssetCategoriesSelectorTag;
import com.liferay.asset.taglib.servlet.taglib.AssetCategoriesSummaryTag;
import com.liferay.asset.taglib.servlet.taglib.AssetDisplayTag;
import com.liferay.asset.taglib.servlet.taglib.AssetLinksTag;
import com.liferay.asset.taglib.servlet.taglib.AssetMetadataTag;
import com.liferay.asset.taglib.servlet.taglib.AssetTagsErrorTag;
import com.liferay.asset.taglib.servlet.taglib.AssetTagsNavigationTag;
import com.liferay.asset.taglib.servlet.taglib.AssetTagsSelectorTag;
import com.liferay.asset.taglib.servlet.taglib.AssetTagsSummaryTag;
import com.liferay.asset.taglib.servlet.taglib.CategorizationFilterTag;
import com.liferay.asset.taglib.servlet.taglib.InputAssetLinksTag;
import com.liferay.asset.taglib.servlet.taglib.SelectAssetDisplayPageTag;

import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(service = {})
public class AssetTaglibInitializationHelper {

	@Activate
	protected void activate() {
		AssetAddonEntrySelectorTag.initServletContext(_servletContext);
		AssetCategoriesErrorTag.initServletContext(_servletContext);
		AssetCategoriesNavigationTag.initServletContext(_servletContext);
		AssetCategoriesSelectorTag.initServletContext(_servletContext);
		AssetCategoriesSummaryTag.initServletContext(_servletContext);
		AssetDisplayTag.initServletContext(_servletContext);
		AssetLinksTag.initServletContext(_servletContext);
		AssetMetadataTag.initServletContext(_servletContext);
		AssetTagsErrorTag.initServletContext(_servletContext);
		AssetTagsNavigationTag.initServletContext(_servletContext);
		AssetTagsSelectorTag.initServletContext(_servletContext);
		AssetTagsSummaryTag.initServletContext(_servletContext);
		CategorizationFilterTag.initServletContext(_servletContext);
		InputAssetLinksTag.initServletContext(_servletContext);
		SelectAssetDisplayPageTag.initServletContext(_servletContext);
	}

	@Reference(target = "(osgi.web.symbolicname=com.liferay.asset.taglib)")
	private ServletContext _servletContext;

}