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

package com.liferay.site.internal.exportimport.staged.model.repository;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.layout.set.model.adapter.StagedLayoutSet;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.adapter.ModelAdapterUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.model.adapter.StagedGroup;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(service = StagedGroupStagedModelRepositoryHelper.class)
public class StagedGroupStagedModelRepositoryHelper {

	public List<StagedModel> fetchChildrenStagedModels(
		PortletDataContext portletDataContext, StagedGroup stagedGroup) {

		List<StagedModel> childrenStagedModels = new ArrayList<>();

		Group group = stagedGroup.getGroup();

		long groupId = group.getGroupId();

		try {
			childrenStagedModels.add(
				ModelAdapterUtil.adapt(
					_layoutSetLocalService.getLayoutSet(
						groupId, portletDataContext.isPrivateLayout()),
					LayoutSet.class, StagedLayoutSet.class));
		}
		catch (PortalException portalException) {
			_log.error(
				StringBundler.concat(
					"Unable to fetch Layout Set with groupId ", groupId,
					" and private layout ",
					portletDataContext.isPrivateLayout()),
				portalException);
		}

		return childrenStagedModels;
	}

	public Group fetchExistingGroup(
		PortletDataContext portletDataContext, Element referenceElement) {

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));
		long liveGroupId = GetterUtil.getLong(
			referenceElement.attributeValue("live-group-id"));

		if ((groupId == 0) || (liveGroupId == 0)) {
			return null;
		}

		String groupKey = GetterUtil.getString(
			referenceElement.attributeValue("group-key"));

		return fetchExistingGroup(
			portletDataContext, groupId, liveGroupId, groupKey);
	}

	public Group fetchExistingGroup(
		PortletDataContext portletDataContext, long groupId, long liveGroupId) {

		return fetchExistingGroup(
			portletDataContext, groupId, liveGroupId, null);
	}

	public Group fetchExistingGroup(
		PortletDataContext portletDataContext, long groupId, long liveGroupId,
		String groupKey) {

		Group liveGroup = _groupLocalService.fetchGroup(liveGroupId);

		if ((liveGroup != null) &&
			(liveGroup.getCompanyId() == portletDataContext.getCompanyId())) {

			return liveGroup;
		}

		long existingGroupId = 0;

		if (groupId == portletDataContext.getSourceCompanyGroupId()) {
			existingGroupId = portletDataContext.getCompanyGroupId();
		}
		else if (groupId == portletDataContext.getSourceGroupId()) {
			existingGroupId = portletDataContext.getGroupId();
		}
		else if (Validator.isNotNull(groupKey)) {
			Group groupKeyGroup = _groupLocalService.fetchGroup(
				portletDataContext.getCompanyId(), groupKey);

			if (groupKeyGroup != null) {
				existingGroupId = groupKeyGroup.getGroupId();
			}
		}

		// During remote staging, valid mappings are found when the reference's
		// group is properly staged. During local staging, valid mappings are
		// found when the references do not change between staging and live.

		Group group = _groupLocalService.fetchGroup(existingGroupId);

		if ((group != null) &&
			(group.getCompanyId() == portletDataContext.getCompanyId())) {

			return group;
		}

		return _groupLocalService.fetchGroup(
			portletDataContext.getScopeGroupId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagedGroupStagedModelRepositoryHelper.class);

	@Reference(unbind = "-")
	private GroupLocalService _groupLocalService;

	@Reference(unbind = "-")
	private LayoutSetLocalService _layoutSetLocalService;

}