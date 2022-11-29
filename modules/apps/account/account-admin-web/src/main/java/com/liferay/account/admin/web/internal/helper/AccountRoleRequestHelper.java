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

package com.liferay.account.admin.web.internal.helper;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategoryRegistry;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.application.list.display.context.logic.PersonalMenuEntryHelper;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;
import com.liferay.roles.admin.constants.RolesAdminWebKeys;
import com.liferay.roles.admin.panel.category.role.type.mapper.PanelCategoryRoleTypeMapper;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Pei-Jung Lan
 */
@Component(service = AccountRoleRequestHelper.class)
public class AccountRoleRequestHelper {

	public void setRequestAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY, _panelAppRegistry);
		httpServletRequest.setAttribute(
			ApplicationListWebKeys.PANEL_CATEGORY_HELPER,
			new PanelCategoryHelper(_panelAppRegistry, _panelCategoryRegistry));
		httpServletRequest.setAttribute(
			ApplicationListWebKeys.PANEL_CATEGORY_REGISTRY,
			_panelCategoryRegistry);
		httpServletRequest.setAttribute(
			ApplicationListWebKeys.PERSONAL_MENU_ENTRY_HELPER,
			new PersonalMenuEntryHelper(_serviceTrackerList.toList()));
		httpServletRequest.setAttribute(
			RolesAdminWebKeys.CURRENT_ROLE_TYPE, _accountRoleTypeContributor);
		httpServletRequest.setAttribute(
			RolesAdminWebKeys.PANEL_CATEGORY_KEYS,
			ArrayUtil.toStringArray(_panelCategoryKeys));
		httpServletRequest.setAttribute(
			RolesAdminWebKeys.SHOW_NAV_TABS, Boolean.FALSE);
	}

	public void setRequestAttributes(PortletRequest portletRequest) {
		setRequestAttributes(_portal.getHttpServletRequest(portletRequest));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker =
			new ServiceTracker
				<PanelCategoryRoleTypeMapper, PanelCategoryRoleTypeMapper>(
					bundleContext, PanelCategoryRoleTypeMapper.class,
					new ServiceTrackerCustomizer
						<PanelCategoryRoleTypeMapper,
						 PanelCategoryRoleTypeMapper>() {

						@Override
						public PanelCategoryRoleTypeMapper addingService(
							ServiceReference<PanelCategoryRoleTypeMapper>
								serviceReference) {

							PanelCategoryRoleTypeMapper
								panelCategoryRoleTypeMapper =
									bundleContext.getService(serviceReference);

							if (ArrayUtil.contains(
									panelCategoryRoleTypeMapper.getRoleTypes(),
									RoleConstants.TYPE_ACCOUNT)) {

								_panelCategoryKeys.add(
									panelCategoryRoleTypeMapper.
										getPanelCategoryKey());
							}

							return panelCategoryRoleTypeMapper;
						}

						@Override
						public void modifiedService(
							ServiceReference<PanelCategoryRoleTypeMapper>
								serviceReference,
							PanelCategoryRoleTypeMapper
								panelCategoryRoleTypeMapper) {
						}

						@Override
						public void removedService(
							ServiceReference<PanelCategoryRoleTypeMapper>
								serviceReference,
							PanelCategoryRoleTypeMapper
								panelCategoryRoleTypeMapper) {

							_panelCategoryKeys.remove(
								panelCategoryRoleTypeMapper.
									getPanelCategoryKey());

							bundleContext.ungetService(serviceReference);
						}

					});

		_serviceTracker.open();

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, PersonalMenuEntry.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();

		_serviceTrackerList.close();
	}

	@Reference(target = "(component.name=*.AccountRoleTypeContributor)")
	private RoleTypeContributor _accountRoleTypeContributor;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	private final List<String> _panelCategoryKeys =
		new CopyOnWriteArrayList<>();

	@Reference
	private PanelCategoryRegistry _panelCategoryRegistry;

	@Reference
	private Portal _portal;

	private ServiceTracker
		<PanelCategoryRoleTypeMapper, PanelCategoryRoleTypeMapper>
			_serviceTracker;
	private ServiceTrackerList<PersonalMenuEntry> _serviceTrackerList;

}