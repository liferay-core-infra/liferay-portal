/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class LDAPConfigurationListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testStaleConfigurationUpdatedEvent() throws Exception {
		String factoryPid = LDAPServerConfiguration.class.getName();

		String pid = StringBundler.concat(
			factoryPid, "~", RandomTestUtil.randomString());

		Bundle bundle = FrameworkUtil.getBundle(
			LDAPConfigurationListenerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_configurationListener.configurationEvent(
			new ConfigurationEvent(
				bundleContext.getServiceReference(ConfigurationAdmin.class),
				ConfigurationEvent.CM_UPDATED, factoryPid, pid));

		DataSource dataSource = InfrastructureUtil.getDataSource();

		try (Connection connection = dataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select configurationId from Configuration_ where " +
					"configurationId = ?")) {

			preparedStatement.setString(1, pid);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertFalse(resultSet.next());
			}
		}
	}

	@Inject(
		filter = "component.name=com.liferay.portal.security.ldap.internal.configuration.LDAPConfigurationListener"
	)
	private ConfigurationListener _configurationListener;

}