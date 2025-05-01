/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.pdf.internal.messaging;

import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.DestinationNames;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(
	property = "destination.name=" + DestinationNames.DOCUMENT_LIBRARY_PDF_PROCESSOR,
	service = DestinationDefinition.class
)
public class PDFProcessorDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return DestinationNames.DOCUMENT_LIBRARY_PDF_PROCESSOR;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SERIAL;
	}

}