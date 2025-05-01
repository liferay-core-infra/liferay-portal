/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.messaging;

import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "destination.name=" + CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
	service = MessageListener.class
)
public class ScheduledPublishMessageListener extends BaseMessageListener {

	@Override
	protected void doReceive(Message message) throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			message.getLong("ctCollectionId"));

		if ((ctCollection != null) &&
			(ctCollection.getStatus() == WorkflowConstants.STATUS_SCHEDULED)) {

			_ctProcessLocalService.addCTProcess(
				message.getLong("userId"), ctCollection.getCtCollectionId());
		}
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTProcessLocalService _ctProcessLocalService;

}