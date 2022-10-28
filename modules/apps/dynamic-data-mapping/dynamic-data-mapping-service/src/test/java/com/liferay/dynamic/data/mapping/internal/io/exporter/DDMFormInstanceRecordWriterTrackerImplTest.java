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
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Leonardo Barros
 */
public class DDMFormInstanceRecordWriterTrackerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_ddmFormInstanceRecordWriterTrackerImpl =
			new DDMFormInstanceRecordWriterTrackerImpl();

		_bundleContext = SystemBundleUtil.getBundleContext();

		_ddmFormInstanceRecordWriterTrackerImpl.activate(_bundleContext);
	}

	@After
	public void tearDown() {
		_ddmFormInstanceRecordWriterTrackerImpl.deactivate();

		for (ServiceRegistration<DDMFormInstanceRecordWriter>
				serviceRegistration : _serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	public void testDeactivate() {
		_addDDMFormInstanceRecordCSVWriter();

		_ddmFormInstanceRecordWriterTrackerImpl.deactivate();

		Map<String, String> ddmFormInstanceRecordWriterExtensions =
			_ddmFormInstanceRecordWriterTrackerImpl.
				getDDMFormInstanceRecordWriterExtensions();

		Assert.assertTrue(ddmFormInstanceRecordWriterExtensions.isEmpty());
	}

	@Test
	public void testGetDDMFormInstanceRecordWriter() {
		_addDDMFormInstanceRecordCSVWriter();

		DDMFormInstanceRecordWriter ddmFormInstanceRecordWriter =
			_ddmFormInstanceRecordWriterTrackerImpl.
				getDDMFormInstanceRecordWriter("csv");

		Assert.assertTrue(
			ddmFormInstanceRecordWriter instanceof
				DDMFormInstanceRecordCSVWriter);
	}

	@Test
	public void testGetDDMFormInstanceRecordWriterDefaultUpperCaseExtension() {
		_addDDMFormInstanceRecordXMLWriter();

		Map<String, String> ddmFormInstanceRecordWriterExtensions =
			_ddmFormInstanceRecordWriterTrackerImpl.
				getDDMFormInstanceRecordWriterExtensions();

		Assert.assertEquals(
			"XML", ddmFormInstanceRecordWriterExtensions.get("xml"));
	}

	@Test
	public void testGetDDMFormInstanceRecordWriterTypes() {
		_addDDMFormInstanceRecordCSVWriter();
		_addDDMFormInstanceRecordJSONWriter();

		Map<String, String> ddmFormInstanceRecordWriterExtensions =
			_ddmFormInstanceRecordWriterTrackerImpl.
				getDDMFormInstanceRecordWriterExtensions();

		Assert.assertEquals(
			"csv", ddmFormInstanceRecordWriterExtensions.get("csv"));
		Assert.assertEquals(
			"json", ddmFormInstanceRecordWriterExtensions.get("json"));
	}

	@Test
	public void testRemoveDDMFormInstanceRecordWriter() {
		_addDDMFormInstanceRecordCSVWriter();

		Assert.assertNotNull(
			_ddmFormInstanceRecordWriterTrackerImpl.
				getDDMFormInstanceRecordWriter("csv"));

		_serviceRegistration.unregister();

		Assert.assertNull(
			_ddmFormInstanceRecordWriterTrackerImpl.
				getDDMFormInstanceRecordWriter("csv"));
	}

	private void _addDDMFormInstanceRecordCSVWriter() {
		_serviceRegistration = _bundleContext.registerService(
			DDMFormInstanceRecordWriter.class,
			new DDMFormInstanceRecordCSVWriter(),
			HashMapDictionaryBuilder.put(
				"ddm.form.instance.record.writer.extension", "csv"
			).put(
				"ddm.form.instance.record.writer.type", "csv"
			).build());

		_serviceRegistrations.add(_serviceRegistration);
	}

	private void _addDDMFormInstanceRecordJSONWriter() {
		_serviceRegistration = _bundleContext.registerService(
			DDMFormInstanceRecordWriter.class,
			new DDMFormInstanceRecordJSONWriter(),
			HashMapDictionaryBuilder.put(
				"ddm.form.instance.record.writer.extension", "json"
			).put(
				"ddm.form.instance.record.writer.type", "json"
			).build());

		_serviceRegistrations.add(_serviceRegistration);
	}

	private void _addDDMFormInstanceRecordXMLWriter() {
		_serviceRegistration = _bundleContext.registerService(
			DDMFormInstanceRecordWriter.class,
			new DDMFormInstanceRecordXMLWriter(),
			HashMapDictionaryBuilder.put(
				"ddm.form.instance.record.writer.type", "xml"
			).build());

		_serviceRegistrations.add(_serviceRegistration);
	}

	private BundleContext _bundleContext;
	private DDMFormInstanceRecordWriterTrackerImpl
		_ddmFormInstanceRecordWriterTrackerImpl;
	private ServiceRegistration<DDMFormInstanceRecordWriter>
		_serviceRegistration;
	private final List<ServiceRegistration<DDMFormInstanceRecordWriter>>
		_serviceRegistrations = new ArrayList<>();

}