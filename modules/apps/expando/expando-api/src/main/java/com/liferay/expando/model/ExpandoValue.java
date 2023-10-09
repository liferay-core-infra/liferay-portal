/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the ExpandoValue service. Represents a row in the &quot;ExpandoValue&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoValueModel
 * @generated
 */
@ImplementationClassName("com.liferay.expando.model.impl.ExpandoValueImpl")
@ProviderType
public interface ExpandoValue extends ExpandoValueModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.expando.model.impl.ExpandoValueImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ExpandoValue, Long> VALUE_ID_ACCESSOR =
		new Accessor<ExpandoValue, Long>() {

			@Override
			public Long get(ExpandoValue expandoValue) {
				return expandoValue.getValueId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<ExpandoValue> getTypeClass() {
				return ExpandoValue.class;
			}

		};

}