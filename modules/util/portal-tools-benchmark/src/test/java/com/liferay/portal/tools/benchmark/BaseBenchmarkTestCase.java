/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.benchmark;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.dao.jdbc.DataSourceFactoryImpl;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactory;
import com.liferay.portal.kernel.util.GetterUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.sql.DataSource;

import org.junit.Before;

/**
 * @author Tina Tian
 */
public abstract class BaseBenchmarkTestCase {

	@Before
	public void setUp() {
		_excludedCompanies = StringUtil.split(
			System.getProperty("benchmark.test.excluded.companies"));
		_driverClassName = System.getProperty(
			"benchmark.test.jdbc.driverClassName");
		_jdbcUrl = System.getProperty("benchmark.test.jdbc.url");
		_jdbcUserName = System.getProperty("benchmark.test.jdbc.username");
		_jdbcPassword = System.getProperty("benchmark.test.jdbc.password");
		_runCount = GetterUtil.getInteger(
			System.getProperty("benchmark.test.run.count"));
		_skipWarmUp = GetterUtil.getBoolean(
			System.getProperty("benchmark.test.skip.warm.up"));
		_threadCount = GetterUtil.getInteger(
			System.getProperty("benchmark.test.thread.count"));
		_userPassword = System.getProperty("benchmark.test.user.password");

		StringBundler sb = new StringBundler(16);

		sb.append("\nCurrent properties:\n	Excluded Companies:");
		sb.append(_excludedCompanies);
		sb.append("\n	JDBC Driver Class Name:");
		sb.append(_driverClassName);
		sb.append("\n	JDBC URL:");
		sb.append(_jdbcUrl);
		sb.append("\n	JDBC User Name:");
		sb.append(_jdbcUserName);
		sb.append("\n	JDBC Password:");
		sb.append(_jdbcPassword);
		sb.append("\n	Run Count:");
		sb.append(_runCount);
		sb.append("\n	Thread Count:");
		sb.append(_threadCount);
		sb.append("\n	User Password:");
		sb.append(_userPassword);

		System.out.println(sb);
	}

	protected String getExcludedCompaniesSQL() {
		if (_excludedCompanies.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		sb.append("company.webId not in (");

		for (int i = 0; i < _excludedCompanies.size(); i++) {
			sb.append("'");
			sb.append(_excludedCompanies.get(i));
			sb.append("'");

			if (i < (_excludedCompanies.size() - 1)) {
				sb.append(",");
			}
		}

		sb.append(") and ");

		return sb.toString();
	}

	protected String getUserPassword() {
		return _userPassword;
	}

	protected void runBenchmarkTest(
			String loadDataSQL,
			UnsafeFunction<ResultSet, String[], Exception>
				loadDataUnsafeFunction,
			Function<String[], BenchmarkTask> benchmarkTaskFunction)
		throws Exception {

		String[][] data = _loadData(loadDataSQL, loadDataUnsafeFunction);

		if (!_skipWarmUp) {
			System.out.println("\nStart warm up data...");

			_runBenchmarkTest(
				data, 1, data.length, index -> index, benchmarkTaskFunction);

			System.out.println("\nWarm up finished!");
		}

		System.out.println("\nStart running test...");

		_runBenchmarkTest(
			data, _threadCount, _runCount, index -> index % data.length,
			benchmarkTaskFunction);

		System.out.println("\nTest run finished!");
	}

	private String[][] _loadData(
			String loadDataSQL,
			UnsafeFunction<ResultSet, String[], Exception>
				loadDataUnsafeFunction)
		throws Exception {

		List<String[]> data = new ArrayList<>();

		System.out.println("\nTest data load SQL : " + loadDataSQL);

		DataSourceFactory dataSourceFactory = new DataSourceFactoryImpl();

		DataSource dataSource = dataSourceFactory.initDataSource(
			_driverClassName, _jdbcUrl, _jdbcUserName, _jdbcPassword,
			StringPool.BLANK);

		try (Connection connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				loadDataSQL);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				data.add(loadDataUnsafeFunction.apply(resultSet));
			}
		}

		return data.toArray(new String[0][0]);
	}

	private void _runBenchmarkTest(
			String[][] data, int threadCount, int runCount,
			Function<Integer, Integer> indexFunction,
			Function<String[], BenchmarkTask> benchmarkTaskFunction)
		throws Exception {

		ExecutorService executorService = new ThreadPoolExecutor(
			threadCount, threadCount, 0, TimeUnit.SECONDS,
			new LinkedBlockingDeque<>());

		List<Future<Void>> futures = new ArrayList<>();

		Statistics statistics = new Statistics(runCount);

		statistics.start();

		for (int i = 0; i < runCount; i++) {
			String[] testData = data[indexFunction.apply(i)];

			futures.add(
				executorService.submit(
					() -> {
						BenchmarkTask benchmarkTask =
							benchmarkTaskFunction.apply(testData);

						statistics.record(benchmarkTask.execute());

						return null;
					}));
		}

		for (Future<Void> future : futures) {
			future.get();
		}

		statistics.finish();
	}

	private String _driverClassName;
	private List<String> _excludedCompanies;
	private String _jdbcPassword;
	private String _jdbcUrl;
	private String _jdbcUserName;
	private int _runCount;
	private boolean _skipWarmUp;
	private int _threadCount;
	private String _userPassword;

}