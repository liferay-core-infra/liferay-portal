/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.multiple.internal.cluster.link;

import com.liferay.portal.cache.multiple.internal.PortalCacheClusterEvent;
import com.liferay.portal.cache.multiple.internal.PortalCacheClusterEventType;
import com.liferay.portal.cache.multiple.internal.cluster.link.messaging.ClusterLinkPortalCacheClusterListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.util.SerializableUtil;

import java.io.Serializable;

/**
 * @author Tina Tian
 */
public class ClusterLinkMessageUtil {

	public static Message create(
		String destinationName,
		PortalCacheClusterEvent portalCacheClusterEvent) {

		Message message = new Message();

		message.setDestinationName(destinationName);
		message.put(
			_CACHE_MANAGER_NAME,
			portalCacheClusterEvent.getPortalCacheManagerName());
		message.put(_CACHE_NAME, portalCacheClusterEvent.getPortalCacheName());
		message.put(_COMPANY_ID, portalCacheClusterEvent.getCompanyId());
		message.put(
			_EVENT_TYPE,
			String.valueOf(portalCacheClusterEvent.getEventType()));
		message.put(
			_KEY,
			SerializableUtil.serialize(
				portalCacheClusterEvent.getElementKey()));
		message.put(_TIME_TO_LIVE, portalCacheClusterEvent.getTimeToLive());

		Serializable value = portalCacheClusterEvent.getElementValue();

		if (value != null) {
			message.put(_VALUE, SerializableUtil.serialize(value));
		}

		return message;
	}

	public static String getCacheManagerName(Message message) {
		return message.getString(_CACHE_MANAGER_NAME);
	}

	public static String getCacheName(Message message) {
		return message.getString(_CACHE_NAME);
	}

	public static long getCompanyId(Message message) {
		return message.getLong(_COMPANY_ID);
	}

	public static Serializable getKey(Message message) {
		return (Serializable)SerializableUtil.deserialize(
			(byte[])message.get(_KEY),
			ClusterLinkPortalCacheClusterListener.class.getClassLoader());
	}

	public static PortalCacheClusterEventType getPortalCacheClusterEventType(
		Message message) {

		return PortalCacheClusterEventType.valueOf(
			message.getString(_EVENT_TYPE));
	}

	public static int getTimeToLive(Message message) {
		return message.getInteger(_TIME_TO_LIVE);
	}

	public static Serializable getValue(Message message) {
		Object value = message.get(_VALUE);

		if (value == null) {
			return null;
		}

		return (Serializable)SerializableUtil.deserialize(
			(byte[])value,
			ClusterLinkPortalCacheClusterListener.class.getClassLoader());
	}

	private static final String _CACHE_MANAGER_NAME = "cache.manager.name";

	private static final String _CACHE_NAME = "cache.name";

	private static final String _COMPANY_ID = "company.id";

	private static final String _EVENT_TYPE = "event.type";

	private static final String _KEY = "key";

	private static final String _TIME_TO_LIVE = "time.to.live";

	private static final String _VALUE = "value";

}