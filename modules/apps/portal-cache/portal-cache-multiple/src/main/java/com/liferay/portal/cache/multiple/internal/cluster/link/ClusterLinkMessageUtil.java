/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.multiple.internal.cluster.link;

import com.liferay.portal.cache.multiple.internal.PortalCacheClusterEvent;
import com.liferay.portal.cache.multiple.internal.cluster.link.messaging.ClusterLinkPortalCacheClusterListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.util.SerializableUtil;

/**
 * @author Tina Tian
 */
public class ClusterLinkMessageUtil {

	public static Message create(
		String destinationName,
		PortalCacheClusterEvent portalCacheClusterEvent) {

		Message message = new Message();

		message.setDestinationName(destinationName);
		message.setPayload(SerializableUtil.serialize(portalCacheClusterEvent));

		return message;
	}

	public static PortalCacheClusterEvent getPortalCacheClusterEvent(
		Message message) {

		return (PortalCacheClusterEvent)SerializableUtil.deserialize(
			(byte[])message.getPayload(),
			ClusterLinkPortalCacheClusterListener.class.getClassLoader());
	}

}