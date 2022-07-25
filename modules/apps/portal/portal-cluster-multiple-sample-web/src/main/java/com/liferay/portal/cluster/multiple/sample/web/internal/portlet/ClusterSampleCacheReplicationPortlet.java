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

package com.liferay.portal.cluster.multiple.sample.web.internal.portlet;

import com.liferay.portal.cluster.multiple.sample.web.internal.constants.ClusterSamplePortletKeys;
import com.liferay.portal.cluster.multiple.sample.web.internal.constants.ClusterSampleWebKeys;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Janis Zhang
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Cluster Sample Cache",
		"javax.portlet.init-param.template-path=/META-INF/resources/",
		"javax.portlet.init-param.view-template=/cache_replication/view.jsp",
		"javax.portlet.name=" + ClusterSamplePortletKeys.CLUSTER_SAMPLE_CACHE_REPLICATION,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"javax.portlet.version=3.0"
	},
	service = Portlet.class
)
public class ClusterSampleCacheReplicationPortlet extends MVCPortlet {

	public static final String PORTAL_CACHE_NAME = "test.cache";

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		Map<String, String> cacheEntries = new HashMap<>();

		List<String> keys = _portalCache.getKeys();

		for (String key : keys) {
			cacheEntries.put(key, _portalCache.get(key));
		}

		renderRequest.setAttribute(
			ClusterSampleWebKeys.CLUSTER_SAMPLE_CACHE_ENTRIES, cacheEntries);

		super.render(renderRequest, renderResponse);
	}

	@Activate
	protected void activate() {
		_portalCache = (PortalCache<String, String>)_multiVMPool.getPortalCache(
			PORTAL_CACHE_NAME);
	}

	@Reference
	private MultiVMPool _multiVMPool;

	private volatile PortalCache<String, String> _portalCache;

}