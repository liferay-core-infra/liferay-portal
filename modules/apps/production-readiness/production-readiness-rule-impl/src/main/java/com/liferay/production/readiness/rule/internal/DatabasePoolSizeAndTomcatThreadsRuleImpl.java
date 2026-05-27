/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.lang.management.ManagementFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class DatabasePoolSizeAndTomcatThreadsRuleImpl
	implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		int jdbcMaxPoolSize = GetterUtil.getInteger(
			PropsUtil.get("jdbc.default.maximumPoolSize"));

		int tomcatMaxThreads = _getMaxThreads();

		if ((jdbcMaxPoolSize <= 0) || (tomcatMaxThreads <= 0)) {
			return Collections.emptyList();
		}

		double ratio = (double)jdbcMaxPoolSize / tomcatMaxThreads;

		boolean pass = false;

		if ((ratio >= 0.3) && (ratio <= 0.4)) {
			pass = true;
		}

		String currentValue = StringBundler.concat(
			"DB Pool Size=", jdbcMaxPoolSize, ", Tomcat Threads=",
			tomcatMaxThreads, " (Ratio=", String.format("%.2f", ratio), ")");

		if (pass) {
			return Collections.singletonList(
				new Result(
					Result.Status.PASS, Result.Severity.LOW, getCategory(),
					String.valueOf(ratio), null,
					"production-readiness-rule-pool-vs-thread-ratio-pass",
					new Object[0], null, getKey()));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.LOW, getCategory(),
				currentValue, null,
				"production-readiness-rule-pool-vs-thread-ratio-fail",
				new Object[0], null, getKey()));
	}

	@Override
	public String getCategory() {
		return "database-configuration";
	}

	@Override
	public String getKey() {
		return "pool-vs-thread-ratio";
	}

	private int _getMaxThreads() {
		try {
			MBeanServer mBeanServer =
				ManagementFactory.getPlatformMBeanServer();

			ObjectName objectName = new ObjectName(
				"Catalina:type=ThreadPool,name=*");

			Set<ObjectName> objectNames = mBeanServer.queryNames(
				objectName, null);

			int maxThreads = 0;

			for (ObjectName name : objectNames) {
				int threads = (int)mBeanServer.getAttribute(name, "maxThreads");

				if (threads > maxThreads) {
					maxThreads = threads;
				}
			}

			return maxThreads;
		}
		catch (AttributeNotFoundException | InstanceNotFoundException |
			   MalformedObjectNameException | MBeanException |
			   ReflectionException exception) {

			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return 200;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DatabasePoolSizeAndTomcatThreadsRuleImpl.class);

}