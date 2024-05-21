/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.util.comparator;

import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.wiki.model.WikiNode;

/**
 * @author Sergio González
 */
public class NodeNameComparator extends OrderByComparator<WikiNode> {

	public static NodeNameComparator getInstance(boolean ascending) {
		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(WikiNode node1, WikiNode node2) {
		String name1 = new String(node1.getName());
		String name2 = new String(node2.getName());

		int value = name1.compareTo(name2);

		if (_ascending) {
			return value;
		}

		return -value;
	}

	@Override
	public String getOrderBy() {
		if (_ascending) {
			return _ORDER_BY_ASC;
		}

		return _ORDER_BY_DESC;
	}

	@Override
	public String[] getOrderByFields() {
		return _ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private NodeNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final NodeNameComparator _INSTANCE_ASCENDING =
		new NodeNameComparator(true);

	private static final NodeNameComparator _INSTANCE_DESCENDING =
		new NodeNameComparator(false);

	private static final String _ORDER_BY_ASC = "WikiNode.name ASC";

	private static final String _ORDER_BY_DESC = "WikiNode.name DESC";

	private static final String[] _ORDER_BY_FIELDS = {"name"};

	private final boolean _ascending;

}