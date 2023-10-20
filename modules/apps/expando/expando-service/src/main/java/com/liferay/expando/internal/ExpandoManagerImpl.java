/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.internal;

import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.manager.ExpandoManager;
import com.liferay.expando.service.ExpandoRowLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(service = ExpandoManager.class)
public class ExpandoManagerImpl implements ExpandoManager {

	@Override
	public void deleteRows(long classPK) {
		_expandoRowLocalService.deleteRows(classPK);
	}

	@Override
	public void deleteRows(long companyId, long classNameId, long classPK) {
		_expandoRowLocalService.deleteRows(companyId, classNameId, classPK);
	}

	@Override
	public ExpandoRow fetchRow(long tableId, long classPK) {
		return new ExpandoRowImpl(
			_expandoRowLocalService.fetchRow(tableId, classPK));
	}

	@Override
	public void updateExpandoRow(ExpandoRow expandoRow) {
		_expandoRowLocalService.updateExpandoRow(
			(com.liferay.expando.model.ExpandoRow)
				expandoRow.getExpandoRowInstance());
	}

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

}