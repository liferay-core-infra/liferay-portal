/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.concurrent.DefaultNoticeableFuture;
import com.liferay.portal.kernel.concurrent.FutureListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author Shuyang Zhou
 */
public class InitialRequestSyncUtil {

	public static void registerSyncCallable(
		long companyId, Callable<?> syncCallable) {

		_addFutureListener(
			companyId,
			future -> {
				try {
					syncCallable.call();
				}
				catch (Exception exception) {
					_log.error("Unable to sync callable", exception);
				}
			});
	}

	public static void sync(long companyId) {
		DefaultNoticeableFuture<Void> defaultNoticeableFuture =
			_syncCallableDefaultNoticeableFutures.remove(companyId);

		if (defaultNoticeableFuture == null) {
			return;
		}

		defaultNoticeableFuture.run();
	}

	private static void _addFutureListener(
		long companyId, FutureListener<Void> futureListener) {

		DefaultNoticeableFuture<Void> defaultNoticeableFuture =
			_syncCallableDefaultNoticeableFutures.computeIfAbsent(
				companyId, key -> new DefaultNoticeableFuture<>());

		defaultNoticeableFuture.addFutureListener(
			new FutureListener<Void>() {

				@Override
				public void complete(Future<Void> future) {
					futureListener.complete(future);

					defaultNoticeableFuture.removeFutureListener(this);
				}

			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InitialRequestSyncUtil.class);

	private static final Map<Long, DefaultNoticeableFuture<Void>>
		_syncCallableDefaultNoticeableFutures = new ConcurrentHashMap<>();

}