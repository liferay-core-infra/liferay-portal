/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.properties.swapper.internal.portal.profile;

import com.liferay.osgi.util.ComponentUtil;
import com.liferay.portal.change.tracking.store.CTStoreFactory;
import com.liferay.portlet.documentlibrary.store.StoreFactory;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = {})
public class ModulePortalProfileEnabler {

	@Activate
	protected void activate(ComponentContext componentContext) {
		ComponentUtil.enableComponents(
			StoreFactory.class, "(dl.store.impl.enabled=true)",
			componentContext, ModulePortalProfile.class);
	}

	@Reference
	private CTStoreFactory _ctStoreFactory;

}