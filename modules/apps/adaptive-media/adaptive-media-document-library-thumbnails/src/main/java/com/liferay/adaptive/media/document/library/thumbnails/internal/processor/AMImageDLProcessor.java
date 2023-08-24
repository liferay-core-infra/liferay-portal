/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.thumbnails.internal.processor;

import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.util.DLProcessor;
import com.liferay.portal.kernel.repository.model.FileVersion;

import org.osgi.service.component.annotations.Component;

/**
 * @author Renan Vasconcelos
 */
@Component(
	property = "type=" + DLProcessorConstants.IMAGE_PROCESSOR,
	service = DLProcessor.class
)
public class AMImageDLProcessor
	extends AMImageEntryProcessor implements DLProcessor {

	@Override
	public void afterPropertiesSet() {
	}

	@Override
	public void copy(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {
	}

	@Override
	public String getType() {
		return DLProcessorConstants.IMAGE_PROCESSOR;
	}

	@Override
	public boolean isSupported(FileVersion fileVersion) {
		return amImageValidator.isValid(fileVersion);
	}

}