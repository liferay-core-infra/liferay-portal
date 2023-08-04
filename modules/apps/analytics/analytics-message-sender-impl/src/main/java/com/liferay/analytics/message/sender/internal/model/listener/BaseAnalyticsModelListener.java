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
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
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
	extends BaseModelListener<T> {

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

		EntityModel<T> entityModel = getEntityModelListener();

		entityModel.addAnalyticsMessage(
			"add", entityModel.getAttributeNames(shardedModel.getCompanyId()),
			model);
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

		EntityModel<T> entityModel = getEntityModelListener();

		entityModel.addAnalyticsMessage("delete", new ArrayList<>(), model);
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
			EntityModel<T> entityModel = getEntityModelListener();

			List<String> modifiedAttributeNames = _getModifiedAttributeNames(
				entityModel.getAttributeNames(shardedModel.getCompanyId()),
				model, getModel((long)model.getPrimaryKeyObj()));

			if (modifiedAttributeNames.isEmpty()) {
				return;
			}

			entityModel.addAnalyticsMessage(
				"update",
				entityModel.getAttributeNames(shardedModel.getCompanyId()),
				model);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected abstract EntityModel<T> getEntityModelListener();

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

		EntityModel<T> entityModel = getEntityModelListener();

		String modelClassName = entityModel.getModelClassName();

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

				entityModel.addAnalyticsMessage(
					"update", userAttributeNames, (T)user);

				if (user.fetchContact() != null) {
					AnalyticsConfiguration analyticsConfiguration =
						analyticsConfigurationRegistry.
							getAnalyticsConfiguration(user.getCompanyId());

					entityModel.addAnalyticsMessage(
						"update",
						Arrays.asList(
							analyticsConfiguration.syncedContactFieldNames()),
						(T)user.fetchContact());
				}
			}

			Map<String, Object> modelAttributes = model.getModelAttributes();

			long companyId = (long)modelAttributes.get("companyId");

			AnalyticsMessage.Builder analyticsMessageBuilder =
				AnalyticsMessage.builder(entityModel.getModelClassName());

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