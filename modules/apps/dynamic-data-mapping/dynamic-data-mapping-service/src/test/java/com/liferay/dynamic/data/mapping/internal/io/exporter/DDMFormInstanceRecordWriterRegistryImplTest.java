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
import java.util.Set;

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
public class DDMFormInstanceRecordWriterRegistryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_ddmFormInstanceRecordWriterRegistryImpl =
			new DDMFormInstanceRecordWriterRegistryImpl();

		_bundleContext = SystemBundleUtil.getBundleContext();

		_ddmFormInstanceRecordWriterRegistryImpl.activate(_bundleContext);
	}

	@After
	public void tearDown() {
		_ddmFormInstanceRecordWriterRegistryImpl.deactivate();

		for (ServiceRegistration<DDMFormInstanceRecordWriter>
				serviceRegistration : _serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	public void testDeactivate() {
		_addDDMFormInstanceRecordCSVWriter();

		_ddmFormInstanceRecordWriterRegistryImpl.deactivate();

		Set<String> ddmFormInstanceRecordWriterTypes =
			_ddmFormInstanceRecordWriterRegistryImpl.
				getDDMFormInstanceRecordWriterTypes();

		Assert.assertTrue(ddmFormInstanceRecordWriterTypes.isEmpty());
	}

	@Test
	public void testGetDDMFormInstanceRecordWriter() {
		_addDDMFormInstanceRecordCSVWriter();

		DDMFormInstanceRecordWriter ddmFormInstanceRecordWriter =
			_ddmFormInstanceRecordWriterRegistryImpl.
				getDDMFormInstanceRecordWriter("csv");

		Assert.assertTrue(
			ddmFormInstanceRecordWriter instanceof
				DDMFormInstanceRecordCSVWriter);
	}

	@Test
	public void testGetDDMFormInstanceRecordWriterTypes() {
		_addDDMFormInstanceRecordCSVWriter();
		_addDDMFormInstanceRecordJSONWriter();

		Set<String> ddmFormInstanceRecordWriterTypes =
			_ddmFormInstanceRecordWriterRegistryImpl.
				getDDMFormInstanceRecordWriterTypes();

		Assert.assertTrue(ddmFormInstanceRecordWriterTypes.contains("csv"));
		Assert.assertTrue(ddmFormInstanceRecordWriterTypes.contains("json"));
	}

	@Test
	public void testRemoveDDMFormInstanceRecordWriter() {
		_addDDMFormInstanceRecordCSVWriter();

		Assert.assertNotNull(
			_ddmFormInstanceRecordWriterRegistryImpl.
				getDDMFormInstanceRecordWriter("csv"));

		ServiceRegistration<DDMFormInstanceRecordWriter> serviceRegistration =
			_serviceRegistrations.remove(0);

		serviceRegistration.unregister();

		Assert.assertNull(
			_ddmFormInstanceRecordWriterRegistryImpl.
				getDDMFormInstanceRecordWriter("csv"));
	}

	private void _addDDMFormInstanceRecordCSVWriter() {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				DDMFormInstanceRecordWriter.class,
				new DDMFormInstanceRecordCSVWriter(),
				HashMapDictionaryBuilder.put(
					"ddm.form.instance.record.writer.type", "csv"
				).build()));
	}

	private void _addDDMFormInstanceRecordJSONWriter() {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				DDMFormInstanceRecordWriter.class,
				new DDMFormInstanceRecordJSONWriter(),
				HashMapDictionaryBuilder.put(
					"ddm.form.instance.record.writer.type", "json"
				).build()));
	}

	private BundleContext _bundleContext;
	private DDMFormInstanceRecordWriterRegistryImpl
		_ddmFormInstanceRecordWriterRegistryImpl;
	private final List<ServiceRegistration<DDMFormInstanceRecordWriter>>
		_serviceRegistrations = new ArrayList<>();

}