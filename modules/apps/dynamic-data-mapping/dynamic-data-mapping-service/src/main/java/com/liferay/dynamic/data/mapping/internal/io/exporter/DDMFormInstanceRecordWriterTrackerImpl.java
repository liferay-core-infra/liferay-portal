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

package com.liferay.dynamic.data.mapping.internal.io.exporter;

import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriter;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterTracker;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Leonardo Barros
 */
@Component(service = DDMFormInstanceRecordWriterTracker.class)
public class DDMFormInstanceRecordWriterTrackerImpl
	implements DDMFormInstanceRecordWriterTracker {

	@Override
	public DDMFormInstanceRecordWriter getDDMFormInstanceRecordWriter(
		String type) {

		return _ddmFormInstanceRecordWriters.get(type);
	}

	@Override
	public Map<String, String> getDDMFormInstanceRecordWriterExtensions() {
		return Collections.unmodifiableMap(
			_ddmFormInstanceRecordWriterExtensions);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, DDMFormInstanceRecordWriter.class,
			new DDMFormInstanceRecordWriterServiceTrackerCustomizer(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
		_ddmFormInstanceRecordWriters.clear();
		_ddmFormInstanceRecordWriterExtensions.clear();
	}

	private final Map<String, String> _ddmFormInstanceRecordWriterExtensions =
		new TreeMap<>();
	private final Map<String, DDMFormInstanceRecordWriter>
		_ddmFormInstanceRecordWriters = new TreeMap<>();
	private volatile ServiceTracker
		<DDMFormInstanceRecordWriter, DDMFormInstanceRecordWriter>
			_serviceTracker;

	private class DDMFormInstanceRecordWriterServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<DDMFormInstanceRecordWriter, DDMFormInstanceRecordWriter> {

		public DDMFormInstanceRecordWriterServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public DDMFormInstanceRecordWriter addingService(
			ServiceReference<DDMFormInstanceRecordWriter> serviceReference) {

			String type = (String)serviceReference.getProperty(
				"ddm.form.instance.record.writer.type");

			String extension = (String)serviceReference.getProperty(
				"ddm.form.instance.record.writer.extension");

			if (Validator.isNull(extension)) {
				extension = StringUtil.toUpperCase(type);
			}

			_ddmFormInstanceRecordWriterExtensions.put(type, extension);
			_ddmFormInstanceRecordWriters.put(
				type, _bundleContext.getService(serviceReference));

			return _bundleContext.getService(serviceReference);
		}

		@Override
		public void modifiedService(
			ServiceReference<DDMFormInstanceRecordWriter> serviceReference,
			DDMFormInstanceRecordWriter ddmFormInstanceRecordWriter) {
		}

		@Override
		public void removedService(
			ServiceReference<DDMFormInstanceRecordWriter> serviceReference,
			DDMFormInstanceRecordWriter ddmFormInstanceRecordWriter) {

			String type = (String)serviceReference.getProperty(
				"ddm.form.instance.record.writer.type");

			_ddmFormInstanceRecordWriterExtensions.remove(type);
			_ddmFormInstanceRecordWriters.remove(type);

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;

	}

}