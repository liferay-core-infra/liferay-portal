/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.ArithmeticEntry;
import com.liferay.portal.tools.service.builder.test.model.ArithmeticEntryTable;
import com.liferay.portal.tools.service.builder.test.service.persistence.ArithmeticEntryPersistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class ArithmeticEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@After
	public void tearDown() throws Exception {
		for (ArithmeticEntry arithmeticEntry : _arithmeticEntries) {
			_arithmeticEntryPersistence.remove(arithmeticEntry);
		}

		_arithmeticEntries.clear();
	}

	@Test
	public void testDivideByDecimal() {
		ArithmeticEntry arithmeticEntry = _arithmeticEntryPersistence.create(
			RandomTestUtil.nextLong());

		arithmeticEntry.setNumber1(3L);
		arithmeticEntry.setNumber2(2L);

		_arithmeticEntries.add(
			_arithmeticEntryPersistence.update(arithmeticEntry));

		Assert.assertEquals(
			Collections.singletonList(1.5),
			_arithmeticEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLFunctionFactoryUtil.divide(
						ArithmeticEntryTable.INSTANCE.number1,
						ArithmeticEntryTable.INSTANCE.number2
					).as(
						"alias", Double.class
					)
				).from(
					ArithmeticEntryTable.INSTANCE
				)));
	}

	@Test
	public void testDivideByNull() {
		ArithmeticEntry arithmeticEntry = _arithmeticEntryPersistence.create(
			RandomTestUtil.nextLong());

		arithmeticEntry.setNumber1(3L);

		_arithmeticEntries.add(
			_arithmeticEntryPersistence.update(arithmeticEntry));

		Assert.assertEquals(
			Collections.singletonList(null),
			_arithmeticEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLFunctionFactoryUtil.divide(
						ArithmeticEntryTable.INSTANCE.number1,
						ArithmeticEntryTable.INSTANCE.number2
					).as(
						"alias", Double.class
					)
				).from(
					ArithmeticEntryTable.INSTANCE
				)));
	}

	private final List<ArithmeticEntry> _arithmeticEntries = new ArrayList<>();

	@Inject
	private ArithmeticEntryPersistence _arithmeticEntryPersistence;

}