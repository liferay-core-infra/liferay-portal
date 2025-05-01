/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.messaging.config;

import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.security.ldap.internal.constants.LDAPDestinationNames;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(
	property = "destination.name=" + LDAPDestinationNames.SCHEDULED_USER_LDAP_IMPORT,
	service = DestinationDefinition.class
)
public class MessagingConfigurator implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return LDAPDestinationNames.SCHEDULED_USER_LDAP_IMPORT;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SERIAL;
	}

}