/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Debora Buriti
 */
@RunWith(Arquillian.class)
public class TemplateRestrictedClassesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() {
		FileUtil.delete(_file);
	}

	@Test
	public void testGetRestrictedClasses() throws Exception {
		File tempDirFile = new File(
			SystemProperties.get(SystemProperties.TMP_DIR));

		_file = new File(tempDirFile, RandomTestUtil.randomString());

		_testGetRestrictedClasses(
			tempDirFile,
			"${object.toPath().resolve(\"" + _file.getName() +
				"\").toFile().createNewFile()?c}");

		Assert.assertFalse(_file.getAbsolutePath(), _file.exists());

		Path path = tempDirFile.toPath();

		_testGetRestrictedClasses(path, "${object.getFileSystem()}");

		FileSystem fileSystem = path.getFileSystem();

		_testGetRestrictedClasses(fileSystem, "${object.provider()}");
		_testGetRestrictedClasses(
			fileSystem.provider(), "${object.getScheme()}");

		Bundle bundle = FrameworkUtil.getBundle(
			TemplateRestrictedClassesTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String templateContent = "${object.getBundles()?size}";

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL,
			new StringTemplateResource(
				RandomTestUtil.randomString(), templateContent),
			false);

		template.put("object", bundleContext);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		Assert.assertEquals(
			String.valueOf(bundleContext.getBundles().length),
			unsyncStringWriter.toString());

		_testGetRestrictedClasses(bundle, "${object.getBundleContext()}");
		_testGetRestrictedClasses(
			bundleContext,
			"${object.getServiceReference(\"" +
				UserLocalService.class.getName() + "\")}");
		_testGetRestrictedClasses(bundleContext, templateContent);
	}

	private void _testGetRestrictedClasses(
			Object object, String templateContent)
		throws Exception {

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL,
			new StringTemplateResource(
				RandomTestUtil.randomString(), templateContent),
			true);

		template.put("object", object);

		try {
			template.processTemplate(new UnsyncStringWriter());

			Assert.fail(templateContent);
		}
		catch (TemplateException templateException) {
			Throwable throwable = templateException;

			while (throwable.getCause() != null) {
				throwable = throwable.getCause();
			}

			String message = throwable.getMessage();

			Assert.assertTrue(
				message, message.contains("Denied access to method or field "));
		}
	}

	private File _file;

}