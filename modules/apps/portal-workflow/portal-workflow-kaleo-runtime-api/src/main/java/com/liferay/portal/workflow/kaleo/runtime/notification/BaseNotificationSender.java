/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.notification;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.constants.MyWorkflowTasksConstants;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;
import com.liferay.portal.workflow.kaleo.definition.RecipientType;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.notification.recipient.NotificationRecipientBuilder;
import com.liferay.portal.workflow.kaleo.runtime.notification.recipient.NotificationRecipientBuilderRegistry;

import java.io.Serializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
public abstract class BaseNotificationSender implements NotificationSender {

	@Override
	public void sendNotification(
			List<KaleoNotificationRecipient> kaleoNotificationRecipients,
			String defaultSubject, String notificationMessage,
			ExecutionContext executionContext)
		throws NotificationMessageSenderException {

		try {
			Map<NotificationReceptionType, Set<NotificationRecipient>>
				notificationRecipientsMap = getNotificationRecipientsMap(
					kaleoNotificationRecipients, executionContext);

			if (notificationRecipientsMap.isEmpty()) {
				return;
			}

			doSendNotification(
				notificationRecipientsMap, defaultSubject, notificationMessage,
				executionContext);
		}
		catch (Exception exception) {
			throw new NotificationMessageSenderException(
				"Unable to send notification message", exception);
		}
	}

	protected JSONObject createMessageJSONObject(
		String notificationMessage, ExecutionContext executionContext) {

		JSONObject jsonObject = jsonFactory.createJSONObject();

		Map<String, Serializable> workflowContext =
			executionContext.getWorkflowContext();

		jsonObject.put(
			WorkflowConstants.CONTEXT_COMPANY_ID,
			String.valueOf(
				workflowContext.get(WorkflowConstants.CONTEXT_COMPANY_ID))
		).put(
			WorkflowConstants.CONTEXT_CT_COLLECTION_ID,
			String.valueOf(
				workflowContext.get(WorkflowConstants.CONTEXT_CT_COLLECTION_ID))
		).put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME,
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME)
		).put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
			String.valueOf(
				workflowContext.get(WorkflowConstants.CONTEXT_ENTRY_CLASS_PK))
		).put(
			WorkflowConstants.CONTEXT_ENTRY_TYPE,
			(String)workflowContext.get(WorkflowConstants.CONTEXT_ENTRY_TYPE)
		).put(
			WorkflowConstants.CONTEXT_GROUP_ID,
			String.valueOf(
				workflowContext.get(WorkflowConstants.CONTEXT_GROUP_ID))
		).put(
			WorkflowConstants.CONTEXT_URL,
			String.valueOf(workflowContext.get(WorkflowConstants.CONTEXT_URL))
		);

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		jsonObject.put(
			WorkflowConstants.CONTEXT_USER_ID,
			String.valueOf(_getUserId(executionContext, kaleoInstanceToken))
		).put(
			"notificationMessage", notificationMessage
		).put(
			"plid", workflowContext.get("plid")
		).put(
			"portletId", workflowContext.get("portletId")
		).put(
			"workflowInstanceId", kaleoInstanceToken.getKaleoInstanceId()
		);

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			executionContext.getKaleoTaskInstanceToken();

		if (kaleoTaskInstanceToken != null) {
			jsonObject.put(
				"workflowTaskId",
				kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());
		}

		return jsonObject;
	}

	protected abstract void doSendNotification(
			Map<NotificationReceptionType, Set<NotificationRecipient>>
				notificationRecipientsMap,
			String defaultSubject, String notificationMessage,
			ExecutionContext executionContext)
		throws Exception;

	protected Set<NotificationRecipient> getDeliverableNotificationRecipients(
			Set<NotificationRecipient> notificationRecipients,
			int notificationDeliveryType)
		throws PortalException {

		Set<NotificationRecipient> newNotificationRecipients = new HashSet<>();

		if (notificationRecipients == null) {
			return newNotificationRecipients;
		}

		for (NotificationRecipient notificationRecipient :
				notificationRecipients) {

			if (notificationRecipient.getUserId() <= 0) {
				continue;
			}

			if (UserNotificationManagerUtil.isDeliver(
					notificationRecipient.getUserId(),
					PortletKeys.MY_WORKFLOW_TASK, 0,
					MyWorkflowTasksConstants.
						NOTIFICATION_TYPE_MY_WORKFLOW_TASKS,
					notificationDeliveryType)) {

				newNotificationRecipients.add(notificationRecipient);
			}
		}

		return newNotificationRecipients;
	}

	protected Map<NotificationReceptionType, Set<NotificationRecipient>>
			getNotificationRecipientsMap(
				List<KaleoNotificationRecipient> kaleoNotificationRecipients,
				ExecutionContext executionContext)
		throws Exception {

		Map<NotificationReceptionType, Set<NotificationRecipient>>
			notificationRecipientsMap = new HashMap<>();

		if (kaleoNotificationRecipients.isEmpty()) {
			Set<NotificationRecipient> notificationRecipients =
				retrieveNotificationRecipients(
					notificationRecipientsMap, NotificationReceptionType.TO);

			NotificationRecipientBuilder notificationRecipientBuilder =
				notificationRecipientBuilderRegistry.
					getNotificationRecipientBuilder(RecipientType.ASSIGNEES);

			notificationRecipientBuilder.processKaleoNotificationRecipient(
				notificationRecipients, null, NotificationReceptionType.TO,
				executionContext);

			return notificationRecipientsMap;
		}

		for (KaleoNotificationRecipient kaleoNotificationRecipient :
				kaleoNotificationRecipients) {

			NotificationReceptionType notificationReceptionType =
				NotificationReceptionType.parse(
					kaleoNotificationRecipient.getNotificationReceptionType());

			Set<NotificationRecipient> notificationRecipients =
				retrieveNotificationRecipients(
					notificationRecipientsMap, notificationReceptionType);

			RecipientType recipientType = RecipientType.parse(
				kaleoNotificationRecipient.getRecipientClassName());

			NotificationRecipientBuilder notificationRecipientBuilder =
				notificationRecipientBuilderRegistry.
					getNotificationRecipientBuilder(recipientType);

			notificationRecipientBuilder.processKaleoNotificationRecipient(
				notificationRecipients, kaleoNotificationRecipient,
				notificationReceptionType, executionContext);
		}

		return notificationRecipientsMap;
	}

	protected Set<NotificationRecipient> retrieveNotificationRecipients(
		Map<NotificationReceptionType, Set<NotificationRecipient>>
			notificationRecipientsMap,
		NotificationReceptionType notificationReceptionType) {

		Set<NotificationRecipient> notificationRecipients =
			notificationRecipientsMap.get(notificationReceptionType);

		if (notificationRecipients == null) {
			notificationRecipients = new HashSet<>();

			notificationRecipientsMap.put(
				notificationReceptionType, notificationRecipients);
		}

		return notificationRecipients;
	}

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected NotificationRecipientBuilderRegistry
		notificationRecipientBuilderRegistry;

	private long _getUserId(
		ExecutionContext executionContext,
		KaleoInstanceToken kaleoInstanceToken) {

		try {
			ServiceContext serviceContext =
				executionContext.getServiceContext();

			return serviceContext.getGuestOrUserId();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get user from context, using userId from " +
						"kaleoInstanceToken instead",
					portalException);
			}

			return kaleoInstanceToken.getUserId();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseNotificationSender.class);

}