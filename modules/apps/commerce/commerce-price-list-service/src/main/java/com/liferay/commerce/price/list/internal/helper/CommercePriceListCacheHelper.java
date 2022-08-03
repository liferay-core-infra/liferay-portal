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

package com.liferay.commerce.price.list.internal.helper;

import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(enabled = false, service = CommercePriceListCacheHelper.class)
public class CommercePriceListCacheHelper {

	public void cleanPriceListCache(long companyId) {
		PortalCache<String, Serializable> portalCache =
			(PortalCache<String, Serializable>)_multiVMPool.getPortalCache(
				"PRICE_LISTS_" + companyId);

		portalCache.removeAll();
	}

	public PortalCache<String, Serializable> getPriceListCache(long companyId) {
		return (PortalCache<String, Serializable>)_multiVMPool.getPortalCache(
			"PRICE_LISTS_" + companyId);
	}

	@Reference
	private MultiVMPool _multiVMPool;

}