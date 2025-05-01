/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.video.internal.messaging;

import com.liferay.document.library.kernel.processor.VideoProcessorUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portlet.documentlibrary.messaging.BaseProcessorMessageListener;

import org.osgi.service.component.annotations.Component;

/**
 * @author Juan González
 * @author Sergio González
 */
@Component(
	property = "destination.name=" + DestinationNames.DOCUMENT_LIBRARY_VIDEO_PROCESSOR,
	service = MessageListener.class
)
public class VideoProcessorMessageListener
	extends BaseProcessorMessageListener {

	@Override
	protected void generate(
			FileVersion sourceFileVersion, FileVersion destinationFileVersion)
		throws Exception {

		VideoProcessorUtil.generateVideo(
			sourceFileVersion, destinationFileVersion);
	}

}