/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.lang.HashUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Tina Tian
 */
public class SidecarProcessBag implements Serializable {

	public SidecarProcessBag(
		long heartbeatInterval, List<String> jvmArguments,
		List<String> log4j2Properties, String log4j2PropertiesFile,
		long shutdownTimeout, byte[] sidecarServerBytes,
		Map<String, String> systemEnvironments) {

		_heartbeatInterval = heartbeatInterval;
		_jvmArguments = jvmArguments;
		_log4j2Properties = log4j2Properties;
		_log4j2PropertiesFile = log4j2PropertiesFile;
		_shutdownTimeout = shutdownTimeout;
		_sidecarServerBytes = sidecarServerBytes;
		_systemEnvironments = systemEnvironments;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SidecarProcessBag)) {
			return false;
		}

		SidecarProcessBag sidecarProcessBag = (SidecarProcessBag)object;

		if ((_heartbeatInterval == sidecarProcessBag._heartbeatInterval) &&
			Objects.equals(_jvmArguments, sidecarProcessBag._jvmArguments) &&
			Objects.equals(
				_log4j2Properties, sidecarProcessBag._log4j2Properties) &&
			Objects.equals(
				_log4j2PropertiesFile,
				sidecarProcessBag._log4j2PropertiesFile) &&
			(_shutdownTimeout == sidecarProcessBag._shutdownTimeout) &&
			Arrays.equals(
				_sidecarServerBytes, sidecarProcessBag._sidecarServerBytes) &&
			Objects.equals(
				_systemEnvironments, sidecarProcessBag._systemEnvironments)) {

			return true;
		}

		return false;
	}

	public long getHeartbeatInterval() {
		return _heartbeatInterval;
	}

	public List<String> getJvmArguments() {
		return _jvmArguments;
	}

	public List<String> getLog4j2Properties() {
		return _log4j2Properties;
	}

	public String getLog4j2PropertiesFile() {
		return _log4j2PropertiesFile;
	}

	public long getShutdownTimeout() {
		return _shutdownTimeout;
	}

	public byte[] getSidecarServerBytes() {
		return _sidecarServerBytes;
	}

	public Map<String, String> getSystemEnvironments() {
		return _systemEnvironments;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, _heartbeatInterval);

		hashCode = HashUtil.hash(hashCode, _jvmArguments);

		hashCode = HashUtil.hash(hashCode, _log4j2Properties);

		hashCode = HashUtil.hash(hashCode, _log4j2PropertiesFile);

		hashCode = HashUtil.hash(hashCode, _shutdownTimeout);

		hashCode = HashUtil.hash(hashCode, _sidecarServerBytes);

		return HashUtil.hash(hashCode, _systemEnvironments);
	}

	private static final long serialVersionUID = 1L;

	private final long _heartbeatInterval;
	private final List<String> _jvmArguments;
	private final List<String> _log4j2Properties;
	private final String _log4j2PropertiesFile;
	private final long _shutdownTimeout;
	private final byte[] _sidecarServerBytes;
	private final Map<String, String> _systemEnvironments;

}