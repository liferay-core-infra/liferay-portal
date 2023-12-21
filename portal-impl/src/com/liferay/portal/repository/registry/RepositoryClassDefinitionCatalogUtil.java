/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.registry;

import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.Collection;

/**
 * @author Adolfo Pérez
 */
public class RepositoryClassDefinitionCatalogUtil {

	public static Iterable<RepositoryClassDefinition>
		getExternalRepositoryClassDefinitions(long companyId) {

		RepositoryClassDefinitionCatalog repositoryClassDefinitionCatalog =
			_repositoryClassDefinitionCatalogSnapshot.get();

		return repositoryClassDefinitionCatalog.
			getExternalRepositoryClassDefinitions(companyId);
	}

	public static Collection<String> getExternalRepositoryClassNames(
		long companyId) {

		RepositoryClassDefinitionCatalog repositoryClassDefinitionCatalog =
			_repositoryClassDefinitionCatalogSnapshot.get();

		return repositoryClassDefinitionCatalog.getExternalRepositoryClassNames(
			companyId);
	}

	public static RepositoryClassDefinition getRepositoryClassDefinition(
		long companyId, String repositoryTypeKey) {

		RepositoryClassDefinitionCatalog repositoryClassDefinitionCatalog =
			_repositoryClassDefinitionCatalogSnapshot.get();

		return repositoryClassDefinitionCatalog.getRepositoryClassDefinition(
			companyId, repositoryTypeKey);
	}

	private static final Snapshot<RepositoryClassDefinitionCatalog>
		_repositoryClassDefinitionCatalogSnapshot = new Snapshot<>(
			RepositoryClassDefinitionCatalogUtil.class,
			RepositoryClassDefinitionCatalog.class);

}