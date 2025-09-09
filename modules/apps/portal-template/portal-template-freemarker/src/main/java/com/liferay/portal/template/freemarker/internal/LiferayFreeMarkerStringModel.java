/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.InvalidPropertyException;
import freemarker.ext.beans.StringModel;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.Set;

/**
 * @author Marta Medio
 */
public class LiferayFreeMarkerStringModel extends StringModel {

	public LiferayFreeMarkerStringModel(Object object, BeansWrapper wrapper) {
		super(object, wrapper);
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		if (_restrictedMethodNames == null) {
			throw new InvalidPropertyException(
				StringBundler.concat(
					"Denied access to method or field ", key, " of ",
					object.getClass()));
		}

		String methodOrFieldName = StringUtil.toLowerCase(key);

		for (String restrictedMethodName : _restrictedMethodNames) {
			if (restrictedMethodName.endsWith(methodOrFieldName)) {
				throw new InvalidPropertyException(
					StringBundler.concat(
						"Denied access to method or field ", key, " of ",
						object.getClass()));
			}
		}

		if (_deniedAccessToInterfaceMethods) {
			Class<?>[] interfaces = object.getClass(
			).getInterfaces();

			for (Class<?> interfaceClass : interfaces) {
				String name = interfaceClass.getName();

				if (name.equals(_CT_SERVICE_NAME) ||
					name.equals(_PERSISTED_MODEL_LOCAL_SERVICE_NAME)) {

					throw new InvalidPropertyException(
						StringBundler.concat(
							"Denied access to interface method ", key, " of ",
							object.getClass()));
				}
			}
		}

		return super.get(key);
	}

	@Override
	public String getAsString() {
		if ((_restrictedMethodNames == null) || _deniedAccessToString) {
			return "Denied access to the toString method in class " +
				object.getClass();
		}

		return object.toString();
	}

	public void setDeniedAccessToInterfaceMethods(
		boolean deniedAccessToMethod) {

		_deniedAccessToInterfaceMethods = deniedAccessToMethod;
	}

	public void setDeniedAccessToString(boolean deniedAccessToString) {
		_deniedAccessToString = deniedAccessToString;
	}

	public void setRestrictedMethodNames(Set<String> restrictedMethodNames) {
		_restrictedMethodNames = restrictedMethodNames;
	}

	private static final String _CT_SERVICE_NAME =
		"com.liferay.portal.kernel.service.CTService";

	private static final String _PERSISTED_MODEL_LOCAL_SERVICE_NAME =
		"com.liferay.portal.kernel.service.PersistedModelLocalService";

	private boolean _deniedAccessToInterfaceMethods;
	private boolean _deniedAccessToString;
	private Set<String> _restrictedMethodNames;

}