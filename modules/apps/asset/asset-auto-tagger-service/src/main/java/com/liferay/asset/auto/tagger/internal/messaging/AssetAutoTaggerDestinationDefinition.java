/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.internal.messaging;

import com.liferay.asset.auto.tagger.internal.constants.AssetAutoTaggerDestinationNames;
import com.liferay.portal.kernel.messaging.DestinationDefinition;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(
	property = "destination.name=" + AssetAutoTaggerDestinationNames.ASSET_AUTO_TAGGER,
	service = DestinationDefinition.class
)
public class AssetAutoTaggerDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return AssetAutoTaggerDestinationNames.ASSET_AUTO_TAGGER;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_PARALLEL;
	}

}