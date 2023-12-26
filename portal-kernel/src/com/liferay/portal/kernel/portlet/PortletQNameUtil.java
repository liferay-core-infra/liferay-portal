/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Namespace;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletQNameUtil {

	public static final String PRIVATE_RENDER_PARAMETER_NAMESPACE = "priv_r_p_";

	public static final String PUBLIC_RENDER_PARAMETER_NAMESPACE = "p_r_p_";

	public static final String REMOVE_PUBLIC_RENDER_PARAMETER_NAMESPACE =
		"r_p_r_p_";

	public static String getKey(QName qName) {
		return getKey(qName.getNamespaceURI(), qName.getLocalPart());
	}

	public static String getKey(String uri, String localPart) {
		return StringBundler.concat(uri, _KEY_SEPARATOR, localPart);
	}

	public static String getPublicRenderParameterName(QName qName) {
		String publicRenderParameterName = _qNameStrings.get(qName);

		if (publicRenderParameterName == null) {
			publicRenderParameterName = _toString(
				PortletQNameUtil.PUBLIC_RENDER_PARAMETER_NAMESPACE, qName);

			_qNames.put(publicRenderParameterName, qName);
			_qNameStrings.put(qName, publicRenderParameterName);
		}

		return publicRenderParameterName;
	}

	public static QName getQName(
		Element qNameEl, Element nameEl, String defaultNamespace) {

		if ((qNameEl == null) && (nameEl == null)) {
			_log.error("both qname and name elements are null");

			return null;
		}

		if (qNameEl == null) {
			return SAXReaderUtil.createQName(
				nameEl.getTextTrim(),
				SAXReaderUtil.createNamespace(defaultNamespace));
		}

		String localPart = qNameEl.getTextTrim();

		int pos = localPart.indexOf(CharPool.COLON);

		if (pos == -1) {
			if (_log.isDebugEnabled()) {
				_log.debug("qname " + localPart + " does not have a prefix");
			}

			return SAXReaderUtil.createQName(localPart);
		}

		String prefix = localPart.substring(0, pos);

		Namespace namespace = qNameEl.getNamespaceForPrefix(prefix);

		if (namespace == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"qname " + localPart + " does not have a valid namespace");
			}

			return null;
		}

		localPart = localPart.substring(prefix.length() + 1);

		return SAXReaderUtil.createQName(localPart, namespace);
	}

	public static QName getQName(String publicRenderParameterName) {
		if (!publicRenderParameterName.startsWith(
				PortletQNameUtil.PUBLIC_RENDER_PARAMETER_NAMESPACE) &&
			!publicRenderParameterName.startsWith(
				PortletQNameUtil.REMOVE_PUBLIC_RENDER_PARAMETER_NAMESPACE)) {

			return null;
		}

		return _qNames.get(publicRenderParameterName);
	}

	public static String getRemovePublicRenderParameterName(QName qName) {
		String removePublicRenderParameterName = _qNameStrings.get(qName);

		if (removePublicRenderParameterName == null) {
			removePublicRenderParameterName = _toString(
				PortletQNameUtil.REMOVE_PUBLIC_RENDER_PARAMETER_NAMESPACE,
				qName);

			_qNames.put(removePublicRenderParameterName, qName);
			_qNameStrings.put(qName, removePublicRenderParameterName);
		}

		return removePublicRenderParameterName;
	}

	private static String _toString(String prefix, QName qName) {
		StringBundler sb = new StringBundler(6);

		sb.append(prefix);

		String namespacePrefix = qName.getNamespacePrefix();

		if (!Validator.isBlank(namespacePrefix)) {
			sb.append(namespacePrefix);
			sb.append(StringPool.UNDERLINE);
		}

		if (!Validator.isBlank(qName.getNamespaceURI())) {
			sb.append(qName.getNamespaceURI());
			sb.append(StringPool.UNDERLINE);
		}

		sb.append(qName.getLocalPart());

		return sb.toString();
	}

	private static final String _KEY_SEPARATOR = "_KEY_";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletQNameUtil.class);

	private static final Map<String, QName> _qNames = new ConcurrentHashMap<>();
	private static final Map<QName, String> _qNameStrings =
		new ConcurrentHashMap<>();

}