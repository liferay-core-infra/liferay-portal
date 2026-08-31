/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.test.rule.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class TransactionFlushDBPartitionTest extends BaseDBPartitionTestCase {

	@Test
	public void testSetCompanyIdWithSafeCloseableFlushesSessionOnClose()
		throws Throwable {

		long defaultCompanyId = PortalInstancePool.getDefaultCompanyId();
		String name = RandomTestUtil.randomString();

		TransactionInvokerUtil.invoke(
			_transactionConfig,
			() -> {
				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							defaultCompanyId)) {

					_addRole(defaultCompanyId, name);
				}

				return null;
			});

		try {
			Assert.assertFalse(_hasRole(TestPropsValues.getCompanyId(), name));
			Assert.assertTrue(_hasRole(defaultCompanyId, name));
		}
		finally {
			_deleteRole(TestPropsValues.getCompanyId(), name);
			_deleteRole(defaultCompanyId, name);
		}
	}

	@Test
	public void testSetCompanyIdWithSafeCloseableFlushesSessionOnEntry()
		throws Throwable {

		long defaultCompanyId = PortalInstancePool.getDefaultCompanyId();
		String name = RandomTestUtil.randomString();

		TransactionInvokerUtil.invoke(
			_transactionConfig,
			() -> {
				_addRole(TestPropsValues.getCompanyId(), name);

				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							defaultCompanyId)) {

					_roleLocalService.dynamicQuery(
						_roleLocalService.dynamicQuery());
				}

				return null;
			});

		try {
			Assert.assertFalse(_hasRole(defaultCompanyId, name));
			Assert.assertTrue(_hasRole(TestPropsValues.getCompanyId(), name));
		}
		finally {
			_deleteRole(TestPropsValues.getCompanyId(), name);
			_deleteRole(defaultCompanyId, name);
		}
	}

	private void _addRole(long companyId, String name) {
		long roleId = _counterLocalService.increment();

		Role role = _rolePersistence.create(roleId);

		role.setCompanyId(companyId);
		role.setClassNameId(_classNameLocalService.getClassNameId(Role.class));
		role.setClassPK(roleId);
		role.setName(name);
		role.setType(RoleConstants.TYPE_REGULAR);

		_rolePersistence.update(role);
	}

	private void _deleteRole(long companyId, String name) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setRawCompanyIdWithSafeCloseable(companyId);

			Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from Role_ where name = ?")) {

			preparedStatement.setString(1, name);

			preparedStatement.execute();
		}
	}

	private boolean _hasRole(long companyId, String name) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setRawCompanyIdWithSafeCloseable(companyId);

			Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select roleId from Role_ where name = ?")) {

			preparedStatement.setString(1, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private RolePersistence _rolePersistence;

}