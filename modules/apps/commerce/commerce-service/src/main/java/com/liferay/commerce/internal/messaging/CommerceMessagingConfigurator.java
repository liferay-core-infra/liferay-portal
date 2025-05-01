/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.messaging;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(service = {})
public class CommerceMessagingConfigurator {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_basePriceListServiceRegistration = _registerDestination(
			bundleContext, DestinationNames.COMMERCE_BASE_PRICE_LIST,
			CommercePriceList.class.getName());
		_orderStatusServiceRegistration = _registerDestination(
			bundleContext, DestinationNames.COMMERCE_ORDER_STATUS,
			CommerceOrder.class.getName());
		_paymentStatusServiceRegistration = _registerDestination(
			bundleContext, DestinationNames.COMMERCE_PAYMENT_STATUS,
			CommerceOrder.class.getName());
		_shipmentStatusServiceRegistration = _registerDestination(
			bundleContext, DestinationNames.COMMERCE_SHIPMENT_STATUS,
			CommerceShipment.class.getName());
		_subscriptionStatusServiceRegistration = _registerDestination(
			bundleContext, DestinationNames.COMMERCE_SUBSCRIPTION_STATUS,
			CommerceOrder.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		if (_basePriceListServiceRegistration != null) {
			_basePriceListServiceRegistration.unregister();
		}

		if (_orderStatusServiceRegistration != null) {
			_orderStatusServiceRegistration.unregister();
		}

		if (_paymentStatusServiceRegistration != null) {
			_paymentStatusServiceRegistration.unregister();
		}

		if (_shipmentStatusServiceRegistration != null) {
			_shipmentStatusServiceRegistration.unregister();
		}

		if (_subscriptionStatusServiceRegistration != null) {
			_subscriptionStatusServiceRegistration.unregister();
		}
	}

	private ServiceRegistration<DestinationDefinition> _registerDestination(
		BundleContext bundleContext, String destinationName, String className) {

		DestinationDefinition destinationDefinition =
			DestinationConfiguration.createParallelDestinationConfiguration(
				destinationName);

		return bundleContext.registerService(
			DestinationDefinition.class, destinationDefinition,
			HashMapDictionaryBuilder.<String, Object>put(
				"destination.name", destinationName
			).put(
				"object.action.trigger.class.name", className
			).build());
	}

	private volatile ServiceRegistration<DestinationDefinition>
		_basePriceListServiceRegistration;
	private volatile ServiceRegistration<DestinationDefinition>
		_orderStatusServiceRegistration;
	private volatile ServiceRegistration<DestinationDefinition>
		_paymentStatusServiceRegistration;
	private volatile ServiceRegistration<DestinationDefinition>
		_shipmentStatusServiceRegistration;
	private volatile ServiceRegistration<DestinationDefinition>
		_subscriptionStatusServiceRegistration;

}