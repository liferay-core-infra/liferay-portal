/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.BadColumnNameEntryPersistence;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class BadColumnNameEntryPersistenceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testFilterFindByMethodsAsDifferentUser() throws Exception {
		BadColumnNameEntry badColumnNameEntry =
			_badColumnNameEntryPersistence.create(RandomTestUtil.randomLong());

		badColumnNameEntry.setType("test");

		_badColumnNameEntry = _badColumnNameEntryPersistence.update(
			badColumnNameEntry);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));

			_badColumnNameEntryPersistence.filterFindByType(
				_badColumnNameEntry.getType());

			_badColumnNameEntryPersistence.filterFindByType_PrevAndNext(
				_badColumnNameEntry.getBadColumnNameEntryId(),
				_badColumnNameEntry.getType(), null);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@DeleteAfterTestRun
	private BadColumnNameEntry _badColumnNameEntry;

	@Inject
	private BadColumnNameEntryPersistence _badColumnNameEntryPersistence;

	@DeleteAfterTestRun
	private User _user;

}