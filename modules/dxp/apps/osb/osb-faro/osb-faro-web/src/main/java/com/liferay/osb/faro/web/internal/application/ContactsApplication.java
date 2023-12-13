/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.contacts.AccountFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ActivityFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ActivityGroupFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsCardFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsCardTemplateFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsLayoutFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsLayoutTemplateFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.DataSourceFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.FieldFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.FieldMappingFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.IndividualFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.IndividualSegmentFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.InterestFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.PagesVisitedFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.SessionFaroController;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@ApplicationPath("/" + FaroConstants.APPLICATION_CONTACTS)
@Component(property = "jaxrs.application=true", service = Application.class)
public class ContactsApplication extends BaseApplication {

	@Override
	public Set<Object> getControllers() {
		Set<Object> controllers = new HashSet<>();

		controllers.add(_accountFaroController);
		controllers.add(_activityFaroController);
		controllers.add(_activityGroupFaroController);
		controllers.add(_contactsCardFaroController);
		controllers.add(_contactsCardTemplateFaroController);
		controllers.add(_contactsLayoutFaroController);
		controllers.add(_contactsLayoutTemplateFaroController);
		controllers.add(_dataSourceFaroController);
		controllers.add(_fieldFaroController);
		controllers.add(_fieldMappingFaroController);
		controllers.add(_individualFaroController);
		controllers.add(_individualSegmentFaroController);
		controllers.add(_interestFaroController);
		controllers.add(_pagesVisitedFaroController);
		controllers.add(_sessionFaroController);

		return controllers;
	}

	@Reference
	private AccountFaroController _accountFaroController;

	@Reference
	private ActivityFaroController _activityFaroController;

	@Reference
	private ActivityGroupFaroController _activityGroupFaroController;

	@Reference
	private ContactsCardFaroController _contactsCardFaroController;

	@Reference
	private ContactsCardTemplateFaroController
		_contactsCardTemplateFaroController;

	@Reference
	private ContactsLayoutFaroController _contactsLayoutFaroController;

	@Reference
	private ContactsLayoutTemplateFaroController
		_contactsLayoutTemplateFaroController;

	@Reference
	private DataSourceFaroController _dataSourceFaroController;

	@Reference
	private FieldFaroController _fieldFaroController;

	@Reference
	private FieldMappingFaroController _fieldMappingFaroController;

	@Reference
	private IndividualFaroController _individualFaroController;

	@Reference
	private IndividualSegmentFaroController _individualSegmentFaroController;

	@Reference
	private InterestFaroController _interestFaroController;

	@Reference
	private PagesVisitedFaroController _pagesVisitedFaroController;

	@Reference
	private SessionFaroController _sessionFaroController;

}