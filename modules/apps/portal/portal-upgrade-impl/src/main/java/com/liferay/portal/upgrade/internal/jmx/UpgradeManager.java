/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.jmx;

import com.liferay.portal.upgrade.internal.recorder.UpgradeRecorderUtil;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.service.component.annotations.Component;

/**
 * @author Luis Ortiz
 */
@Component(
	enabled = false,
	property = {
		"jmx.objectname=com.liferay.portal.upgrade:classification=upgrade,name=UpgradeManager",
		"jmx.objectname.cache.key=UpgradeManager"
	},
	service = DynamicMBean.class
)
public class UpgradeManager
	extends StandardMBean implements UpgradeManagerMBean {

	public UpgradeManager() throws NotCompliantMBeanException {
		super(UpgradeManagerMBean.class);
	}

	@Override
	public String getResult() {
		return UpgradeRecorderUtil.getResult();
	}

	@Override
	public String getType() {
		return UpgradeRecorderUtil.getType();
	}

}