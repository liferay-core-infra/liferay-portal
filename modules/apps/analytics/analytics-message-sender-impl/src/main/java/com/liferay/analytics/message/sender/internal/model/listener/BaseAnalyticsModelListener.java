/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.model.AnalyticsMessage;
import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
public abstract class BaseAnalyticsModelListener<T extends BaseModel<T>>
	extends BaseModelListener<T> implements EntityModel<T> {

	@Override
	public void addAnalyticsMessage(
		String eventType, List<String> includeAttributeNames, T model) {

		String modelClassName = model.getModelClassName();

		if (modelClassName.equals(Contact.class.getName())) {
			Contact contact = (Contact)model;

			User user = userLocalService.fetchUser(contact.getClassPK());

			if ((!StringUtil.equalsIgnoreCase(eventType, "delete") &&
				 !isUserActive(user)) ||
				isUserExcluded(user)) {

				return;
			}
		}
		else if (modelClassName.equals(User.class.getName())) {
			User user = (User)model;

			if ((!StringUtil.equalsIgnoreCase(eventType, "delete") &&
				 !isUserActive(user)) ||
				isUserExcluded(user)) {

				return;
			}
		}
		else if (isExcluded(model)) {
			return;
		}

		JSONObject jsonObject = serialize(model, includeAttributeNames);

		ShardedModel shardedModel = (ShardedModel)model;

		if (modelClassName.equals(ExpandoRow.class.getName())) {
			ExpandoRow expandoRow = (ExpandoRow)model;

			if (isCustomField(
					Organization.class.getName(), expandoRow.getTableId())) {

				modelClassName = Organization.class.getName();
			}
			else {
				modelClassName = User.class.getName();
			}
		}

		try {
			AnalyticsMessage.Builder analyticsMessageBuilder =
				AnalyticsMessage.builder(modelClassName);

			analyticsMessageBuilder.action(eventType);
			analyticsMessageBuilder.object(jsonObject);

			String analyticsMessageJSON =
				analyticsMessageBuilder.buildJSONString();

			analyticsMessageLocalService.addAnalyticsMessage(
				shardedModel.getCompanyId(),
				userLocalService.getGuestUserId(shardedModel.getCompanyId()),
				analyticsMessageJSON.getBytes(Charset.defaultCharset()));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to add analytics message " + jsonObject.toString(),
					exception);
			}
		}
	}

	@Override
	public long[] getMembershipIds(User user) throws Exception {
		return new long[0];
	}

	@Override
	public String getModelClassName() {
		return null;
	}

	@Override
	public void onAfterAddAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		if (FeatureFlagManagerUtil.isEnabled("LRAC-10632") ||
			!analyticsConfigurationRegistry.isActive()) {

			return;
		}

		_onAfterUpdateAssociation(
			classPK, associationClassName, associationClassPK,
			"addAssociation");
	}

	@Override
	public void onAfterCreate(T model) throws ModelListenerException {
		if (FeatureFlagManagerUtil.isEnabled("LRAC-10632") ||
			!analyticsConfigurationRegistry.isActive()) {

			return;
		}

		ShardedModel shardedModel = (ShardedModel)model;

		addAnalyticsMessage(
			"add", getAttributeNames(shardedModel.getCompanyId()), model);
	}

	@Override
	public void onAfterRemoveAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		if (FeatureFlagManagerUtil.isEnabled("LRAC-10632") ||
			!analyticsConfigurationRegistry.isActive()) {

			return;
		}

		_onAfterUpdateAssociation(
			classPK, associationClassName, associationClassPK,
			"deleteAssociation");
	}

	@Override
	public void onBeforeRemove(T model) throws ModelListenerException {
		if (FeatureFlagManagerUtil.isEnabled("LRAC-10632") ||
			!analyticsConfigurationRegistry.isActive()) {

			return;
		}

		addAnalyticsMessage("delete", new ArrayList<>(), model);
	}

	@Override
	public void onBeforeUpdate(T originalModel, T model)
		throws ModelListenerException {

		if (FeatureFlagManagerUtil.isEnabled("LRAC-10632") ||
			!analyticsConfigurationRegistry.isActive()) {

			return;
		}

		ShardedModel shardedModel = (ShardedModel)model;

		try {
			List<String> modifiedAttributeNames = _getModifiedAttributeNames(
				getAttributeNames(shardedModel.getCompanyId()), model,
				getModel((long)model.getPrimaryKeyObj()));

			if (modifiedAttributeNames.isEmpty()) {
				return;
			}

			addAnalyticsMessage(
				"update", getAttributeNames(shardedModel.getCompanyId()),
				model);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void syncAll(long companyId) throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery();

		if (actionableDynamicQuery == null) {
			return;
		}

		actionableDynamicQuery.setCompanyId(companyId);
		actionableDynamicQuery.setPerformActionMethod(
			(T model) -> addAnalyticsMessage(
				"add", getAttributeNames(companyId), model));

		actionableDynamicQuery.performActions();
	}

	protected void updateConfigurationProperties(
		long companyId, String configurationPropertyName, String modelId,
		String preferencePropertyName) {

		Dictionary<String, Object> configurationProperties =
			analyticsConfigurationRegistry.getAnalyticsConfigurationProperties(
				companyId);

		if (configurationProperties == null) {
			return;
		}

		String[] modelIds = (String[])configurationProperties.get(
			configurationPropertyName);

		if (!ArrayUtil.contains(modelIds, modelId)) {
			return;
		}

		modelIds = ArrayUtil.remove(modelIds, modelId);

		if (Validator.isNotNull(preferencePropertyName)) {
			try {
				companyService.updatePreferences(
					companyId,
					UnicodePropertiesBuilder.create(
						true
					).put(
						preferencePropertyName,
						StringUtil.merge(modelIds, StringPool.COMMA)
					).build());
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to update preferences for company " + companyId,
						exception);
				}
			}
		}

		configurationProperties.put(configurationPropertyName, modelIds);

		try {
			configurationProvider.saveCompanyConfiguration(
				AnalyticsConfiguration.class, companyId,
				configurationProperties);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to update configuration for company " + companyId,
					exception);
			}
		}
	}

	@Reference
	protected CompanyService companyService;

	@Reference
	protected ConfigurationProvider configurationProvider;

	private List<String> _getModifiedAttributeNames(
		List<String> attributeNames, T model, T originalModel) {

		List<String> modifiedAttributeNames = new ArrayList<>();

		for (String attributeName : attributeNames) {
			if (attributeName.equalsIgnoreCase("expando") ||
				attributeName.equalsIgnoreCase("memberships") ||
				(attributeName.equalsIgnoreCase("modifiedDate") &&
				 !Objects.equals(
					 model.getModelClassName(), ExpandoRow.class.getName()))) {

				continue;
			}

			String value = String.valueOf(
				BeanPropertiesUtil.getObject(model, attributeName));
			String originalValue = String.valueOf(
				BeanPropertiesUtil.getObject(originalModel, attributeName));

			if (!Objects.equals(value, originalValue)) {
				modifiedAttributeNames.add(attributeName);
			}
		}

		return modifiedAttributeNames;
	}

	private void _onAfterUpdateAssociation(
		Object classPK, String associationClassName, Object associationClassPK,
		String eventType) {

		String modelClassName = getModelClassName();

		if ((modelClassName == null) ||
			!associationClassName.equals(User.class.getName())) {

			return;
		}

		try {
			T model = getModel((long)classPK);

			if (isExcluded(model)) {
				return;
			}

			User user = userLocalService.fetchUser((long)associationClassPK);

			if (!eventType.equals("deleteAssociation") &&
				(!isUserActive(user) || isUserExcluded(user))) {

				return;
			}

			if (!eventType.equals("deleteAssociation")) {
				List<String> userAttributeNames = getUserAttributeNames(
					user.getCompanyId());

				userAttributeNames.add("associations");
				userAttributeNames.add("userId");

				addAnalyticsMessage("update", userAttributeNames, (T)user);

				if (user.fetchContact() != null) {
					AnalyticsConfiguration analyticsConfiguration =
						analyticsConfigurationRegistry.
							getAnalyticsConfiguration(user.getCompanyId());

					addAnalyticsMessage(
						"update",
						Arrays.asList(
							analyticsConfiguration.syncedContactFieldNames()),
						(T)user.fetchContact());
				}
			}

			Map<String, Object> modelAttributes = model.getModelAttributes();

			long companyId = (long)modelAttributes.get("companyId");

			AnalyticsMessage.Builder analyticsMessageBuilder =
				AnalyticsMessage.builder(getModelClassName());

			analyticsMessageBuilder.action(eventType);
			analyticsMessageBuilder.object(
				JSONUtil.put(
					"classPK", classPK
				).put(
					"emailAddress", user.getEmailAddress()
				).put(
					"userId", associationClassPK
				));

			String analyticsMessageJSON =
				analyticsMessageBuilder.buildJSONString();

			analyticsMessageLocalService.addAnalyticsMessage(
				companyId, userLocalService.getGuestUserId(companyId),
				analyticsMessageJSON.getBytes(Charset.defaultCharset()));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					String.format(
						"Unable to get %s %s", modelClassName, classPK),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAnalyticsModelListener.class);

}