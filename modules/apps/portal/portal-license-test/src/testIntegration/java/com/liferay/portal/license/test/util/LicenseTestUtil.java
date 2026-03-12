/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test.util;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.module.framework.ModuleFrameworkUtil;
import com.liferay.portal.util.LicenseUtil;

import jakarta.el.MethodNotFoundException;

import java.io.File;
import java.io.InputStream;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.net.URL;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jodd.io.FileUtil;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

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

	public static ResettableClassFileTransformer disableKeyValidator() {
		return _transformMethod(_validateMethod, true);
	}

	public static long getCheckInterval() throws IllegalAccessException {
		Object lifecycleAction = ReflectionTestUtil.getFieldValue(
			EventsProcessorUtil.class, "_lifecycleAction");

		return _checkIntervalField.getLong(lifecycleAction);
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
		if (_licenseManagerHelperClass != null) {
			return true;
		}

		return false;
	}

	public static void removeAllLicenseBinaryFiles() throws Exception {
		File dir = new File(LicenseUtil.LICENSE_REPOSITORY_DIR);

		if (dir.exists()) {
			FileUtil.deleteDir(dir);
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

	public static void resetClassFileTransformer(
		ResettableClassFileTransformer resettableClassFileTransformer) {

		resettableClassFileTransformer.reset(
			_instrumentation,
			AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
	}

	public static void resetLifecycleAction() throws Exception {
		Object lifecycleAction = ReflectionTestUtil.getFieldValue(
			EventsProcessorUtil.class, "_lifecycleAction");

		for (Field field : _bundleDataFields) {
			Object bundleData = field.get(lifecycleAction);

			if (bundleData != null) {
				_installAndStartBundlesMethod.invoke(
					lifecycleAction, SystemBundleUtil.getBundleContext(),
					bundleData, ModuleFrameworkUtil.getFramework());
			}
		}

		ReflectionTestUtil.setFieldValue(
			EventsProcessorUtil.class, "_lifecycleAction",
			ReflectionTestUtil.invoke(
				_licenseManagerHelperClass, "getLifecycleAction",
				new Class<?>[0]));
	}

	public static ResettableClassFileTransformer setVersionDisplayName(
		String versionDisplayName) {

		return _transformMethod(
			_getVersionDisplayNameMethod, versionDisplayName);
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

	private static Method _findMethod(
		Class<?> clazz, int modifier, Class<?> returnType,
		Class<?>[] parameterTypes) {

		for (Method method : clazz.getDeclaredMethods()) {
			if ((method.getModifiers() == modifier) &&
				(method.getReturnType() == returnType)) {

				if ((parameterTypes != null) &&
					!Arrays.equals(
						parameterTypes, method.getParameterTypes())) {

					continue;
				}

				method.setAccessible(true);

				return method;
			}
		}

		throw new MethodNotFoundException(
			"Unable to find method for " + clazz.getName());
	}

	private static Class<?> _getKeyValidatorClass(
			ClassLoader classLoader, Class<?> licenseManagerHelperClass)
		throws Exception {

		ProtectionDomain protectionDomain =
			licenseManagerHelperClass.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		try (JarFile jarFile = new JarFile(url.getFile())) {
			Enumeration<JarEntry> enumeration = jarFile.entries();

			while (enumeration.hasMoreElements()) {
				JarEntry jarEntry = enumeration.nextElement();

				String jarEntryName = jarEntry.getName();

				if (jarEntry.isDirectory() ||
					!jarEntryName.startsWith(_OBFUSCATE_PACKAGE_PATH) ||
					!jarEntryName.endsWith(".class")) {

					continue;
				}

				try (InputStream inputStream = jarFile.getInputStream(
						jarEntry)) {

					String content = new String(inputStream.readAllBytes());

					if (content.contains(_KEY_VALIDATOR_CLASS_KEY_WORDS)) {
						String fileName = StringUtil.replace(
							jarEntry.toString(), CharPool.SLASH,
							CharPool.PERIOD);

						return classLoader.loadClass(
							fileName.substring(0, fileName.length() - 6));
					}
				}
			}
		}

		throw new ClassNotFoundException("Unable to find key validator class");
	}

	private static ResettableClassFileTransformer _transformMethod(
		Method method, Object returnValue) {

		return new AgentBuilder.Default(
		).disableClassFormatChanges(
		).with(
			AgentBuilder.RedefinitionStrategy.RETRANSFORMATION
		).type(
			ElementMatchers.is(method.getDeclaringClass())
		).transform(
			(builder, typeDescription, classLoader, module, protectionDomain) ->
				builder.method(
					ElementMatchers.is(method)
				).intercept(
					FixedValue.value(returnValue)
				)
		).installOn(
			_instrumentation
		);
	}

	private static final DateFormat _DATE_FORMAT = new SimpleDateFormat(
		"EEEE, MMMM d, yyyy hh:mm:ss a z", LocaleUtil.US);

	private static final String _FREE_TIER_ACCOUNT_NAME = "Free Account";

	private static final String _FREE_TIER_DOMAIN = "free.tier.com";

	private static final String _FREE_TIER_LICENSE_TYPE = "free";

	private static final String _FREE_TIER_PRODUCT_NAME = "DXP Production";

	private static final String _KEY_VALIDATOR_CLASS_KEY_WORDS = "BannedKeys";

	private static final String _OBFUSCATE_PACKAGE_PATH =
		"com/liferay/portal/ee/license/";

	private static final String _PRODUCT_ID_CMP =
		"f37efde7-11b1-6ad7-5c60-07bec3334db1";

	private static final String _PRODUCT_ID_PORTAL = "Portal";

	private static final Log _log = LogFactoryUtil.getLog(
		LicenseTestUtil.class);

	private static Set<Field> _bundleDataFields;
	private static Field _checkIntervalField;
	private static Method _getVersionDisplayNameMethod;
	private static Method _installAndStartBundlesMethod;
	private static Instrumentation _instrumentation;
	private static Class<?> _licenseManagerHelperClass;
	private static Method _validateMethod;

	static {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		try {
			_licenseManagerHelperClass = classLoader.loadClass(
				"com.liferay.portal.ee.license.util.LicenseManagerHelper");
		}
		catch (ClassNotFoundException classNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(classNotFoundException);
			}
		}

		if (_licenseManagerHelperClass != null) {
			try {
				_getVersionDisplayNameMethod = ReflectionUtil.getDeclaredMethod(
					ReleaseInfo.class, "getVersionDisplayName");

				_validateMethod = _findMethod(
					_getKeyValidatorClass(
						classLoader, _licenseManagerHelperClass),
					Modifier.PUBLIC | Modifier.STATIC, boolean.class, null);

				Object lifecycleAction = ReflectionTestUtil.getFieldValue(
					EventsProcessorUtil.class, "_lifecycleAction");

				Class<?> lifecycleActionClass = lifecycleAction.getClass();

				_installAndStartBundlesMethod = _findMethod(
					lifecycleActionClass, Modifier.PRIVATE, Void.TYPE,
					new Class<?>[] {
						BundleContext.class, Map.class, Framework.class
					});

				_bundleDataFields = new HashSet<>();

				for (Field field : lifecycleActionClass.getDeclaredFields()) {
					if (Map.class.isAssignableFrom(field.getType())) {
						field.setAccessible(true);

						_bundleDataFields.add(field);
					}

					if (long.class.isAssignableFrom(field.getType()) &&
						(field.getModifiers() ==
							(Modifier.FINAL | Modifier.PRIVATE |
							 Modifier.STATIC))) {

						field.setAccessible(true);

						_checkIntervalField = field;
					}
				}

				ByteBuddyAgent.install();

				_instrumentation = ByteBuddyAgent.getInstrumentation();
			}
			catch (Exception exception) {
				throw new ExceptionInInitializerError(exception);
			}
		}
	}

}