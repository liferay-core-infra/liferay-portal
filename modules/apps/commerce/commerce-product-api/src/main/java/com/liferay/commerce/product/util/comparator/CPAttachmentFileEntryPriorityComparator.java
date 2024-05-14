/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.util.comparator;

import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Marco Leo
 */
public class CPAttachmentFileEntryPriorityComparator
	extends OrderByComparator<CPAttachmentFileEntry> {

	public static CPAttachmentFileEntryPriorityComparator get(
		boolean ascending) {

		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	public CPAttachmentFileEntryPriorityComparator() {
		this(false);
	}

	@Override
	public int compare(
		CPAttachmentFileEntry cpAttachmentFileEntry1,
		CPAttachmentFileEntry cpAttachmentFileEntry2) {

		int value = Double.compare(
			cpAttachmentFileEntry1.getPriority(),
			cpAttachmentFileEntry2.getPriority());

		if (_ascending) {
			return value;
		}

		return Math.negateExact(value);
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

	private CPAttachmentFileEntryPriorityComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final CPAttachmentFileEntryPriorityComparator _ASCENDING =
		new CPAttachmentFileEntryPriorityComparator(true);

	private static final CPAttachmentFileEntryPriorityComparator _DESCENDING =
		new CPAttachmentFileEntryPriorityComparator(false);

	private static final String _ORDER_BY_ASC =
		"CPAttachmentFileEntry.priority ASC";

	private static final String _ORDER_BY_DESC =
		"CPAttachmentFileEntry.priority DESC";

	private static final String[] _ORDER_BY_FIELDS = {"priority"};

	private final boolean _ascending;

}