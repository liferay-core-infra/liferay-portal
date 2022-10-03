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

package com.liferay.commerce.service.impl;

import com.liferay.commerce.service.CommerceAvailabilityEstimateLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;

/**
 * @author Tina Tian
 */
public class CommerceServiceCircularDependencies {

	protected static CommerceAvailabilityEstimateLocalService
		getCommerceAvailabilityEstimateLocalService() {

		return _commerceAvailabilityEstimateLocalService;
	}

	protected static CommerceOrderLocalService getCommerceOrderLocalService() {
		return _commerceOrderLocalService;
	}

	protected static void setCommerceAvailabilityEstimateLocalService(
		CommerceAvailabilityEstimateLocalService
			commerceAvailabilityEstimateLocalService) {

		_commerceAvailabilityEstimateLocalService =
			commerceAvailabilityEstimateLocalService;
	}

	protected static void setCommerceOrderLocalService(
		CommerceOrderLocalService commerceOrderLocalService) {

		_commerceOrderLocalService = commerceOrderLocalService;
	}

	private static CommerceAvailabilityEstimateLocalService
		_commerceAvailabilityEstimateLocalService;
	private static CommerceOrderLocalService _commerceOrderLocalService;

}