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

package com.liferay.portal.tools;

import com.liferay.petra.string.StringBundler;

import java.util.List;

import oshi.SystemInfo;

import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import oshi.software.os.OperatingSystem;

import oshi.util.FormatUtil;

/**
 * @author Renato Rego
 */
public class MachineInfoUtil {

	public static void main(String[] args) {
		SystemInfo systemInfo = new SystemInfo();

		HardwareAbstractionLayer hardware = systemInfo.getHardware();

		List<NetworkIF> networkIFs = hardware.getNetworkIFs();

		for (NetworkIF networkIF : networkIFs) {
			StringBundler sb = new StringBundler(4);

			sb.append("Network Interface: ");
			sb.append(networkIF.getName());
			sb.append(", IP: ");
			sb.append(networkIF.getIPv4addr()[0]);

			System.out.println(sb.toString());
		}

		OperatingSystem operatingSystem = systemInfo.getOperatingSystem();

		System.out.println("OS Family: " + operatingSystem.getFamily());
		System.out.println(
			"OS Manufacturer: " + operatingSystem.getManufacturer());

		CentralProcessor centralProcessor = hardware.getProcessor();

		System.out.println(
			"Logical Processors Count: " +
				centralProcessor.getLogicalProcessorCount());
		System.out.println(
			"Physical Processors Count: " +
				centralProcessor.getPhysicalProcessorCount());

		GlobalMemory globalMemory = hardware.getMemory();

		System.out.println(
			"RAM: " + FormatUtil.formatBytes(globalMemory.getTotal()));
	}

}