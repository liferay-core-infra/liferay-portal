/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.announcements.service.impl;

import com.liferay.announcements.kernel.exception.NoSuchFlagException;
import com.liferay.announcements.kernel.model.AnnouncementsFlag;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portlet.announcements.service.base.AnnouncementsFlagLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

/**
 * @author Thiago Moreira
 * @author Raymond Augé
 */
public class AnnouncementsFlagLocalServiceImpl
	extends AnnouncementsFlagLocalServiceBaseImpl {

	@Override
	public AnnouncementsFlag addFlag(long userId, long entryId, int value) {
		long flagId = counterLocalService.increment();

		AnnouncementsFlag flag = announcementsFlagPersistence.create(flagId);

		flag.setUserId(userId);
		flag.setCreateDate(new Date());
		flag.setEntryId(entryId);
		flag.setValue(value);

		return announcementsFlagPersistence.update(flag);
	}

	@Override
	public void deleteFlag(AnnouncementsFlag flag) {
		announcementsFlagPersistence.remove(flag);
	}

	@Override
	public void deleteFlag(long flagId) throws PortalException {
		AnnouncementsFlag flag = announcementsFlagPersistence.findByPrimaryKey(
			flagId);

		deleteFlag(flag);
	}

	@Override
	public void deleteFlags(long entryId) {
		List<AnnouncementsFlag> flags =
			announcementsFlagPersistence.findByEntryId(entryId);

		for (AnnouncementsFlag flag : flags) {
			deleteFlag(flag);
		}
	}

	@Override
	public AnnouncementsFlag getFlag(long userId, long entryId, int value)
		throws PortalException {

		List<AnnouncementsFlag> announcementsFlags =
			announcementsFlagPersistence.findByU_E_V(userId, entryId, value);

		if (announcementsFlags.isEmpty()) {
			throw new NoSuchFlagException(
				StringBundler.concat(
					"No announcements flag was found for user ID ", userId,
					", entry ID ", entryId, " and value ", value));
		}

		if (announcementsFlags.size() > 1) {
			_log.error(
				StringBundler.concat(
					"Duplicate announcements flags were found for user ID ",
					userId, ", entry ID ", entryId, " and value ", value));
		}

		return announcementsFlags.get(announcementsFlags.size() - 1);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnnouncementsFlagLocalServiceImpl.class);

}