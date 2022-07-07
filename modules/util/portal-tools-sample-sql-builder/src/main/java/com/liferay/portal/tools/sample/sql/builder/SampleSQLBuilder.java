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

		// Generic

		File tempDir = new File(BenchmarksPropsValues.OUTPUT_DIR, "temp");

		tempDir.mkdirs();

		Reader reader = mergeSQLTemplates();

		try {

			// Specific

			compressSQLTemplate(reader, tempDir);

			// Merge

			if (BenchmarksPropsValues.OUTPUT_MERGE) {
				File sqlFile = new File(
					BenchmarksPropsValues.OUTPUT_DIR,
					"sample-" + BenchmarksPropsValues.DB_TYPE + ".sql");

				FileUtil.delete(sqlFile);

				mergeSQL(tempDir, sqlFile);
			}
			else {
				File outputDir = new File(
					BenchmarksPropsValues.OUTPUT_DIR, "sqlfiles");

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

	protected void compressSQLTemplate(
			DB db, File directory, Map<String, Writer> sqlWriters,
			Map<String, StringBundler> sqls, String sqlTemplate)
		throws IOException, SQLException {

		if (sqlTemplate.startsWith("create")) {
			compressSQLTemplate(db, directory, sqlWriters, sqlTemplate);

			return;
		}

		sqlTemplate = sqlTemplate.substring(12);

		String tableName = sqlTemplate.substring(0, sqlTemplate.indexOf(' '));

		int index = sqlTemplate.indexOf(" values ") + 8;

		StringBundler sb = sqls.get(tableName);

		if ((sb == null) || (sb.index() == 0)) {
			sb = new StringBundler();

			sqls.put(tableName, sb);

			sb.append("insert into ");
			sb.append(sqlTemplate.substring(0, index));
			sb.append(StringPool.NEW_LINE);
		}
		else {
			sb.append(StringPool.COMMA);
			sb.append(StringPool.NEW_LINE);
		}

		String values = sqlTemplate.substring(index, sqlTemplate.length() - 1);

		sb.append(values);

		if (sb.index() >= BenchmarksPropsValues.OPTIMIZE_BUFFER_SIZE) {
			sb.append(StringPool.SEMICOLON);
			sb.append(StringPool.NEW_LINE);

			sqlTemplate = db.buildSQL(sb.toString());

			sb.setIndex(0);

			writeToSQLFile(directory, tableName, sqlWriters, sqlTemplate);
		}
	}

	protected void compressSQLTemplate(
			DB db, File directory, Map<String, Writer> sqlWriters,
			String createSQLTemplate)
		throws IOException, SQLException {

		String tableName = null;

		if (createSQLTemplate.startsWith("create table ")) {
			tableName = createSQLTemplate.substring(
				13, createSQLTemplate.indexOf(StringPool.OPEN_PARENTHESIS) - 1);
		}
		else {
			int index = createSQLTemplate.indexOf(" on ");

			tableName = createSQLTemplate.substring(
				index + 4,
				createSQLTemplate.indexOf(StringPool.OPEN_PARENTHESIS) - 1);
		}

		createSQLTemplate =
			db.buildSQL(createSQLTemplate) + StringPool.NEW_LINE;

		writeToSQLFile(directory, tableName, sqlWriters, createSQLTemplate);
	}

	protected void compressSQLTemplate(Reader reader, File dir)
		throws Exception {

		DB db = DBManagerUtil.getDB(BenchmarksPropsValues.DB_TYPE, null);

		if ((BenchmarksPropsValues.DB_TYPE == DBType.MARIADB) ||
			(BenchmarksPropsValues.DB_TYPE == DBType.MYSQL)) {

			db = new SampleMySQLDB(db.getMajorVersion(), db.getMinorVersion());
		}

		Map<String, Writer> sqlWriters = new HashMap<>();
		Map<String, StringBundler> sqls = new HashMap<>();
		List<String> counterSQLs = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader)) {

			String s = null;

			while ((_freeMarkerThrowable == null) &&
				   ((s = unsyncBufferedReader.readLine()) != null)) {

				s = s.trim();

				if (s.length() > 0) {
					if (s.startsWith("create") ||
						s.startsWith("insert into ")) {

						if (!s.endsWith(");")) {
							StringBundler sb = new StringBundler();

							while (!s.endsWith(");")) {
								sb.append(s);
								sb.append(StringPool.NEW_LINE);

								s = unsyncBufferedReader.readLine();
							}

							sb.append(s);

							s = sb.toString();
						}

						compressSQLTemplate(db, dir, sqlWriters, sqls, s);
					}
					else if (!s.contains("##")) {
						counterSQLs.add(s);
					}
				}
			}
		}

		if (_freeMarkerThrowable != null) {
			throw new Exception(
				"Unable to process FreeMarker template ", _freeMarkerThrowable);
		}

		for (Map.Entry<String, StringBundler> entry : sqls.entrySet()) {
			String tableName = entry.getKey();
			StringBundler sb = entry.getValue();

			if (sb.index() > 0) {
				String sql = db.buildSQL(sb.toString());

				writeToSQLFile(dir, tableName, sqlWriters, sql);
			}

			try (Writer sqlWriter = sqlWriters.remove(tableName)) {
				sqlWriter.write(StringPool.SEMICOLON);
				sqlWriter.write(StringPool.NEW_LINE);
			}
		}

		try (Writer counterSQLWriter = new FileWriter(
				new File(dir, "Counter.sql"), true)) {

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

	protected void mergeSQL(File inputDir, File outputSQLFile)
		throws IOException {

		FileOutputStream outputSQLFileOutputStream = new FileOutputStream(
			outputSQLFile);

		try (FileChannel outputFileChannel =
				outputSQLFileOutputStream.getChannel()) {

			File counterSQLFile = null;

			for (File inputFile : inputDir.listFiles()) {
				String inputFileName = inputFile.getName();

				if (inputFileName.equals("Counter.sql")) {
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

	protected Reader mergeSQLTemplates() {
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
								"sample.sql")))) {

					_loadCreateSQLTemplates(sampleSQLWriter);

					_generateEditSQLTemplates(csvFileWriter, sampleSQLWriter);
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

	protected void writeToSQLFile(
			File dir, String tableName, Map<String, Writer> sqlWriters,
			String sql)
		throws IOException {

		Writer sqlWriter = sqlWriters.get(tableName);

		if (sqlWriter == null) {
			File file = new File(dir, tableName + ".sql");

			sqlWriter = createFileWriter(file);

			sqlWriters.put(tableName, sqlWriter);
		}

		sqlWriter.write(sql);

		sqlWriter.flush();
	}

	private void _generateEditSQLTemplates(
			CSVFileWriter csvFileWriter, Writer sampleSQLWriter)
		throws Exception {

		FreeMarkerUtil.process(
			BenchmarksPropsValues.SCRIPT,
			HashMapBuilder.<String, Object>put(
				"csvFileWriter", csvFileWriter
			).put(
				"dataFactory", new DataFactory()
			).build(),
			sampleSQLWriter);
	}

	private void _loadCreateSQLTemplate(InputStream inputStream, Writer writer)
		throws IOException {

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(inputStream))) {

			String line;

			while ((line = reader.readLine()) != null) {
				writer.append(line);
				writer.append(System.lineSeparator());
			}
		}
	}

	private void _loadCreateSQLTemplates(Writer writer) throws IOException {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		for (String sqlFileName : _createSQLTemplateFileNames) {
			if (sqlFileName.contains("META-INF")) {
				Enumeration<URL> enumeration = classLoader.getResources(
					sqlFileName);

				while (enumeration.hasMoreElements()) {
					URL url = enumeration.nextElement();

					_loadCreateSQLTemplate(url.openStream(), writer);
				}
			}
			else {
				_loadCreateSQLTemplate(
					classLoader.getResourceAsStream(sqlFileName), writer);
			}
		}

		writer.flush();
	}

	private static final int _PIPE_BUFFER_SIZE = 16 * 1024 * 1024;

	private static final int _WRITER_BUFFER_SIZE = 16 * 1024;

	private static final List<String> _createSQLTemplateFileNames =
		Arrays.asList(
			"com/liferay/portal/tools/sql/dependencies/portal-tables.sql",
			"com/liferay/portal/tools/sql/dependencies/portal-data-common.sql",
			"com/liferay/portal/tools/sql/dependencies/portal-data-counter.sql",
			"com/liferay/portal/tools/sql/dependencies/indexes.sql",
			"META-INF/sql/tables.sql", "META-INF/sql/indexes.sql");

	private volatile Throwable _freeMarkerThrowable;

}