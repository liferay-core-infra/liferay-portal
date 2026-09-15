/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.source.formatter.SourceFormatterArgs;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author Jorge Avalos
 */
public class JavaUpgradeMissingCTCollectionIdDuringUpdateCheckTest
	extends BaseSourceProcessorTestCase {

	@Test
	public void testUpgradeMissingCTCollectionIdDuringUpdate()
		throws Exception {

		test(
			SourceProcessorTestParameters.create(
				"upgrade/UpgradeMissingCTCollectionIdDuringUpdate.testjava"
			).addExpectedMessage(
				"Missing \"ctCollectionId\" in where clause during update", 21
			).addExpectedMessage(
				"Missing \"ctCollectionId\" in where clause during update", 28
			));
	}

	@Override
	protected SourceFormatterArgs getSourceFormatterArgs() {
		SourceFormatterArgs sourceFormatterArgs =
			super.getSourceFormatterArgs();

		sourceFormatterArgs.setCurrentBranchAddedFileNames(
			Arrays.asList(
				"upgrade/UpgradeMissingCTCollectionIdDuringUpdate.java"));
		sourceFormatterArgs.setSourceFormatterProperties(
			Arrays.asList(
				"source.check." +
					"JavaUpgradeMissingCTCollectionIdDuringUpdateCheck." +
						"enabled=true"));

		return sourceFormatterArgs;
	}

}