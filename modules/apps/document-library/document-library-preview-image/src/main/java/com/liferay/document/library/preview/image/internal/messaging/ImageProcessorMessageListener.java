/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.image.internal.messaging;

import com.liferay.document.library.kernel.processor.ImageProcessorUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portlet.documentlibrary.messaging.BaseProcessorMessageListener;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = "destination.name=" + DestinationNames.DOCUMENT_LIBRARY_IMAGE_PROCESSOR,
	service = MessageListener.class
)
public class ImageProcessorMessageListener
	extends BaseProcessorMessageListener {

	@Override
	protected void generate(
			FileVersion sourceFileVersion, FileVersion destinationFileVersion)
		throws Exception {

		ImageProcessorUtil.generateImages(
			sourceFileVersion, destinationFileVersion);
	}

}