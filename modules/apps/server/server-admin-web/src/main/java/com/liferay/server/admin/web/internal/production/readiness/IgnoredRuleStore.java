/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.production.readiness;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.file.install.properties.TypedProperties;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.server.admin.web.internal.configuration.ProductionReadinessConfiguration;

import java.io.File;
import java.io.Writer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * Persists the production readiness ignore status to a configuration file in
 * <code>${liferay.home}/osgi/configs</code> instead of the database.
 *
 * @author Lily Chi
 */
@Component(
	configurationPid = "com.liferay.server.admin.web.internal.configuration.ProductionReadinessConfiguration",
	service = IgnoredRuleStore.class
)
public class IgnoredRuleStore {

	public synchronized IgnoredRule addIgnoredRule(
			String ruleKey, String ignoredBy, String reason)
		throws Exception {

		IgnoredRule ignoredRule = new IgnoredRule(
			System.currentTimeMillis(), ignoredBy, reason, ruleKey);

		Map<String, IgnoredRule> ignoredRules = LinkedHashMapBuilder.create(
			_ignoredRules
		).put(
			ruleKey, ignoredRule
		).build();

		_store(ignoredRules);

		_ignoredRules = ignoredRules;

		return ignoredRule;
	}

	public synchronized void deleteIgnoredRule(String ruleKey)
		throws Exception {

		if (!_ignoredRules.containsKey(ruleKey)) {
			return;
		}

		Map<String, IgnoredRule> ignoredRules = new LinkedHashMap<>(
			_ignoredRules);

		ignoredRules.remove(ruleKey);

		_store(ignoredRules);

		_ignoredRules = ignoredRules;
	}

	public IgnoredRule fetchIgnoredRule(String ruleKey) {
		return _ignoredRules.get(ruleKey);
	}

	public List<IgnoredRule> getIgnoredRules() {
		return new ArrayList<>(_ignoredRules.values());
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		ProductionReadinessConfiguration productionReadinessConfiguration =
			ConfigurableUtil.createConfigurable(
				ProductionReadinessConfiguration.class, properties);

		Map<String, IgnoredRule> ignoredRules = new LinkedHashMap<>();

		String[] values = productionReadinessConfiguration.ignoredRules();

		if (values != null) {
			for (String value : values) {
				IgnoredRule ignoredRule = _parse(value);

				if (ignoredRule != null) {
					ignoredRules.put(ignoredRule.getRuleKey(), ignoredRule);
				}
			}
		}

		_ignoredRules = ignoredRules;
	}

	private File _getConfigFile() {
		File configsDir = new File(PropsValues.LIFERAY_HOME, "osgi/configs");

		configsDir.mkdirs();

		return new File(configsDir, _PID + ".config");
	}

	private IgnoredRule _parse(String value) {
		if (Validator.isNull(value)) {
			return null;
		}

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(value);

			return new IgnoredRule(
				jsonObject.getLong("ignoredAt"),
				jsonObject.getString("ignoredBy"),
				jsonObject.getString("reason"),
				jsonObject.getString("ruleKey"));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to parse ignored rule: " + value, exception);
			}

			return null;
		}
	}

	private void _store(Map<String, IgnoredRule> ignoredRules)
		throws Exception {

		String[] values = new String[ignoredRules.size()];

		int i = 0;

		for (IgnoredRule ignoredRule : ignoredRules.values()) {
			values[i++] = _jsonFactory.createJSONObject(
			).put(
				"ignoredAt", ignoredRule.getIgnoredAt()
			).put(
				"ignoredBy", ignoredRule.getIgnoredBy()
			).put(
				"reason", ignoredRule.getReason()
			).put(
				"ruleKey", ignoredRule.getRuleKey()
			).toString();
		}

		TypedProperties typedProperties = new TypedProperties();

		typedProperties.put("ignoredRules", values);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		try (Writer writer = unsyncStringWriter) {
			typedProperties.save(writer);
		}

		FileUtil.write(_getConfigFile(), unsyncStringWriter.toString());
	}

	private static final String _PID =
		"com.liferay.server.admin.web.internal.configuration." +
			"ProductionReadinessConfiguration";

	private static final Log _log = LogFactoryUtil.getLog(
		IgnoredRuleStore.class);

	private volatile Map<String, IgnoredRule> _ignoredRules =
		new LinkedHashMap<>();

	@Reference
	private JSONFactory _jsonFactory;

}