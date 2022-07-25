/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.store.s3;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FileImpl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Bowerman
 */
public class S3FileCacheTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_s3FileCache = Mockito.spy(new S3FileCache());

		ReflectionTestUtil.setFieldValue(
			_s3FileCache, "_cacheDirCleanUpExpunge", new AtomicInteger(7));
		ReflectionTestUtil.setFieldValue(
			_s3FileCache, "_cacheDirCleanUpFrequency", new AtomicInteger(5));
	}

	@Test
	public void testCleanUpCacheFilesCalledExactlyOnceWhenExactlyCacheDirCleanUpFrequency() {
		for (int i = 0; i < 5; i++) {
			_s3FileCache.cleanUpCacheFiles();
		}

		Mockito.verify(
			_s3FileCache, Mockito.times(1)
		).cleanUpCacheFiles(
			Mockito.any(), Mockito.anyLong()
		);
	}

	@Test
	public void testCleanUpCacheFilesCalledExactlyOnceWhenFewerThanTwiceCacheDirCleanUpFrequency() {
		for (int i = 0; i < 9; i++) {
			_s3FileCache.cleanUpCacheFiles();
		}

		Mockito.verify(
			_s3FileCache, Mockito.times(1)
		).cleanUpCacheFiles(
			Mockito.any(), Mockito.anyLong()
		);
	}

	@Test
	public void testCleanUpCacheFilesCalledExactlyTwiceWhenExactlyTwiceCacheDirCleanUpFrequency() {
		for (int i = 0; i < 10; i++) {
			_s3FileCache.cleanUpCacheFiles();
		}

		Mockito.verify(
			_s3FileCache, Mockito.times(2)
		).cleanUpCacheFiles(
			Mockito.any(), Mockito.anyLong()
		);
	}

	@Test
	public void testCleanUpCacheFilesNotCalledWhenFewerThanCacheDirCleanUpFrequency() {
		for (int i = 0; i < 4; i++) {
			_s3FileCache.cleanUpCacheFiles();
		}

		Mockito.verify(
			_s3FileCache, Mockito.never()
		).cleanUpCacheFiles(
			Mockito.any(), Mockito.anyLong()
		);
	}

	@Test
	public void testMultithreadedCleanUpCacheFilesCalledExactlyOnceWhenFewerThanTwiceCacheDirCleanUpFrequency()
		throws Exception {

		Lock lock = new ReentrantLock();

		lock.lock();

		FileImpl fileImpl = Mockito.mock(FileImpl.class);

		ReflectionTestUtil.setFieldValue(FileUtil.class, "_file", fileImpl);

		Mockito.doAnswer(
			invocationOnMock -> {
				lock.lock();

				lock.unlock();

				return null;
			}
		).when(
			_s3FileCache
		).cleanUpCacheFiles(
			Mockito.any(), Mockito.anyLong()
		);

		for (int i = 0; i < 9; i++) {
			Thread thread = new Thread(_s3FileCache::cleanUpCacheFiles);

			thread.start();
		}

		Thread.sleep(2000);

		lock.unlock();

		Thread.sleep(2000);

		Mockito.verify(
			_s3FileCache, Mockito.times(1)
		).cleanUpCacheFiles(
			Mockito.any(), Mockito.anyLong()
		);
	}

	private static S3FileCache _s3FileCache;

	static {
		ReflectionTestUtil.setFieldValue(
			FileUtil.class, "_file", FileImpl.getInstance());
	}

}