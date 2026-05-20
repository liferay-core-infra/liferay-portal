/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the IgnoredRule service. Represents a row in the &quot;IgnoredRule&quot; database table, with each column mapped to a property of this class.
 *
 * @author Lily Chi
 * @see IgnoredRuleModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.production.readiness.ignore.model.impl.IgnoredRuleImpl"
)
@ProviderType
public interface IgnoredRule extends IgnoredRuleModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.production.readiness.ignore.model.impl.IgnoredRuleImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<IgnoredRule, Long> IGNORED_RULE_ID_ACCESSOR =
		new Accessor<IgnoredRule, Long>() {

			@Override
			public Long get(IgnoredRule ignoredRule) {
				return ignoredRule.getIgnoredRuleId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<IgnoredRule> getTypeClass() {
				return IgnoredRule.class;
			}

		};

}
// LIFERAY-SERVICE-BUILDER-HASH:-127260566