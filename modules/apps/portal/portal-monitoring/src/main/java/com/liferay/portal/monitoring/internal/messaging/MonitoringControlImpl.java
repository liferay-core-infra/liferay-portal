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

package com.liferay.portal.monitoring.internal.messaging;

import com.liferay.portal.kernel.monitoring.Level;
import com.liferay.portal.kernel.monitoring.MonitoringControl;

import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Joao Victor Alves
 */
@Component(enabled = false, service = MonitoringControl.class)
public class MonitoringControlImpl implements MonitoringControl {

	@Override
	public Level getLevel(String namespace) {
		Level level = LevelsRegisterUtil.getLevel(namespace);

		if (level == null) {
			return Level.OFF;
		}

		return level;
	}

	@Override
	public Set<String> getNamespaces() {
		return LevelsRegisterUtil.getKeySet();
	}

	@Override
	public void setLevel(String namespace, Level level) {
		LevelsRegisterUtil.setLevels(namespace, level);
	}

	public void setLevels(Map<String, String> levels) {
		for (Map.Entry<String, String> entry : levels.entrySet()) {
			String namespace = entry.getKey();

			String levelName = entry.getValue();

			Level level = Level.valueOf(levelName);

			LevelsRegisterUtil.setLevels(namespace, level);
		}
	}

}