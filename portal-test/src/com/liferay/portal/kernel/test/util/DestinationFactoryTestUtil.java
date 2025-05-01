/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Dante Wang
 */
public class DestinationFactoryTestUtil {

	public static AutoCloseable registerTestDestinationProvider(
		String destinationType,
		Function<DestinationDefinition, Destination> providerFunction) {

		_providerFunctionsMap.put(destinationType, providerFunction);

		return () -> _providerFunctionsMap.remove(destinationType);
	}

	public static AutoCloseable swapDestinationFactory() {
		MessageBus messageBus = _snapshot.get();

		DestinationFactory originalDestinationFactory =
			ReflectionTestUtil.getFieldValue(messageBus, "_destinationFactory");

		return ReflectionTestUtil.setFieldValueWithAutoCloseable(
			messageBus, "_destinationFactory",
			new TestDestinationFactory(originalDestinationFactory));
	}

	public static class TestDestinationFactory implements DestinationFactory {

		public TestDestinationFactory(DestinationFactory destinationFactory) {
			_destinationFactory = destinationFactory;
		}

		@Override
		public Destination createDestination(
			DestinationDefinition destinationDefinition) {

			Function<DestinationDefinition, Destination> providerFunction =
				_providerFunctionsMap.get(
					destinationDefinition.getDestinationType());

			if (providerFunction == null) {
				return _destinationFactory.createDestination(
					destinationDefinition);
			}

			return providerFunction.apply(destinationDefinition);
		}

		@Override
		public Collection<String> getDestinationTypes() {
			List<String> destinationTypes = new ArrayList<>(
				_destinationFactory.getDestinationTypes());

			destinationTypes.addAll(_providerFunctionsMap.keySet());

			return destinationTypes;
		}

		private final DestinationFactory _destinationFactory;

	}

	private static final Map
		<String, Function<DestinationDefinition, Destination>>
			_providerFunctionsMap = new ConcurrentHashMap<>();
	private static final Snapshot<MessageBus> _snapshot = new Snapshot<>(
		DestinationFactoryTestUtil.class, MessageBus.class, null, true);

}