/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.kernel.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AnnouncementsFlagService}.
 *
 * @author Brian Wing Shun Chan
 * @see AnnouncementsFlagService
 * @generated
 */
public class AnnouncementsFlagServiceWrapper
	implements AnnouncementsFlagService,
			   ServiceWrapper<AnnouncementsFlagService> {

	public AnnouncementsFlagServiceWrapper() {
		this(null);
	}

	public AnnouncementsFlagServiceWrapper(
		AnnouncementsFlagService announcementsFlagService) {

		_announcementsFlagService = announcementsFlagService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _announcementsFlagService.getOSGiServiceIdentifier();
	}

	@Override
	public AnnouncementsFlagService getWrappedService() {
		return _announcementsFlagService;
	}

	@Override
	public void setWrappedService(
		AnnouncementsFlagService announcementsFlagService) {

		_announcementsFlagService = announcementsFlagService;
	}

	private AnnouncementsFlagService _announcementsFlagService;

}