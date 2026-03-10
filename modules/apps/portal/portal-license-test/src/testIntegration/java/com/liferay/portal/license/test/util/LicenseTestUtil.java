/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.module.framework.ModuleFrameworkUtil;
import com.liferay.portal.util.LicenseUtil;

import java.io.File;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;

/**
 * @author Tina Tian
 */
public class LicenseTestUtil {

	public static void deployFreeTierLicenseContent(
			Date startDate, Date expirationDate)
		throws Exception {

		StringBundler sb = new StringBundler(19);

		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<license><account-name>");
		sb.append(_FREE_TIER_ACCOUNT_NAME);
		sb.append("</account-name><product-name>");
		sb.append(_FREE_TIER_PRODUCT_NAME);
		sb.append("</product-name><product-version>2026.Q1</product-version>");
		sb.append("<license-type>");
		sb.append(_FREE_TIER_LICENSE_TYPE);
		sb.append("</license-type><license-version>6</license-version>");
		sb.append("<start-date>");
		sb.append(_DATE_FORMAT.format(startDate));
		sb.append("</start-date><expiration-date>");
		sb.append(_DATE_FORMAT.format(expirationDate));
		sb.append("</expiration-date>");
		sb.append("<max-cluster-nodes>3</max-cluster-nodes>");
		sb.append("<domains><domain>");
		sb.append(_FREE_TIER_DOMAIN);
		sb.append("</domain><domain>localhost</domain></domains>");
		sb.append("<key></key></license>");

		LicenseManagerUtil.registerLicense(
			JSONUtil.put("licenseXML", sb.toString()));
	}

	public static Set<String> getCurrentBundleNames() {
		Set<String> bundleNames = new HashSet<>();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			bundleNames.add(bundle.getSymbolicName());
		}

		return bundleNames;
	}

	public static Map<String, String> getPortalLicenseProperties() {
		return LicenseManagerUtil.getLicenseProperties(_PRODUCT_ID_PORTAL);
	}

	public static String hitHomePage(String host, int port) throws Exception {
		Http.Options options = new Http.Options();

		options.setCookieSpec(Http.CookieSpec.IGNORE_COOKIES);
		options.setLocation(String.format("http://%s:%d/", host, port));
		options.setMethod(Http.Method.GET);

		return HttpUtil.URLtoString(options);
	}

	public static boolean isReleaseBundle() {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		try {
			classLoader.loadClass(
				"com.liferay.portal.ee.license.util.LicenseManagerHelper");

			return true;
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(reflectiveOperationException);
			}
		}

		return false;
	}

	public static void removeAllLicenseBinaryFiles() {
		File dir = new File(LicenseUtil.LICENSE_REPOSITORY_DIR);

		if (dir.exists()) {
			FileUtil.deltree(dir);
		}

		LicenseManagerUtil.checkLicense(_PRODUCT_ID_PORTAL);
		LicenseManagerUtil.checkLicense(_PRODUCT_ID_CMP);
	}

	public static void removeFreeTierLicense() {
		File binaryFile = _buildBinaryFile(
			_PRODUCT_ID_PORTAL, _FREE_TIER_ACCOUNT_NAME,
			_FREE_TIER_PRODUCT_NAME, _FREE_TIER_LICENSE_TYPE);

		binaryFile.delete();

		LicenseManagerUtil.checkLicense(_PRODUCT_ID_PORTAL);
	}

	public static void resetLifecycleAction() throws Exception {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		Object lifecycleAction = ReflectionTestUtil.getFieldValue(
			EventsProcessorUtil.class, "_lifecycleAction");

		Class<?> clazz = lifecycleAction.getClass();

		Method installAndStartBundlesMethod = null;

		for (Method method : clazz.getDeclaredMethods()) {
			if (Arrays.equals(
					method.getParameterTypes(),
					new Class<?>[] {
						BundleContext.class, Map.class, Framework.class
					})) {

				method.setAccessible(true);

				installAndStartBundlesMethod = method;

				break;
			}
		}

		for (Field field : clazz.getDeclaredFields()) {
			if (Map.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);

				Object bundleData = field.get(lifecycleAction);

				if (bundleData != null) {
					installAndStartBundlesMethod.invoke(
						lifecycleAction, SystemBundleUtil.getBundleContext(),
						bundleData, ModuleFrameworkUtil.getFramework());
				}
			}
		}

		ReflectionTestUtil.setFieldValue(
			EventsProcessorUtil.class, "_lifecycleAction",
			ReflectionTestUtil.invoke(
				classLoader.loadClass(
					"com.liferay.portal.ee.license.util.LicenseManagerHelper"),
				"getLifecycleAction", new Class<?>[0]));
	}

	private static File _buildBinaryFile(
		String productId, String accountName, String productEntryName,
		String licenseType) {

		StringBundler sb = new StringBundler(6);

		if (productId.equals(_PRODUCT_ID_PORTAL)) {
			sb.append(StringUtil.extractChars(accountName));
			sb.append("_");
		}

		sb.append(StringUtil.extractChars(productEntryName));
		sb.append("_");
		sb.append(StringUtil.extractChars(licenseType));
		sb.append(".li");

		return new File(LicenseUtil.LICENSE_REPOSITORY_DIR, sb.toString());
	}

	private static final DateFormat _DATE_FORMAT = new SimpleDateFormat(
		"EEEE, MMMM d, yyyy hh:mm:ss a z", LocaleUtil.US);

	private static final String _FREE_TIER_ACCOUNT_NAME = "Free Account";

	private static final String _FREE_TIER_DOMAIN = "free.tier.com";

	private static final String _FREE_TIER_LICENSE_TYPE = "free";

	private static final String _FREE_TIER_PRODUCT_NAME = "DXP Production";

	private static final String _PRODUCT_ID_CMP =
		"f37efde7-11b1-6ad7-5c60-07bec3334db1";

	private static final String _PRODUCT_ID_PORTAL = "Portal";

	private static final Log _log = LogFactoryUtil.getLog(
		LicenseTestUtil.class);

}