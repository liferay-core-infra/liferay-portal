/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.parameter.contributor;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributorDefinition;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributorDefinitionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Renan Vasconcelos
 */
@Component(
	enabled = false, service = SXPParameterContributorDefinitionProvider.class
)
public class SXPParameterContributorDefinitionProviderImpl
	implements SXPParameterContributorDefinitionProvider {

	@Override
	public List<SXPParameterContributorDefinition>
		getSXPParameterContributorDefinitions(long companyId, Locale locale) {

		if (ArrayUtil.isEmpty(
				_sxpParameterContributorProvider.
					getSxpParameterContributors())) {

			return Collections.emptyList();
		}

		List<SXPParameterContributorDefinition>
			sxpParameterContributorDefinitions = new ArrayList<>();

		for (SXPParameterContributor sxpParameterContributor :
				_sxpParameterContributorProvider.
					getSxpParameterContributors()) {

			sxpParameterContributorDefinitions.addAll(
				sxpParameterContributor.getSXPParameterContributorDefinitions(
					companyId, locale));
		}

		return sxpParameterContributorDefinitions;
	}

	@Reference
	private SXPParameterContributorProvider _sxpParameterContributorProvider;

}