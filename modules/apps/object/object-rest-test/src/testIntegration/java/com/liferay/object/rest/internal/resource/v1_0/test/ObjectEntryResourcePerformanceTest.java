/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class ObjectEntryResourcePerformanceTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-117379")
	public void testGetObjectEntriesPageWithFilter() throws Exception {
		_addObjectEntries(5);

		ObjectDefinition objectDefinition = getObjectDefinition1();

		String endpoint = StringBundler.concat(
			objectDefinition.getRESTContextPath(), "?filter=",
			URLCodec.encodeURL(
				StringBundler.concat(
					OBJECT_FIELD_NAME_TEXT, " eq '", _OBJECT_FIELD_VALUE, "'")),
			"&pageSize=20");

		int queriesCount = _getObjectEntryQueriesCount(5, endpoint);

		_addObjectEntries(5);

		Assert.assertEquals(
			queriesCount, _getObjectEntryQueriesCount(10, endpoint));
	}

	private void _addObjectEntries(int count) throws Exception {
		for (int i = 0; i < count; i++) {
			ObjectEntryTestUtil.addObjectEntry(
				getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
				_OBJECT_FIELD_VALUE);
		}
	}

	private int _getObjectEntryQueriesCount(
			int expectedItemsCount, String endpoint)
		throws Exception {

		HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.GET);

		CacheRegistryUtil.clear();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"org.hibernate.SQL", LoggerTestUtil.DEBUG)) {

			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint, Http.Method.GET);

			JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

			Assert.assertEquals(expectedItemsCount, itemsJSONArray.length());

			int queriesCount = 0;

			for (LogEntry logEntry : logCapture.getLogEntries()) {
				String message = logEntry.getMessage();

				if (!message.contains(" ObjectEntry ")) {
					continue;
				}

				queriesCount++;
			}

			return queriesCount;
		}
	}

	private static final String _OBJECT_FIELD_VALUE =
		RandomTestUtil.randomString();

}