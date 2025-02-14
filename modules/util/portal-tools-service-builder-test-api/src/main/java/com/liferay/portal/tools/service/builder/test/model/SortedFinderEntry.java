/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the SortedFinderEntry service. Represents a row in the &quot;SortedFinderEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see SortedFinderEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portal.tools.service.builder.test.model.impl.SortedFinderEntryImpl"
)
@ProviderType
public interface SortedFinderEntry
	extends PersistedModel, SortedFinderEntryModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.tools.service.builder.test.model.impl.SortedFinderEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<SortedFinderEntry, Long>
		SORTED_FINDER_ENTRY_ID_ACCESSOR =
			new Accessor<SortedFinderEntry, Long>() {

				@Override
				public Long get(SortedFinderEntry sortedFinderEntry) {
					return sortedFinderEntry.getSortedFinderEntryId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<SortedFinderEntry> getTypeClass() {
					return SortedFinderEntry.class;
				}

			};

}