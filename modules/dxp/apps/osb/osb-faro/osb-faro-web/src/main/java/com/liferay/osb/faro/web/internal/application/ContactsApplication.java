/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.FaroController;

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

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.AccountFaroController)"
	)
	private FaroController _accountFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ActivityFaroController)"
	)
	private FaroController _activityFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ActivityGroupFaroController)"
	)
	private FaroController _activityGroupFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ContactsCardFaroController)"
	)
	private FaroController _contactsCardFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ContactsCardTemplateFaroController)"
	)
	private FaroController _contactsCardTemplateFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ContactsLayoutFaroController)"
	)
	private FaroController _contactsLayoutFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ContactsLayoutTemplateFaroController)"
	)
	private FaroController _contactsLayoutTemplateFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.DataSourceFaroController)"
	)
	private FaroController _dataSourceFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.FieldFaroController)"
	)
	private FaroController _fieldFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.FieldMappingFaroController)"
	)
	private FaroController _fieldMappingFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.IndividualFaroController)"
	)
	private FaroController _individualFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.IndividualSegmentFaroController)"
	)
	private FaroController _individualSegmentFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.InterestFaroController)"
	)
	private FaroController _interestFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.PagesVisitedFaroController)"
	)
	private FaroController _pagesVisitedFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.SessionFaroController)"
	)
	private FaroController _sessionFaroController;

}