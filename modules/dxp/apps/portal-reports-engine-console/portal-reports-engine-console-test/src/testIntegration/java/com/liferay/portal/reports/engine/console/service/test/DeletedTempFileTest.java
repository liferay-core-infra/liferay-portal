/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mail.kernel.model.FileAttachment;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.model.Entry;
import com.liferay.portal.reports.engine.console.service.DefinitionLocalServiceUtil;
import com.liferay.portal.reports.engine.console.service.EntryLocalServiceUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alberto Sousa
 */
@RunWith(Arquillian.class)
public class DeletedTempFileTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		setUpDefinition();
	}

	@Test
	public void testRun() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(DeletedTempFileTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> dictionary = MapUtil.singletonDictionary(
			"destination.name", DestinationNames.MAIL);

		TestMessageListener testMessageListener = new TestMessageListener();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				MessageListener.class, testMessageListener, dictionary);

		try {
			Entry entry = EntryLocalServiceUtil.addEntry(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				_definition.getDefinitionId(), "txt", false, null, null, false,
				StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomString() + "@liferay.com",
				StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext());

			for (FileAttachment fileAttachment :
					testMessageListener.getFileAttachment()) {

				Assert.assertFalse(FileUtil.exists(fileAttachment.getFile()));
			}

			EntryLocalServiceUtil.deleteEntry(entry);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	protected void setUpDefinition() throws Exception {
		try (InputStream inputStream =
				EntryServiceTest.class.getResourceAsStream(
					"dependencies/" + _TEMPLATE_NAME + ".jrxml")) {

			Map<Locale, String> nameMap = HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build();

			String content = StringUtil.read(inputStream);

			_definition = DefinitionLocalServiceUtil.addDefinition(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				nameMap, null, 0, null, _TEMPLATE_NAME,
				new UnsyncByteArrayInputStream(
					content.getBytes(StringPool.DEFAULT_CHARSET_NAME)),
				ServiceContextTestUtil.getServiceContext());
		}
	}

	private static final String _TEMPLATE_NAME =
		"reports_admin_template_source_sample_list_type";

	private Definition _definition;

	private class TestMessageListener extends BaseMessageListener {

		public List<FileAttachment> getFileAttachment() {
			return _fileAttachments;
		}

		@Override
		protected void doReceive(Message message) {
			MailMessage mailMessage = (MailMessage)message.getPayload();

			_fileAttachments = mailMessage.getFileAttachments();
		}

		private List<FileAttachment> _fileAttachments = new ArrayList<>();

	}

}