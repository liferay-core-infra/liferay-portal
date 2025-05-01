/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.pdf.internal.messaging;

import com.liferay.document.library.kernel.processor.PDFProcessorUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileVersion;
import com.liferay.portlet.documentlibrary.messaging.BaseProcessorMessageListener;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alexander Chow
 */
@Component(
	property = "destination.name=" + DestinationNames.DOCUMENT_LIBRARY_PDF_PROCESSOR,
	service = MessageListener.class
)
public class PDFProcessorMessageListener extends BaseProcessorMessageListener {

	@Override
	protected void generate(
			FileVersion sourceFileVersion, FileVersion destinationFileVersion)
		throws Exception {

		if (CTCollectionThreadLocal.isProductionMode() ||
			!(destinationFileVersion instanceof LiferayFileVersion)) {

			PDFProcessorUtil.generateImages(
				sourceFileVersion, destinationFileVersion);

			return;
		}

		LiferayFileVersion liferayFileVersion =
			(LiferayFileVersion)destinationFileVersion;

		long ctCollectionId = liferayFileVersion.getCTCollectionId();

		if (ctCollectionId == CTCollectionThreadLocal.getCTCollectionId()) {
			PDFProcessorUtil.generateImages(
				sourceFileVersion, destinationFileVersion);
		}
		else {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollectionId)) {

				PDFProcessorUtil.generateImages(
					sourceFileVersion, destinationFileVersion);
			}
		}
	}

}