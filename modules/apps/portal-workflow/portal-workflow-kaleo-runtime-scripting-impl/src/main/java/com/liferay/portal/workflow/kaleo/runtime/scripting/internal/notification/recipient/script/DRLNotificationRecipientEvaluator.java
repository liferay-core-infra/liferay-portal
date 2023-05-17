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

package com.liferay.portal.workflow.kaleo.runtime.scripting.internal.notification.recipient.script;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.notification.recipient.script.NotificationRecipientEvaluator;
import com.liferay.portal.workflow.kaleo.runtime.scripting.internal.util.RulesEngineExecutor;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	enabled = false, property = "scripting.language=drl",
	service = NotificationRecipientEvaluator.class
)
public class DRLNotificationRecipientEvaluator
	implements NotificationRecipientEvaluator {

	@Override
	public Map<String, ?> evaluate(
			KaleoNotificationRecipient kaleoNotificationRecipient,
			ExecutionContext executionContext)
		throws PortalException {

		return _rulesEngineExecutor.execute(
			executionContext, kaleoNotificationRecipient.getRecipientScript());
	}

	@Reference
	private RulesEngineExecutor _rulesEngineExecutor;

}