/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.adaptive.media.document.library.thumbnails.internal.processor;

import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.adaptive.media.image.validator.AMImageValidator;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.util.DLProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.xml.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Renan Vasconcelos
 */
@Component(
	configurationPid = "com.liferay.adaptive.media.document.library.thumbnails.internal.configuration.AMSystemImagesConfiguration",
	service = DLProcessor.class
)
public class AMDLProcessor implements DLProcessor {

	@Override
	public void afterPropertiesSet() {
	}

	@Override
	public void cleanUp(FileEntry fileEntry) {
	}

	@Override
	public void cleanUp(FileVersion fileVersion) {
	}

	@Override
	public void copy(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {
	}

	@Override
	public void exportGeneratedFiles(
		PortletDataContext portletDataContext, FileEntry fileEntry,
		Element fileEntryElement) {
	}

	@Override
	public String getType() {
		return DLProcessorConstants.IMAGE_PROCESSOR;
	}

	@Override
	public void importGeneratedFiles(
		PortletDataContext portletDataContext, FileEntry fileEntry,
		FileEntry importedFileEntry, Element fileEntryElement) {
	}

	@Override
	public boolean isSupported(FileVersion fileVersion) {
		return amImageValidator.isValid(fileVersion);
	}

	@Override
	public boolean isSupported(String mimeType) {
		return isMimeTypeSupported(mimeType);
	}

	@Override
	public void trigger(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {
	}

	protected boolean isMimeTypeSupported(String mimeType) {
		return amImageMimeTypeProvider.isMimeTypeSupported(mimeType);
	}

	@Reference
	protected AMImageMimeTypeProvider amImageMimeTypeProvider;

	@Reference
	protected AMImageValidator amImageValidator;

}