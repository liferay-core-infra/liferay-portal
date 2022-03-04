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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.petra.io.OutputStreamWriter;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncBufferedWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.freemarker.FreeMarkerUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.tools.ToolDependencies;
import com.liferay.portal.tools.sample.sql.builder.constants.SampleSQLBuilderConstants;
import com.liferay.portal.tools.sample.sql.builder.io.CharPipe;
import com.liferay.portal.tools.sample.sql.builder.io.UnsyncTeeWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import java.net.URL;

import java.nio.channels.FileChannel;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class SampleSQLBuilder {

	public SampleSQLBuilder() {
		ToolDependencies.wireBasic();

		Class<?> clazz = getClass();

		_classLoader = clazz.getClassLoader();

		// Generic

		File tempDir = new File(
			BenchmarksPropsValues.OUTPUT_DIR,
			SampleSQLBuilderConstants.COMPRESS_SQL_TEMP_FOLDER_NAME);

		tempDir.mkdirs();

		Reader reader = generateSQL();

		try {

			// Specific

			compressSQL(reader, tempDir);

			// Merge

			if (BenchmarksPropsValues.OUTPUT_MERGE) {
				File sqlFile = new File(
					BenchmarksPropsValues.OUTPUT_DIR,
					SampleSQLBuilderConstants.MERGE_SQL_FILE_NAME_PREFIX +
						BenchmarksPropsValues.DB_TYPE +
							SampleSQLBuilderConstants.SQL_FILE_SUFFIX);

				FileUtil.delete(sqlFile);

				mergeSQL(tempDir, sqlFile);
			}
			else {
				File outputDir = new File(
					BenchmarksPropsValues.OUTPUT_DIR,
					SampleSQLBuilderConstants.OUTPUT_FOLDER_NAME);

				FileUtil.deltree(outputDir);

				if (!tempDir.renameTo(outputDir)) {

					// This will only happen when temp and output directories
					// are on different file systems

					FileUtil.copyDirectory(tempDir, outputDir);
				}
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		finally {
			FileUtil.deltree(tempDir);
		}
	}

	protected void compressSQL(
			DB db, File directory, Map<String, Writer> sqlWriters,
			Map<String, StringBundler> sqls, String insertSQL)
		throws IOException, SQLException {

		String tableName = insertSQL.substring(0, insertSQL.indexOf(' '));

		int index =
			insertSQL.indexOf(SampleSQLBuilderConstants.INSERT_SQL_FLAG) + 8;

		StringBundler sb = sqls.get(tableName);

		if ((sb == null) || (sb.index() == 0)) {
			sb = new StringBundler();

			sqls.put(tableName, sb);

			sb.append(SampleSQLBuilderConstants.INSERT_SQL_PREFIX);
			sb.append(insertSQL.substring(0, index));
			sb.append(StringPool.NEW_LINE);
		}
		else {
			sb.append(StringPool.COMMA);
			sb.append(StringPool.NEW_LINE);
		}

		String values = insertSQL.substring(index, insertSQL.length() - 1);

		sb.append(values);

		if (sb.index() >= BenchmarksPropsValues.OPTIMIZE_BUFFER_SIZE) {
			sb.append(StringPool.SEMICOLON);
			sb.append(StringPool.NEW_LINE);

			insertSQL = db.buildSQL(sb.toString());

			sb.setIndex(0);

			writeToSQLFile(directory, tableName, sqlWriters, insertSQL);
		}
	}

	protected void compressSQL(
			DB db, File directory, Map<String, Writer> sqlWriters,
			String createSQL)
		throws IOException, SQLException {

		String tableName;

		if (createSQL.startsWith(
				SampleSQLBuilderConstants.CREATE_TABLE_SQL_PREFIX)) {

			tableName = createSQL.substring(
				13, createSQL.indexOf(StringPool.OPEN_PARENTHESIS) - 1);
		}
		else {
			int index = createSQL.indexOf(
				SampleSQLBuilderConstants.INDEX_SQL_FLAG);

			tableName = createSQL.substring(
				index + 4, createSQL.indexOf(StringPool.OPEN_PARENTHESIS) - 1);
		}

		createSQL = db.buildSQL(createSQL) + StringPool.NEW_LINE;

		writeToSQLFile(directory, tableName, sqlWriters, createSQL);
	}

	protected void compressSQL(Reader reader, File dir) throws Exception {
		DB db = DBManagerUtil.getDB(BenchmarksPropsValues.DB_TYPE, null);

		if ((BenchmarksPropsValues.DB_TYPE == DBType.MARIADB) ||
			(BenchmarksPropsValues.DB_TYPE == DBType.MYSQL)) {

			db = new SampleMySQLDB(db.getMajorVersion(), db.getMinorVersion());
		}

		Map<String, Writer> sqlWriters = new HashMap<>();
		Map<String, StringBundler> insertSQLs = new HashMap<>();
		List<String> counterSQLs = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader)) {

			String s = null;

			while ((_freeMarkerThrowable == null) &&
				   ((s = unsyncBufferedReader.readLine()) != null)) {

				s = s.trim();

				if (s.length() > 0) {
					if (s.startsWith(
							SampleSQLBuilderConstants.CREATE_SQL_PREFIX)) {

						compressSQL(
							db, dir, sqlWriters,
							_mergeSingleSQLTemplate(s, unsyncBufferedReader));
					}
					else if (s.startsWith(
								SampleSQLBuilderConstants.INSERT_SQL_PREFIX)) {

						s = _mergeSingleSQLTemplate(s, unsyncBufferedReader);

						compressSQL(
							db, dir, sqlWriters, insertSQLs, s.substring(12));
					}
					else if (!s.contains(StringPool.POUND)) {
						counterSQLs.add(s);
					}
				}
			}
		}

		if (_freeMarkerThrowable != null) {
			throw new Exception(
				"Unable to process FreeMarker template ", _freeMarkerThrowable);
		}

		for (Map.Entry<String, StringBundler> entry : insertSQLs.entrySet()) {
			String tableName = entry.getKey();
			StringBundler sb = entry.getValue();

			if (sb.index() > 0) {
				String insertSQL = db.buildSQL(sb.toString());

				writeToSQLFile(dir, tableName, sqlWriters, insertSQL);
			}

			try (Writer sqlWriter = sqlWriters.remove(tableName)) {
				sqlWriter.write(StringPool.SEMICOLON);
				sqlWriter.write(StringPool.NEW_LINE);
			}
		}

		try (Writer counterSQLWriter = new FileWriter(
				new File(dir, SampleSQLBuilderConstants.COUNTER_SQL_FILE_NAME),
				true)) {

			for (String counterSQL : counterSQLs) {
				counterSQL = db.buildSQL(counterSQL);

				counterSQLWriter.write(counterSQL);

				counterSQLWriter.write(StringPool.NEW_LINE);
			}
		}
	}

	protected Writer createFileWriter(File file) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		Writer writer = new OutputStreamWriter(fileOutputStream);

		return new UnsyncBufferedWriter(writer, _WRITER_BUFFER_SIZE);
	}

	protected Reader generateSQL() {
		CharPipe charPipe = new CharPipe(_PIPE_BUFFER_SIZE);

		Thread thread = new Thread(
			() -> {
				try (CSVFileWriter csvFileWriter = new CSVFileWriter();
					Writer sampleSQLWriter = new UnsyncTeeWriter(
						new UnsyncBufferedWriter(
							charPipe.getWriter(), _WRITER_BUFFER_SIZE),
						createFileWriter(
							new File(
								BenchmarksPropsValues.OUTPUT_DIR,
								SampleSQLBuilderConstants.
									SQL_TEMPLATE_FILE_NAME)))) {

					for (String sqlFileName : _createSQLStatementTemplateList) {
						if (sqlFileName.contains(
								SampleSQLBuilderConstants.CORE_SQL_FILE_DIR)) {

							_mergeCreateSQLTemplate(
								_classLoader.getResourceAsStream(sqlFileName),
								sampleSQLWriter);
						}
						else {
							Enumeration<URL> enumeration =
								_classLoader.getResources(sqlFileName);

							while (enumeration.hasMoreElements()) {
								URL url = enumeration.nextElement();

								_mergeCreateSQLTemplate(
									url.openStream(), sampleSQLWriter);
							}
						}
					}

					FreeMarkerUtil.process(
						BenchmarksPropsValues.SCRIPT,
						HashMapBuilder.<String, Object>put(
							SampleSQLBuilderConstants.
								FTL_CONTEXT_CSV_WRITER_REFERENCE_NAME,
							csvFileWriter
						).put(
							SampleSQLBuilderConstants.
								FTL_CONTEXT_DATAFACTORY_REFERENCE_NAME,
							new DataFactory()
						).build(),
						sampleSQLWriter);
				}
				catch (Throwable throwable) {
					_freeMarkerThrowable = throwable;
				}
				finally {
					charPipe.close();
				}
			});

		thread.start();

		return charPipe.getReader();
	}

	protected void mergeSQL(File inputDir, File outputSQLFile)
		throws IOException {

		FileOutputStream outputSQLFileOutputStream = new FileOutputStream(
			outputSQLFile);

		try (FileChannel outputFileChannel =
				outputSQLFileOutputStream.getChannel()) {

			File counterSQLFile = null;

			for (File inputFile : inputDir.listFiles()) {
				String inputFileName = inputFile.getName();

				if (inputFileName.equals(
						SampleSQLBuilderConstants.COUNTER_SQL_FILE_NAME)) {

					counterSQLFile = inputFile;

					continue;
				}

				mergeSQL(inputFile, outputFileChannel);
			}

			if (counterSQLFile != null) {
				mergeSQL(counterSQLFile, outputFileChannel);
			}
		}
	}

	protected void mergeSQL(File inputFile, FileChannel outputFileChannel)
		throws IOException {

		FileInputStream inputFileInputStream = new FileInputStream(inputFile);

		try (FileChannel inputFileChannel = inputFileInputStream.getChannel()) {
			inputFileChannel.transferTo(
				0, inputFileChannel.size(), outputFileChannel);
		}

		inputFile.delete();
	}

	protected void writeToSQLFile(
			File dir, String tableName, Map<String, Writer> sqlWriters,
			String sql)
		throws IOException {

		Writer sqlWriter = sqlWriters.get(tableName);

		if (sqlWriter == null) {
			File file = new File(
				dir, tableName + SampleSQLBuilderConstants.SQL_FILE_SUFFIX);

			sqlWriter = createFileWriter(file);

			sqlWriters.put(tableName, sqlWriter);
		}

		sqlWriter.write(sql);

		sqlWriter.flush();
	}

	private void _mergeCreateSQLTemplate(InputStream inputStream, Writer writer)
		throws Exception {

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(inputStream))) {

			String line;

			while ((line = reader.readLine()) != null) {
				writer.append(line);
				writer.append(System.lineSeparator());
			}
		}
	}

	private String _mergeSingleSQLTemplate(
			String line, UnsyncBufferedReader unsyncBufferedReader)
		throws Exception {

		if (!line.endsWith(SampleSQLBuilderConstants.SQL_END_FLAG)) {
			StringBundler sb = new StringBundler();

			while (!line.endsWith(SampleSQLBuilderConstants.SQL_END_FLAG)) {
				sb.append(line);
				sb.append(StringPool.NEW_LINE);

				line = unsyncBufferedReader.readLine();
			}

			sb.append(line);

			line = sb.toString();
		}

		return line;
	}

	private static final int _PIPE_BUFFER_SIZE = 16 * 1024 * 1024;

	private static final int _WRITER_BUFFER_SIZE = 16 * 1024;

	private final ClassLoader _classLoader;
	private final List<String> _createSQLStatementTemplateList = Arrays.asList(
		SampleSQLBuilderConstants.CORE_SQL_FILE_NAME,
		SampleSQLBuilderConstants.CORE_COMMON_SQL_FILE_NAME,
		SampleSQLBuilderConstants.CORE_CUNTER_SQL_FILE_NAME,
		SampleSQLBuilderConstants.CORE_INDEX_SQL_FILE_NAME,
		SampleSQLBuilderConstants.MODULE_TABLE_SQL_FILE_NAME,
		SampleSQLBuilderConstants.MODULE_INDEX_SQL_FILE_NAME);
	private volatile Throwable _freeMarkerThrowable;

}