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

package com.liferay.commerce.internal.model.listener;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.util.CommerceShippingHelper;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Hai Yu
 */
@Component(enabled = false, immediate = true, service = ModelListener.class)
public class CommerceOrderItemModelListener
	extends BaseModelListener<CommerceOrderItem> {

	@Override
	public void onAfterRemove(CommerceOrderItem commerceOrderItem)
		throws ModelListenerException {

		try {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			if (serviceContext != null) {
				long commerceOrderItemId = GetterUtil.getLong(
					serviceContext.removeAttribute("commerceOrderItemId"));

				CommerceContext commerceContext =
					(CommerceContext)serviceContext.removeAttribute(
						"commerceContext");

				if ((commerceContext != null) &&
					(commerceOrderItemId ==
						commerceOrderItem.getCommerceOrderItemId())) {

					CommerceOrder commerceOrder =
						commerceOrderItem.getCommerceOrder();

					if (_commerceShippingHelper.isFreeShipping(commerceOrder)) {
						_commerceOrderLocalService.updateCommerceShippingMethod(
							commerceOrder.getCommerceOrderId(), 0, null,
							BigDecimal.ZERO, commerceContext);
					}

					_commerceOrderLocalService.recalculatePrice(
						commerceOrder.getCommerceOrderId(), commerceContext);
				}
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterUpdate(
			CommerceOrderItem originalCommerceOrderItem,
			CommerceOrderItem commerceOrderItem)
		throws ModelListenerException {

		try {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			if (serviceContext != null) {
				long commerceOrderItemId = GetterUtil.getLong(
					serviceContext.removeAttribute("commerceOrderItemId"));

				CommerceContext commerceContext =
					(CommerceContext)serviceContext.removeAttribute(
						"commerceContext");

				if ((commerceContext != null) &&
					(commerceOrderItemId ==
						commerceOrderItem.getCommerceOrderItemId())) {

					_commerceOrderLocalService.recalculatePrice(
						commerceOrderItem.getCommerceOrderId(),
						commerceContext);
				}
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceShippingHelper _commerceShippingHelper;

}