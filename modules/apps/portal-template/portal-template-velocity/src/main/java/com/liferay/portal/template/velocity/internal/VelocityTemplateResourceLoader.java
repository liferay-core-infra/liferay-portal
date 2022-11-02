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

package com.liferay.portal.template.velocity.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.template.BaseTemplateResourceLoader;
import com.liferay.portal.template.TemplateResourceParser;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Igor Spasic
 * @author Peter Fellwock
 */
@Component(
	immediate = true,
	service = {
		TemplateResourceLoader.class, VelocityTemplateResourceLoader.class
	}
)
public class VelocityTemplateResourceLoader extends BaseTemplateResourceLoader {

	@Activate
	@Modified
	protected void activate(
			BundleContext bundleContext, Map<String, Object> properties)
		throws InvalidSyntaxException {

		String filterString = StringBundler.concat(
			"(&(lang.type=", TemplateConstants.LANG_TYPE_VM, ")(objectClass=",
			TemplateResourceParser.class.getName(), "))");

		_serviceTracker = new ServiceTracker<>(
			bundleContext, bundleContext.createFilter(filterString),
			new ServiceTrackerCustomizer
				<TemplateResourceParser, TemplateResourceParser>() {

				@Override
				public TemplateResourceParser addingService(
					ServiceReference<TemplateResourceParser> serviceReference) {

					TemplateResourceParser templateResourceParser =
						bundleContext.getService(serviceReference);

					_templateResourceParsers.add(templateResourceParser);

					return templateResourceParser;
				}

				@Override
				public void modifiedService(
					ServiceReference<TemplateResourceParser> serviceReference,
					TemplateResourceParser templateResourceParser) {
				}

				@Override
				public void removedService(
					ServiceReference<TemplateResourceParser> serviceReference,
					TemplateResourceParser templateResourceParser) {

					_templateResourceParsers.remove(templateResourceParser);

					bundleContext.ungetService(serviceReference);
				}

			});

		_serviceTracker.open();

		init(
			TemplateConstants.LANG_TYPE_VM, _templateResourceParsers,
			_velocityTemplateResourceCache);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
		destroy();
	}

	private ServiceTracker
		<TemplateResourceParser, TemplateResourceParser> _serviceTracker;
	private final Set<TemplateResourceParser> _templateResourceParsers =
		new ConcurrentSkipListSet<>();

	@Reference
	private VelocityTemplateResourceCache _velocityTemplateResourceCache;

}