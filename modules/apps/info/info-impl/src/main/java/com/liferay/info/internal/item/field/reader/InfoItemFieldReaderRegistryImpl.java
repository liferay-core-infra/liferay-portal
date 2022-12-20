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

package com.liferay.info.internal.item.field.reader;

import com.liferay.info.item.field.reader.InfoItemFieldReader;
import com.liferay.info.item.field.reader.InfoItemFieldReaderRegistry;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.reflect.GenericUtil;

import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Jürgen Kappler
 * @author Jorge Ferrer
 */
@Component(service = InfoItemFieldReaderRegistry.class)
public class InfoItemFieldReaderRegistryImpl
	implements InfoItemFieldReaderRegistry {

	@Override
	public List<InfoItemFieldReader> getInfoItemFieldReaders(
		String itemClassName) {

		List<InfoItemFieldReader> infoItemFieldReaders =
			_itemInfoItemFieldReaderServiceTrackerMap.getService(itemClassName);

		if (infoItemFieldReaders == null) {
			infoItemFieldReaders = Collections.emptyList();
		}

		return infoItemFieldReaders;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_itemInfoItemFieldReaderServiceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, InfoItemFieldReader.class, null,
				ServiceReferenceMapperFactory.create(
					bundleContext,
					(infoItemFieldReader, emitter) -> emitter.emit(
						GenericUtil.getGenericClassName(infoItemFieldReader))),
				Collections.reverseOrder(
					new PropertyServiceReferenceComparator<>(
						"info.item.field.order")));
	}

	@Deactivate
	protected void deactivate() {
		_itemInfoItemFieldReaderServiceTrackerMap.close();
	}

	private ServiceTrackerMap<String, List<InfoItemFieldReader>>
		_itemInfoItemFieldReaderServiceTrackerMap;

}