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

package com.liferay.util.xml;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class Dom4jDocUtil {

	public static Element add(Element element, QName qName) {
		return element.addElement(qName);
	}

	public static Element add(Element element, QName qName, boolean text) {
		return add(element, qName, String.valueOf(text));
	}

	public static Element add(Element element, QName qName, double text) {
		return add(element, qName, String.valueOf(text));
	}

	public static Element add(Element element, QName qName, float text) {
		return add(element, qName, String.valueOf(text));
	}

	public static Element add(Element element, QName qName, int text) {
		return add(element, qName, String.valueOf(text));
	}

	public static Element add(Element element, QName qName, long text) {
		return add(element, qName, String.valueOf(text));
	}

	public static Element add(Element element, QName qName, Object text) {
		return add(element, qName, String.valueOf(text));
	}

	public static Element add(Element element, QName qName, short text) {
		return add(element, qName, String.valueOf(text));
	}

	public static Element add(Element element, QName qName, String text) {
		Element childElement = element.addElement(qName);

		childElement.addText(GetterUtil.getString(text));

		return childElement;
	}

	public static Element add(Element element, String name, boolean text) {
		return add(element, name, String.valueOf(text));
	}

	public static Element add(Element element, String name, double text) {
		return add(element, name, String.valueOf(text));
	}

	public static Element add(Element element, String name, float text) {
		return add(element, name, String.valueOf(text));
	}

	public static Element add(Element element, String name, int text) {
		return add(element, name, String.valueOf(text));
	}

	public static Element add(Element element, String name, long text) {
		return add(element, name, String.valueOf(text));
	}

	public static Element add(
		Element element, String name, Namespace namespace) {

		QName qName = DocumentHelper.createQName(name, namespace);

		return element.addElement(qName);
	}

	public static Element add(
		Element element, String name, Namespace namespace, boolean text) {

		return add(element, name, namespace, String.valueOf(text));
	}

	public static Element add(
		Element element, String name, Namespace namespace, double text) {

		return add(element, name, namespace, String.valueOf(text));
	}

	public static Element add(
		Element element, String name, Namespace namespace, float text) {

		return add(element, name, namespace, String.valueOf(text));
	}

	public static Element add(
		Element element, String name, Namespace namespace, int text) {

		return add(element, name, namespace, String.valueOf(text));
	}

	public static Element add(
		Element element, String name, Namespace namespace, long text) {

		return add(element, name, namespace, String.valueOf(text));
	}

	public static Element add(
		Element element, String name, Namespace namespace, Object text) {

		return add(element, name, namespace, String.valueOf(text));
	}

	public static Element add(
		Element element, String name, Namespace namespace, short text) {

		return add(element, name, namespace, String.valueOf(text));
	}

	public static Element add(
		Element element, String name, Namespace namespace, String text) {

		QName qName = DocumentHelper.createQName(name, namespace);

		return add(element, qName, text);
	}

	public static Element add(Element element, String name, Object text) {
		return add(element, name, String.valueOf(text));
	}

	public static Element add(Element element, String name, short text) {
		return add(element, name, String.valueOf(text));
	}

	public static Element add(Element element, String name, String text) {
		Element childElement = element.addElement(name);

		childElement.addText(GetterUtil.getString(text));

		return childElement;
	}

	public static String toString(Node node) throws IOException {
		return toString(node, StringPool.TAB);
	}

	public static String toString(Node node, String indent) throws IOException {
		return toString(node, indent, false);
	}

	public static String toString(
			Node node, String indent, boolean expandEmptyElements)
		throws IOException {

		return toString(node, indent, expandEmptyElements, true);
	}

	public static String toString(
			Node node, String indent, boolean expandEmptyElements,
			boolean trimText)
		throws IOException {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		OutputFormat outputFormat = OutputFormat.createPrettyPrint();

		outputFormat.setExpandEmptyElements(expandEmptyElements);
		outputFormat.setIndent(indent);
		outputFormat.setLineSeparator(StringPool.NEW_LINE);
		outputFormat.setTrimText(trimText);

		XMLWriter xmlWriter = new XMLWriter(
			unsyncByteArrayOutputStream, outputFormat);

		xmlWriter.write(node);

		String content = unsyncByteArrayOutputStream.toString(StringPool.UTF8);

		// LEP-4257

		//content = StringUtil.replace(content, "\n\n\n", "\n\n");

		if (content.endsWith("\n\n")) {
			content = content.substring(0, content.length() - 2);
		}

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() - 1);
		}

		while (content.contains(" \n")) {
			content = StringUtil.replace(content, " \n", "\n");
		}

		if (content.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
			content = StringUtil.replaceFirst(
				content, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
				"<?xml version=\"1.0\"?>");
		}

		return content;
	}

}