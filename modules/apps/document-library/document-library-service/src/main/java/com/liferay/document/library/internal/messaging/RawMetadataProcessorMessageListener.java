/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.messaging;

import com.liferay.document.library.kernel.processor.RawMetadataProcessorUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.repository.model.FileVersion;

import org.osgi.service.component.annotations.Component;

/**
 * @author Miguel Pastor
 */
@Component(
	property = "destination.name=" + DestinationNames.DOCUMENT_LIBRARY_RAW_METADATA_PROCESSOR,
	service = MessageListener.class
)
public class RawMetadataProcessorMessageListener extends BaseMessageListener {

	@Override
	protected void doReceive(Message message) throws Exception {
		if (StartupHelperUtil.isUpgrading()) {
			return;
		}

		FileVersion fileVersion = (FileVersion)message.getPayload();

		try {
			RawMetadataProcessorUtil.saveMetadata(fileVersion);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to save metadata for file version " +
						fileVersion.getFileVersionId(),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RawMetadataProcessorMessageListener.class);

}