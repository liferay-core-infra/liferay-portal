/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.production.readiness;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lily Chi
 */
public class ProductionReadinessIgnoreRuleUtil {

	public static void addIgnoreRule(String ruleKey) throws Exception {
		List<String> ignoreRules = getIgnoreRules();

		if (ignoreRules.contains(ruleKey)) {
			return;
		}

		ignoreRules.add(ruleKey);

		_write(ignoreRules);
	}

	public static List<String> getIgnoreRules() throws Exception {
		File configFile = _getConfigFile();

		if (!FileUtil.exists(configFile)) {
			return new ArrayList<>();
		}

		String content = FileUtil.read(configFile);

		String value = content.split(StringPool.EQUAL)[1];

		value = value.substring(1, value.length() - 1);

		if (Validator.isNull(value)) {
			return new ArrayList<>();
		}

		return ListUtil.fromArray(value.split(StringPool.COMMA));
	}

	public static void removeIgnoreRule(String ruleKey) throws Exception {
		List<String> ignoreRules = getIgnoreRules();

		ignoreRules.remove(ruleKey);

		if (ignoreRules.isEmpty()) {
			FileUtil.delete(_getConfigFile());
		}
		else {
			_write(ignoreRules);
		}
	}

	private static File _getConfigFile() {
		return new File(
			PropsValues.LIFERAY_HOME, "osgi/configs/" + _PID + ".config");
	}

	private static void _write(List<String> ignoreRules) throws Exception {
		FileUtil.write(
			_getConfigFile(),
			StringBundler.concat(
				"ignoreRules=\"", StringUtil.merge(ignoreRules),
				StringPool.QUOTE));
	}

	private static final String _PID =
		"com.liferay.server.admin.web.internal.configuration." +
			"ProductionReadinessConfiguration";

}