/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.messaging;

import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.segments.asah.connector.internal.constants.SegmentsAsahDestinationNames;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(
	property = "destination.name=" + SegmentsAsahDestinationNames.INTEREST_TERMS,
	service = DestinationDefinition.class
)
public class InterestTermsDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return SegmentsAsahDestinationNames.INTEREST_TERMS;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_PARALLEL;
	}

}