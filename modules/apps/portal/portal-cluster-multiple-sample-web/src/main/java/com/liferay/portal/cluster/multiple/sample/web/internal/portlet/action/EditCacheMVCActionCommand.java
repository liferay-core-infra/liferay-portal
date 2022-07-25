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

package com.liferay.portal.cluster.multiple.sample.web.internal.portlet.action;

import com.liferay.portal.cluster.multiple.sample.web.internal.constants.ClusterSamplePortletKeys;
import com.liferay.portal.cluster.multiple.sample.web.internal.portlet.ClusterSampleCacheReplicationPortlet;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Janis Zhang
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + ClusterSamplePortletKeys.CLUSTER_SAMPLE_CACHE_REPLICATION,
		"mvc.command.name=/cluster_sample_cache_replication/edit_cache"
	},
	service = MVCActionCommand.class
)
public class EditCacheMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	protected void activate() {
		_portalCache = (PortalCache<String, String>)_multiVMPool.getPortalCache(
			ClusterSampleCacheReplicationPortlet.PORTAL_CACHE_NAME);
	}

	@Override
	protected void doProcessAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals("put_cache_entry")) {
			String key = ParamUtil.getString(actionRequest, "key");
			String value = ParamUtil.getString(actionRequest, "value");

			if (Validator.isNull(key) || Validator.isNull(value)) {
				return;
			}

			_portalCache.put(key, value);
		}
		else if (cmd.equals("remove_cache_entry")) {
			String cacheKey = ParamUtil.getString(actionRequest, "currentKey");

			if (Validator.isNull(cacheKey)) {
				return;
			}

			_portalCache.remove(cacheKey);
		}
		else if (cmd.equals("remove_all_cache_entries")) {
			_portalCache.removeAll();
		}
	}

	@Reference
	private MultiVMPool _multiVMPool;

	private volatile PortalCache<String, String> _portalCache;

}