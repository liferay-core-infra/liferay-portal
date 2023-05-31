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

package com.liferay.redirect.internal.configuration.admin.service;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.redirect.internal.configuration.RedirectPatternConfiguration;
import com.liferay.redirect.internal.provider.util.RedirectPatternEntriesRegistryUtil;
import com.liferay.redirect.internal.util.PatternUtil;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;

/**
 * @author Joao Victor Alves
 */
@Component(
	property = Constants.SERVICE_PID + "=com.liferay.redirect.internal.configuration.RedirectPatternConfiguration.scoped",
	service = ManagedServiceFactory.class
)
public class RedirectProviderManagedServiceFactory
	implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		_unmapPid(pid);
	}

	@Override
	public String getName() {
		return "com.liferay.redirect.internal.configuration." +
			"RedirectPatternConfiguration.scoped";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> dictionary)
		throws ConfigurationException {

		_unmapPid(pid);

		long groupId = GetterUtil.getLong(
			dictionary.get("groupId"), GroupConstants.DEFAULT_PARENT_GROUP_ID);

		if (groupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			return;
		}

		_groupIds.put(pid, groupId);

		RedirectPatternConfiguration redirectPatternConfiguration =
			ConfigurableUtil.createConfigurable(
				RedirectPatternConfiguration.class, dictionary);

		RedirectPatternEntriesRegistryUtil.putRedirectPatternEntry(
			groupId,
			PatternUtil.parse(redirectPatternConfiguration.patternStrings()));
	}

	private void _unmapPid(String pid) {
		if (_groupIds.containsKey(pid)) {
			Long groupId = _groupIds.remove(pid);

			RedirectPatternEntriesRegistryUtil.removeRedirectPatternEntry(
				groupId);
		}
	}

	private final Map<String, Long> _groupIds = new ConcurrentHashMap<>();

}