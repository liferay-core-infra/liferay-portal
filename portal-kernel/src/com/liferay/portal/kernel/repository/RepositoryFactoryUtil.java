/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.repository;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Adolfo Pérez
 */
public class RepositoryFactoryUtil {

	public static LocalRepository createLocalRepository(long repositoryId)
		throws PortalException {

		RepositoryFactory repositoryFactory = _repositoryFactorySnapshot.get();

		return repositoryFactory.createLocalRepository(repositoryId);
	}

	public static Repository createRepository(long repositoryId)
		throws PortalException {

		RepositoryFactory repositoryFactory = _repositoryFactorySnapshot.get();

		return repositoryFactory.createRepository(repositoryId);
	}

	public static RepositoryFactory getRepositoryFactory() {
		return _repositoryFactorySnapshot.get();
	}

	private static final Snapshot<RepositoryFactory>
		_repositoryFactorySnapshot = new Snapshot<>(
			RepositoryFactoryUtil.class, RepositoryFactory.class);

}