/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.Properties;

import javax.portlet.PortletPreferences;

/**
 * @author Brian Wing Shun Chan
 */
public class PrefsPropsUtil {

	public static boolean getBoolean(long companyId, String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getBoolean(companyId, name);
	}

	public static boolean getBoolean(
		long companyId, String name, boolean defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getBoolean(companyId, name, defaultValue);
	}

	public static boolean getBoolean(
		PortletPreferences portletPreferences, String name) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getBoolean(portletPreferences, name);
	}

	public static boolean getBoolean(
		PortletPreferences portletPreferences, String name,
		boolean defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getBoolean(portletPreferences, name, defaultValue);
	}

	public static boolean getBoolean(String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getBoolean(name);
	}

	public static boolean getBoolean(String name, boolean defaultValue) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getBoolean(name, defaultValue);
	}

	public static String getContent(long companyId, String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getContent(companyId, name);
	}

	public static String getContent(
		PortletPreferences portletPreferences, String name) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getContent(portletPreferences, name);
	}

	public static String getContent(String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getContent(name);
	}

	public static double getDouble(long companyId, String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getDouble(companyId, name);
	}

	public static double getDouble(
		long companyId, String name, double defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getDouble(companyId, name, defaultValue);
	}

	public static double getDouble(
		PortletPreferences portletPreferences, String name) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getDouble(portletPreferences, name);
	}

	public static double getDouble(
		PortletPreferences portletPreferences, String name,
		double defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getDouble(portletPreferences, name, defaultValue);
	}

	public static double getDouble(String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getDouble(name);
	}

	public static double getDouble(String name, double defaultValue) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getDouble(name, defaultValue);
	}

	public static int getInteger(long companyId, String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getInteger(companyId, name);
	}

	public static int getInteger(
		long companyId, String name, int defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getInteger(companyId, name, defaultValue);
	}

	public static int getInteger(
		PortletPreferences portletPreferences, String name) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getInteger(portletPreferences, name);
	}

	public static int getInteger(
		PortletPreferences portletPreferences, String name, int defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getInteger(portletPreferences, name, defaultValue);
	}

	public static int getInteger(String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getInteger(name);
	}

	public static int getInteger(String name, int defaultValue) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getInteger(name, defaultValue);
	}

	public static long getLong(long companyId, String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getLong(companyId, name);
	}

	public static long getLong(long companyId, String name, long defaultValue) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getLong(companyId, name, defaultValue);
	}

	public static long getLong(
		PortletPreferences portletPreferences, String name) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getLong(portletPreferences, name);
	}

	public static long getLong(
		PortletPreferences portletPreferences, String name, long defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getLong(portletPreferences, name, defaultValue);
	}

	public static long getLong(String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getLong(name);
	}

	public static long getLong(String name, long defaultValue) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getLong(name, defaultValue);
	}

	public static PortletPreferences getPreferences() {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getPreferences();
	}

	public static PortletPreferences getPreferences(long companyId) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getPreferences(companyId);
	}

	public static PrefsProps getPrefsProps() {
		return _prefsPropsSnapshot.get();
	}

	public static Properties getProperties(
		PortletPreferences portletPreferences, String prefix,
		boolean removePrefix) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getProperties(
			portletPreferences, prefix, removePrefix);
	}

	public static Properties getProperties(
		String prefix, boolean removePrefix) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getProperties(prefix, removePrefix);
	}

	public static short getShort(long companyId, String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getShort(companyId, name);
	}

	public static short getShort(
		long companyId, String name, short defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getShort(companyId, name, defaultValue);
	}

	public static short getShort(
		PortletPreferences portletPreferences, String name) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getShort(portletPreferences, name);
	}

	public static short getShort(
		PortletPreferences portletPreferences, String name,
		short defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getShort(portletPreferences, name, defaultValue);
	}

	public static short getShort(String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getShort(name);
	}

	public static short getShort(String name, short defaultValue) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getShort(name, defaultValue);
	}

	public static String getString(long companyId, String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(companyId, name);
	}

	public static String getString(
		long companyId, String name, String defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(companyId, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(portletPreferences, name);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name,
		boolean defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name,
		double defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name, int defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name, long defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name,
		short defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name,
		String defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(String name) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(name);
	}

	public static String getString(String name, String defaultValue) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getString(name, defaultValue);
	}

	public static String[] getStringArray(
		long companyId, String name, String delimiter) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getStringArray(companyId, name, delimiter);
	}

	public static String[] getStringArray(
		long companyId, String name, String delimiter, String[] defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getStringArray(
			companyId, name, delimiter, defaultValue);
	}

	public static String[] getStringArray(
		PortletPreferences portletPreferences, String name, String delimiter) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getStringArray(portletPreferences, name, delimiter);
	}

	public static String[] getStringArray(
		PortletPreferences portletPreferences, String name, String delimiter,
		String[] defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getStringArray(
			portletPreferences, name, delimiter, defaultValue);
	}

	public static String[] getStringArray(String name, String delimiter) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getStringArray(name, delimiter);
	}

	public static String[] getStringArray(
		String name, String delimiter, String[] defaultValue) {

		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getStringArray(name, delimiter, defaultValue);
	}

	public static String getStringFromNames(long companyId, String... names) {
		PrefsProps prefsProps = _prefsPropsSnapshot.get();

		return prefsProps.getStringFromNames(companyId, names);
	}

	private static final Snapshot<PrefsProps> _prefsPropsSnapshot =
		new Snapshot<>(PrefsPropsUtil.class, PrefsProps.class);

}