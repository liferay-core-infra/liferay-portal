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

package com.liferay.commerce.price.list.internal.util;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.service.persistence.CommercePriceEntryPersistence;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Dante Wang
 */
public class CommercePriceEntryUtil {

	public static CommercePriceEntry setHasTierPrice(
			CommercePriceEntryPersistence commercePriceEntryPersistence,
			long commercePriceEntryId, boolean hasTierPrice)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		commercePriceEntry.setHasTierPrice(hasTierPrice);
		commercePriceEntry.setBulkPricing(true);

		return commercePriceEntryPersistence.update(commercePriceEntry);
	}

	public static CommercePriceEntry setHasTierPrice(
			CommercePriceEntryPersistence commercePriceEntryPersistence,
			long commercePriceEntryId, boolean hasTierPrice,
			boolean bulkPricing)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		commercePriceEntry.setHasTierPrice(hasTierPrice);
		commercePriceEntry.setBulkPricing(bulkPricing);

		return commercePriceEntryPersistence.update(commercePriceEntry);
	}

}