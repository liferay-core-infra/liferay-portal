/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotRequestExecutor;

/**
 * @author Michael C. Han
 */
public class SnapshotRequestExecutorFixture {

	public SnapshotRequestExecutor getSnapshotRequestExecutor() {
		return _snapshotRequestExecutor;
	}

	public void setUp() {
		_snapshotRequestExecutor = new ElasticsearchSnapshotRequestExecutor() {
			{
				createSnapshotRepositoryRequestExecutor =
					_createCreateSnapshotRepositoryRequestExecutor(
						_elasticsearchConnectionManager);
				createSnapshotRequestExecutor =
					_createCreateSnapshotRequestExecutor(
						_elasticsearchConnectionManager);
				deleteSnapshotRequestExecutor =
					_createDeleteSnapshotRequestExecutor(
						_elasticsearchConnectionManager);
				getSnapshotRepositoriesRequestExecutor =
					_createGetSnapshotRepositoriesRequestExecutor(
						_elasticsearchConnectionManager);
				getSnapshotsRequestExecutor =
					_createGetSnapshotsRequestExecutor(
						_elasticsearchConnectionManager);
				restoreSnapshotRequestExecutor =
					_createRestoreSnapshotRequestExecutor(
						_elasticsearchConnectionManager);
			}
		};
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		_elasticsearchConnectionManager = elasticsearchConnectionManager;
	}

	private CreateSnapshotRepositoryRequestExecutor
		_createCreateSnapshotRepositoryRequestExecutor(
			ElasticsearchConnectionManager elasticsearchConnectionManager) {

		CreateSnapshotRepositoryRequestExecutor
			createSnapshotRepositoryRequestExecutor =
				new CreateSnapshotRepositoryRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			createSnapshotRepositoryRequestExecutor,
			"_elasticsearchConnectionManager", elasticsearchConnectionManager);

		return createSnapshotRepositoryRequestExecutor;
	}

	private CreateSnapshotRequestExecutor _createCreateSnapshotRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		CreateSnapshotRequestExecutor createSnapshotRequestExecutor =
			new CreateSnapshotRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			createSnapshotRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return createSnapshotRequestExecutor;
	}

	private DeleteSnapshotRequestExecutor _createDeleteSnapshotRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		DeleteSnapshotRequestExecutor deleteSnapshotRequestExecutor =
			new DeleteSnapshotRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			deleteSnapshotRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return deleteSnapshotRequestExecutor;
	}

	private GetSnapshotRepositoriesRequestExecutor
		_createGetSnapshotRepositoriesRequestExecutor(
			ElasticsearchConnectionManager elasticsearchConnectionManager) {

		GetSnapshotRepositoriesRequestExecutor
			getSnapshotRepositoriesRequestExecutor =
				new GetSnapshotRepositoriesRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getSnapshotRepositoriesRequestExecutor,
			"_elasticsearchConnectionManager", elasticsearchConnectionManager);

		return getSnapshotRepositoriesRequestExecutor;
	}

	private GetSnapshotsRequestExecutor _createGetSnapshotsRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		GetSnapshotsRequestExecutor getSnapshotsRequestExecutor =
			new GetSnapshotsRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getSnapshotsRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return getSnapshotsRequestExecutor;
	}

	private RestoreSnapshotRequestExecutor
		_createRestoreSnapshotRequestExecutor(
			ElasticsearchConnectionManager elasticsearchConnectionManager) {

		RestoreSnapshotRequestExecutor restoreSnapshotRequestExecutor =
			new RestoreSnapshotRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			restoreSnapshotRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return restoreSnapshotRequestExecutor;
	}

	private ElasticsearchConnectionManager _elasticsearchConnectionManager;
	private SnapshotRequestExecutor _snapshotRequestExecutor;

}