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

package com.liferay.journal.internal.transformer;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.util.JournalTransformerListenerRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.templateparser.TransformerListener;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Pavel Savinov
 */
@Component(immediate = true, service = JournalTransformerListenerRegistry.class)
public class DefaultJournalTransformerListenerRegistryImpl
	implements JournalTransformerListenerRegistry {

	@Override
	public TransformerListener getTransformerListener(String className) {
		return _serviceTrackerMap.getService(className);
	}

	@Override
	public List<TransformerListener> getTransformerListeners() {
		return ListUtil.filter(
			new ArrayList<>(_serviceTrackerMap.values()),
			transformerListener -> {
				if (transformerListener.isEnabled()) {
					return true;
				}

				return false;
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, TransformerListener.class,
			"(javax.portlet.name=" + JournalPortletKeys.JOURNAL + ")",
			(serviceReference, emitter) -> {
				Class<?> clazz = serviceReference.getClass();

				emitter.emit(clazz.getName());
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, TransformerListener> _serviceTrackerMap;

}