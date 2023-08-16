/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.background.task;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.SearchEngineHelperUtil;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSenderUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.index.SyncReindexManager;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author Andrew Betts
 */
public class ReindexSingleIndexerBackgroundTaskExecutor
	extends BaseReindexBackgroundTaskExecutor {

	public ReindexSingleIndexerBackgroundTaskExecutor(
		Snapshot<SyncReindexManager> syncReindexManagerSnapshot,
		ServiceTrackerList<Indexer<?>> systemIndexers) {

		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_TASK_NAME);

		_syncReindexManagerSnapshot = syncReindexManagerSnapshot;
		_systemIndexers = systemIndexers;
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return new ReindexSingleIndexerBackgroundTaskExecutor(
			_syncReindexManagerSnapshot, _systemIndexers);
	}

	@Override
	public String generateLockKey(BackgroundTask backgroundTask) {
		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		String className = (String)taskContextMap.get("className");

		if (Validator.isNotNull(className)) {
			return className;
		}

		return super.generateLockKey(backgroundTask);
	}

	@Override
	protected void reindex(
			String className, long[] companyIds, String executionMode)
		throws Exception {

		Indexer<?> indexer = IndexerRegistryUtil.getIndexer(className);

		if (indexer == null) {
			return;
		}

		SearchEngineHelper searchEngineHelper =
			SearchEngineHelperUtil.getSearchEngineHelper();

		SearchEngine searchEngine = searchEngineHelper.getSearchEngine();

		boolean systemIndexer = _isSystemIndexer(indexer);

		for (long companyId : companyIds) {
			if (((companyId == CompanyConstants.SYSTEM) && !systemIndexer) ||
				((companyId != CompanyConstants.SYSTEM) && systemIndexer)) {

				continue;
			}

			ReindexStatusMessageSenderUtil.sendStatusMessage(
				ReindexBackgroundTaskConstants.SINGLE_START, companyId,
				companyIds);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Start reindexing company ", companyId,
						" for class name ", className, " with execution mode ",
						executionMode));
			}

			CTSQLModeThreadLocal.CTSQLMode ctSQLMode =
				CTSQLModeThreadLocal.getCTSQLMode();

			try {
				CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(
					CTSQLModeThreadLocal.CTSQLMode.CT_ALL);

				searchEngine.initialize(companyId);

				Date date = null;

				if (_isExecuteSyncReindex(executionMode)) {
					date = new Date();

					Thread.sleep(1000);
				}
				else {
					IndexWriterHelperUtil.deleteEntityDocuments(
						companyId, className, true);
				}

				indexer.reindex(new String[] {String.valueOf(companyId)});

				if (_isExecuteSyncReindex(executionMode)) {
					SyncReindexManager syncReindexManager =
						_syncReindexManagerSnapshot.get();

					syncReindexManager.deleteStaleDocuments(
						companyId, date, Collections.singleton(className));
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}
			finally {
				CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(ctSQLMode);

				ReindexStatusMessageSenderUtil.sendStatusMessage(
					ReindexBackgroundTaskConstants.SINGLE_END, companyId,
					companyIds);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Finished reindexing company ", companyId,
							" for class name ", className,
							" with execution mode ", executionMode));
				}
			}
		}
	}

	private boolean _isExecuteSyncReindex(String executionMode) {
		if ((_syncReindexManagerSnapshot.get() != null) &&
			(executionMode != null) && executionMode.equals("sync")) {

			return true;
		}

		return false;
	}

	private boolean _isSystemIndexer(Indexer<?> indexer) {
		if (_systemIndexers.size() > 0) {
			for (Indexer<?> systemIndexer : _systemIndexers) {
				if (indexer.equals(systemIndexer)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReindexSingleIndexerBackgroundTaskExecutor.class);

	private final Snapshot<SyncReindexManager> _syncReindexManagerSnapshot;
	private final ServiceTrackerList<Indexer<?>> _systemIndexers;

}