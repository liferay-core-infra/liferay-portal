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

package com.liferay.data.engine.taglib.internal.servlet.taglib.util;

import com.liferay.data.engine.content.type.DataDefinitionContentType;
import com.liferay.data.engine.content.type.DataDefinitionContentTypeRegistry;
import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.renderer.DataLayoutRenderer;
import com.liferay.data.engine.renderer.DataLayoutRendererContext;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.dto.v2_0.DataRecord;
import com.liferay.data.engine.rest.dto.v2_0.DataRule;
import com.liferay.data.engine.rest.dto.v2_0.util.DataDefinitionDDMFormUtil;
import com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.resource.v2_0.DataLayoutResource;
import com.liferay.data.engine.rest.resource.v2_0.DataRecordResource;
import com.liferay.data.engine.taglib.servlet.taglib.definition.DataLayoutBuilderDefinition;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.spi.form.builder.settings.DDMFormBuilderSettingsRetrieverHelper;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutFactory;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Gabriel Albuquerque
 * @author Leonardo Barros
 */
public class DataLayoutTaglibUtil {

	public static Set<Locale> getAvailableLocales(
		Long dataDefinitionId, Long dataLayoutId,
		HttpServletRequest httpServletRequest) {

		return _getAvailableLocales(
			dataDefinitionId, dataLayoutId, httpServletRequest);
	}

	public static JSONObject getContentTypeConfigJSONObject(
		String contentType) {

		DataDefinitionContentType dataDefinitionContentType =
			_getDataDefinitionContentType(contentType);

		if (dataDefinitionContentType == null) {
			dataDefinitionContentType = _getDataDefinitionContentType(
				"default");
		}

		return JSONUtil.put(
			"allowInvalidAvailableLocalesForProperty",
			dataDefinitionContentType.allowInvalidAvailableLocalesForProperty()
		).put(
			"allowReferencedDataDefinitionDeletion",
			dataDefinitionContentType.allowReferencedDataDefinitionDeletion()
		);
	}

	public static DataDefinition getDataDefinition(
			long dataDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception {

		return _getDataDefinition(dataDefinitionId, httpServletRequest);
	}

	public static JSONObject getDataLayoutConfigJSONObject(
		String contentType, Locale locale) {

		DataLayoutBuilderDefinition dataLayoutBuilderDefinition =
			_getDataLayoutBuilderDefinition(contentType);

		JSONObject dataLayoutConfigJSONObject = JSONUtil.put(
			"allowFieldSets", dataLayoutBuilderDefinition.allowFieldSets()
		).put(
			"allowMultiplePages",
			dataLayoutBuilderDefinition.allowMultiplePages()
		).put(
			"allowNestedFields", dataLayoutBuilderDefinition.allowNestedFields()
		).put(
			"allowRules", dataLayoutBuilderDefinition.allowRules()
		).put(
			"allowSuccessPage", dataLayoutBuilderDefinition.allowSuccessPage()
		).put(
			"disabledProperties",
			dataLayoutBuilderDefinition.getDisabledProperties()
		).put(
			"disabledTabs", dataLayoutBuilderDefinition.getDisabledTabs()
		).put(
			"visibleProperties",
			dataLayoutBuilderDefinition.getVisibleProperties()
		);

		if (dataLayoutBuilderDefinition.allowRules()) {
			try {
				dataLayoutConfigJSONObject.put(
					"ruleSettings",
					JSONUtil.put(
						"dataProviderInstanceParameterSettingsURL",
						_getDDMDataProviderInstanceParameterSettingsURL()
					).put(
						"dataProviderInstancesURL",
						_getDDMDataProviderInstancesURL()
					).put(
						"functionsMetadata",
						_getFunctionsMetadataJSONObject(locale)
					).put(
						"functionsURL", _getFunctionsURL()
					));
			}
			catch (JSONException jsonException) {
				_log.error(jsonException);
			}
		}

		dataLayoutConfigJSONObject.put(
			"unimplementedProperties",
			dataLayoutBuilderDefinition.getUnimplementedProperties());

		return dataLayoutConfigJSONObject;
	}

	public static JSONObject getDataLayoutJSONObject(
		Set<Locale> availableLocales, String contentType, Long dataDefinitionId,
		Long dataLayoutId, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return _getDataLayoutJSONObject(
			availableLocales, contentType, dataDefinitionId, dataLayoutId,
			httpServletRequest, httpServletResponse);
	}

	public static Map<String, Object> getDataRecordValues(
			Long dataRecordId, HttpServletRequest httpServletRequest)
		throws Exception {

		return _getDataRecordValues(dataRecordId, httpServletRequest);
	}

	public static Long getDefaultDataLayoutId(
			Long dataDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception {

		return _getDefaultDataLayoutId(dataDefinitionId, httpServletRequest);
	}

	public static JSONArray getFieldTypesJSONArray(
			HttpServletRequest httpServletRequest, Set<String> scopes,
			boolean searchableFieldsDisabled)
		throws Exception {

		return _getFieldTypesJSONArray(
			httpServletRequest, scopes, searchableFieldsDisabled);
	}

	public static String renderDataLayout(
			Long dataLayoutId,
			DataLayoutRendererContext dataLayoutRendererContext)
		throws Exception {

		DataLayoutRenderer dataLayoutRenderer =
			_dataLayoutRendererSnapshot.get();

		return dataLayoutRenderer.render(
			dataLayoutId, dataLayoutRendererContext);
	}

	public static String resolveFieldTypesModules() {
		return _resolveFieldTypesModules();
	}

	public static String resolveModule(String moduleName) {
		NPMResolver npmResolver = _npmResolverSnapshot.get();

		return npmResolver.resolveModuleName(moduleName);
	}

	private static Set<Locale> _getAvailableLocales(
		Long dataDefinitionId, Long dataLayoutId,
		HttpServletRequest httpServletRequest) {

		if (Validator.isNull(dataDefinitionId) &&
			Validator.isNull(dataLayoutId)) {

			return SetUtil.fromArray(LocaleThreadLocal.getSiteDefaultLocale());
		}

		try {
			Set<Locale> availableLocales = new HashSet<>();

			DataDefinition dataDefinition = null;

			if (Validator.isNotNull(dataDefinitionId)) {
				dataDefinition = _getDataDefinition(
					dataDefinitionId, httpServletRequest);
			}
			else {
				DataLayout dataLayout = _getDataLayout(
					dataLayoutId, httpServletRequest);

				dataDefinition = _getDataDefinition(
					dataLayout.getDataDefinitionId(), httpServletRequest);
			}

			for (String languageId : dataDefinition.getAvailableLanguageIds()) {
				availableLocales.add(LocaleUtil.fromLanguageId(languageId));
			}

			return availableLocales;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return SetUtil.fromArray(LocaleThreadLocal.getSiteDefaultLocale());
	}

	private static DataDefinition _getDataDefinition(
			Long dataDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception {

		DataDefinitionResource.Factory dataDefinitionResourceFactory =
			_dataDefinitionResourceFactorySnapshot.get();

		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			dataDefinitionResourceFactory.create();

		Portal portal = _portalSnapshot.get();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.httpServletRequest(
				httpServletRequest
			).user(
				portal.getUser(httpServletRequest)
			).build();

		return dataDefinitionResource.getDataDefinition(dataDefinitionId);
	}

	private static DataDefinitionContentType _getDataDefinitionContentType(
		String contentType) {

		try {
			DataDefinitionContentTypeRegistry
				dataDefinitionContentTypeRegistry =
					_dataDefinitionContentTypeRegistrySnapshot.get();

			return dataDefinitionContentTypeRegistry.
				getDataDefinitionContentType(contentType);
		}
		catch (Exception exception) {
			if (exception instanceof
					DataDefinitionValidationException.MustSetValidContentType) {

				return null;
			}

			throw new RuntimeException(exception);
		}
	}

	private static DataLayout _getDataLayout(
			Long dataLayoutId, HttpServletRequest httpServletRequest)
		throws Exception {

		DataLayoutResource.Factory dataLayoutResourceFactory =
			_dataLayoutResourceFactorySnapshot.get();

		DataLayoutResource.Builder dataLayoutResourceBuilder =
			dataLayoutResourceFactory.create();

		Portal portal = _portalSnapshot.get();

		DataLayoutResource dataLayoutResource =
			dataLayoutResourceBuilder.httpServletRequest(
				httpServletRequest
			).user(
				portal.getUser(httpServletRequest)
			).build();

		return dataLayoutResource.getDataLayout(dataLayoutId);
	}

	private static DataLayoutBuilderDefinition _getDataLayoutBuilderDefinition(
		String contentType) {

		DataLayoutBuilderDefinition dataLayoutBuilderDefinition =
			_serviceTrackerMap.getService(contentType);

		if (dataLayoutBuilderDefinition == null) {
			return _defaultDataLayoutBuilderDefinition;
		}

		return dataLayoutBuilderDefinition;
	}

	private static JSONObject _getDataLayoutJSONObject(
		Set<Locale> availableLocales, String contentType, Long dataDefinitionId,
		Long dataLayoutId, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			String dataLayoutString = ParamUtil.getString(
				httpServletRequest, "dataLayout");

			if (Validator.isNotNull(dataLayoutString)) {
				DataLayoutDDMFormAdapter dataLayoutDDMFormAdapter =
					new DataLayoutDDMFormAdapter(
						availableLocales, contentType,
						DataLayout.toDTO(dataLayoutString), httpServletRequest,
						httpServletResponse);

				return dataLayoutDDMFormAdapter.toJSONObject();
			}

			DataLayout dataLayout = null;

			if (Validator.isNotNull(dataLayoutId)) {
				dataLayout = _getDataLayout(dataLayoutId, httpServletRequest);
			}
			else {
				DataDefinition dataDefinition = _getDataDefinition(
					dataDefinitionId, httpServletRequest);

				dataLayout = dataDefinition.getDefaultDataLayout();
			}

			DataLayoutDDMFormAdapter dataLayoutDDMFormAdapter =
				new DataLayoutDDMFormAdapter(
					availableLocales, contentType, dataLayout,
					httpServletRequest, httpServletResponse);

			return dataLayoutDDMFormAdapter.toJSONObject();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			JSONFactory jsonFactory = _jsonFactorySnapshot.get();

			return jsonFactory.createJSONObject();
		}
	}

	private static Map<String, Object> _getDataRecordValues(
			Long dataRecordId, HttpServletRequest httpServletRequest)
		throws Exception {

		if (Validator.isNull(dataRecordId)) {
			return Collections.emptyMap();
		}

		DataRecordResource.Factory dataRecordResourceFactory =
			_dataRecordResourceFactorySnapshot.get();

		DataRecordResource.Builder dataRecordResourceBuilder =
			dataRecordResourceFactory.create();

		Portal portal = _portalSnapshot.get();

		DataRecordResource dataRecordResource = dataRecordResourceBuilder.user(
			portal.getUser(httpServletRequest)
		).build();

		DataRecord dataRecord = dataRecordResource.getDataRecord(dataRecordId);

		return dataRecord.getDataRecordValues();
	}

	private static String _getDDMDataProviderInstanceParameterSettingsURL() {
		DDMFormBuilderSettingsRetrieverHelper
			ddmFormBuilderSettingsRetrieverHelper =
				_ddmFormBuilderSettingsRetrieverHelperSnapshot.get();

		return ddmFormBuilderSettingsRetrieverHelper.
			getDDMDataProviderInstanceParameterSettingsURL();
	}

	private static String _getDDMDataProviderInstancesURL() {
		DDMFormBuilderSettingsRetrieverHelper
			ddmFormBuilderSettingsRetrieverHelper =
				_ddmFormBuilderSettingsRetrieverHelperSnapshot.get();

		return ddmFormBuilderSettingsRetrieverHelper.
			getDDMDataProviderInstancesURL();
	}

	private static Long _getDefaultDataLayoutId(
			Long dataDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception {

		DataDefinition dataDefinition = getDataDefinition(
			dataDefinitionId, httpServletRequest);

		if (dataDefinition == null) {
			return 0L;
		}

		DataLayout dataLayout = dataDefinition.getDefaultDataLayout();

		if (dataLayout == null) {
			return 0L;
		}

		return dataLayout.getId();
	}

	private static JSONArray _getFieldTypesJSONArray(
			HttpServletRequest httpServletRequest, Set<String> scopes,
			boolean searchableFieldsDisabled)
		throws Exception {

		JSONFactory jsonFactory = _jsonFactorySnapshot.get();

		JSONArray fieldTypesJSONArray = jsonFactory.createJSONArray();

		DataDefinitionResource.Factory dataDefinitionResourceFactory =
			_dataDefinitionResourceFactorySnapshot.get();

		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			dataDefinitionResourceFactory.create();

		Portal portal = _portalSnapshot.get();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.httpServletRequest(
				httpServletRequest
			).user(
				portal.getUser(httpServletRequest)
			).build();

		try {
			JSONArray jsonArray = jsonFactory.createJSONArray(
				dataDefinitionResource.
					getDataDefinitionDataDefinitionFieldFieldTypes());

			if (SetUtil.isEmpty(scopes)) {
				return jsonArray;
			}

			for (JSONObject jsonObject : (Iterable<JSONObject>)jsonArray) {
				if (ListUtil.exists(
						Arrays.asList(
							StringUtil.split(jsonObject.getString("scope"))),
						scopes::contains)) {

					fieldTypesJSONArray.put(jsonObject);

					if (searchableFieldsDisabled) {
						_setFieldIndexTypeNone(
							jsonObject.getJSONObject("settingsContext"));
					}
				}
			}

			return fieldTypesJSONArray;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return fieldTypesJSONArray;
		}
	}

	private static JSONObject _getFunctionsMetadataJSONObject(Locale locale)
		throws JSONException {

		JSONFactory jsonFactory = _jsonFactorySnapshot.get();
		DDMFormBuilderSettingsRetrieverHelper
			ddmFormBuilderSettingsRetrieverHelper =
				_ddmFormBuilderSettingsRetrieverHelperSnapshot.get();

		return jsonFactory.createJSONObject(
			ddmFormBuilderSettingsRetrieverHelper.
				getSerializedDDMExpressionFunctionsMetadata(locale));
	}

	private static String _getFunctionsURL() {
		DDMFormBuilderSettingsRetrieverHelper
			ddmFormBuilderSettingsRetrieverHelper =
				_ddmFormBuilderSettingsRetrieverHelperSnapshot.get();

		return ddmFormBuilderSettingsRetrieverHelper.getDDMFunctionsURL();
	}

	private static boolean _hasJavascriptModule(String name) {
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry =
			_ddmFormFieldTypeServicesRegistrySnapshot.get();

		DDMFormFieldType ddmFormFieldType =
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(name);

		return Validator.isNotNull(ddmFormFieldType.getModuleName());
	}

	private static String _resolveFieldTypeModule(String name) {
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry =
			_ddmFormFieldTypeServicesRegistrySnapshot.get();

		return _resolveModuleName(
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(name));
	}

	private static String _resolveFieldTypesModules() {
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry =
			_ddmFormFieldTypeServicesRegistrySnapshot.get();

		return StringUtil.merge(
			TransformUtil.transform(
				ddmFormFieldTypeServicesRegistry.getDDMFormFieldTypeNames(),
				name -> {
					if (!_hasJavascriptModule(name)) {
						return null;
					}

					return _resolveFieldTypeModule(name);
				}),
			StringPool.COMMA);
	}

	private static String _resolveModuleName(
		DDMFormFieldType ddmFormFieldType) {

		if (Validator.isNull(ddmFormFieldType.getModuleName())) {
			return StringPool.BLANK;
		}

		if (ddmFormFieldType.isCustomDDMFormFieldType()) {
			return ddmFormFieldType.getModuleName();
		}

		NPMResolver npmResolver = _npmResolverSnapshot.get();

		return npmResolver.resolveModuleName(ddmFormFieldType.getModuleName());
	}

	private static void _setFieldIndexTypeNone(JSONObject jsonObject) {
		for (JSONObject pageJSONObject :
				(Iterable<JSONObject>)jsonObject.getJSONArray("pages")) {

			for (JSONObject rowJSONObject :
					(Iterable<JSONObject>)pageJSONObject.getJSONArray("rows")) {

				for (JSONObject columnJSONObject :
						(Iterable<JSONObject>)rowJSONObject.getJSONArray(
							"columns")) {

					for (JSONObject fieldJSONObject :
							(Iterable<JSONObject>)columnJSONObject.getJSONArray(
								"fields")) {

						if (Objects.equals(
								fieldJSONObject.getString("fieldName"),
								"indexType")) {

							fieldJSONObject.put("value", "none");

							return;
						}
					}
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataLayoutTaglibUtil.class);

	private static final Snapshot<DataDefinitionContentTypeRegistry>
		_dataDefinitionContentTypeRegistrySnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class,
			DataDefinitionContentTypeRegistry.class);
	private static final Snapshot<DataDefinitionResource.Factory>
		_dataDefinitionResourceFactorySnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class, DataDefinitionResource.Factory.class);
	private static final Snapshot<DataLayoutRenderer>
		_dataLayoutRendererSnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class, DataLayoutRenderer.class);
	private static final Snapshot<DataLayoutResource.Factory>
		_dataLayoutResourceFactorySnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class, DataLayoutResource.Factory.class);
	private static final Snapshot<DataRecordResource.Factory>
		_dataRecordResourceFactorySnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class, DataRecordResource.Factory.class);
	private static final Snapshot<DDMFormBuilderSettingsRetrieverHelper>
		_ddmFormBuilderSettingsRetrieverHelperSnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class,
			DDMFormBuilderSettingsRetrieverHelper.class);
	private static final Snapshot<DDMFormFieldTypeServicesRegistry>
		_ddmFormFieldTypeServicesRegistrySnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class, DDMFormFieldTypeServicesRegistry.class);
	private static final Snapshot<DDMFormTemplateContextFactory>
		_ddmFormTemplateContextFactorySnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class, DDMFormTemplateContextFactory.class);
	private static final Snapshot<DDMStructureLayoutLocalService>
		_ddmStructureLayoutLocalServiceSnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class, DDMStructureLayoutLocalService.class);
	private static final Snapshot<DDMStructureLocalService>
		_ddmStructureLocalServiceSnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class, DDMStructureLocalService.class);
	private static final DataLayoutBuilderDefinition
		_defaultDataLayoutBuilderDefinition =
			new DataLayoutBuilderDefinition() {
			};
	private static final Snapshot<DDMFormLayoutDeserializer>
		_jsonDDMFormLayoutDeserializerSnapshot = new Snapshot<>(
			DataLayoutTaglibUtil.class, DDMFormLayoutDeserializer.class,
			"(ddm.form.layout.deserializer.type=json)");
	private static final Snapshot<JSONFactory> _jsonFactorySnapshot =
		new Snapshot<>(DataLayoutTaglibUtil.class, JSONFactory.class);
	private static final Snapshot<Language> _languageSnapshot = new Snapshot<>(
		DataLayoutTaglibUtil.class, Language.class);
	private static final Snapshot<NPMResolver> _npmResolverSnapshot =
		new Snapshot<>(DataLayoutTaglibUtil.class, NPMResolver.class);
	private static final Snapshot<Portal> _portalSnapshot = new Snapshot<>(
		DataLayoutTaglibUtil.class, Portal.class);
	private static final ServiceTrackerMap<String, DataLayoutBuilderDefinition>
		_serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(DataLayoutTaglibUtil.class);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundle.getBundleContext(), DataLayoutBuilderDefinition.class,
			"content.type");
	}

	private static class DataLayoutDDMFormAdapter {

		public DataLayoutDDMFormAdapter(
			Set<Locale> availableLocales, String contentType,
			DataLayout dataLayout, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			_availableLocales = availableLocales;
			_contentType = contentType;
			_dataLayout = dataLayout;
			_httpServletRequest = httpServletRequest;
			_httpServletResponse = httpServletResponse;
		}

		public JSONObject toJSONObject() throws Exception {
			DDMForm ddmForm = null;

			if (_dataLayout.getId() == null) {
				DataDefinition dataDefinition = DataDefinition.toDTO(
					_httpServletRequest.getParameter("dataDefinition"));

				DDMFormFieldTypeServicesRegistry
					ddmFormFieldTypeServicesRegistry =
						_ddmFormFieldTypeServicesRegistrySnapshot.get();

				ddmForm = DataDefinitionDDMFormUtil.toDDMForm(
					dataDefinition, ddmFormFieldTypeServicesRegistry);
			}
			else {
				ddmForm = _getDDMForm();
			}

			Locale defaultLocale = ddmForm.getDefaultLocale();

			DDMFormTemplateContextFactory ddmFormTemplateContextFactory =
				_ddmFormTemplateContextFactorySnapshot.get();

			Map<String, Object> ddmFormTemplateContext =
				ddmFormTemplateContextFactory.create(
					ddmForm, _getDDMFormLayout(),
					new DDMFormRenderingContext() {
						{
							setHttpServletRequest(_httpServletRequest);
							setHttpServletResponse(_httpServletResponse);
							setLocale(defaultLocale);
							setPortletNamespace(StringPool.BLANK);
						}
					});

			_populateDDMFormFieldSettingsContext(
				ddmForm.getDDMFormFieldsMap(true), ddmFormTemplateContext,
				defaultLocale);

			ddmFormTemplateContext.put("rules", _getDataRulesJSONArray());

			JSONFactory jsonFactory = _jsonFactorySnapshot.get();

			return jsonFactory.createJSONObject(
				jsonFactory.looseSerializeDeep(ddmFormTemplateContext));
		}

		private Map<String, Object> _createDDMFormFieldSettingContext(
				DDMFormField ddmFormField, Locale defaultLocale)
			throws Exception {

			DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry =
				_ddmFormFieldTypeServicesRegistrySnapshot.get();

			DDMFormFieldType ddmFormFieldType =
				ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
					ddmFormField.getType());

			DDMForm ddmForm = DDMFormFactory.create(
				ddmFormFieldType.getDDMFormFieldTypeSettings());

			DDMFormLayout ddmFormLayout = DDMFormLayoutFactory.create(
				ddmFormFieldType.getDDMFormFieldTypeSettings());

			_removeDisabledProperties(ddmForm, ddmFormLayout);

			DDMFormTemplateContextFactory ddmFormTemplateContextFactory =
				_ddmFormTemplateContextFactorySnapshot.get();

			return ddmFormTemplateContextFactory.create(
				ddmForm, ddmFormLayout,
				new DDMFormRenderingContext() {
					{
						setContainerId("settings");
						setDDMFormValues(
							_createDDMFormFieldSettingContextDDMFormValues(
								ddmFormField, ddmForm));
						setHttpServletRequest(_httpServletRequest);
						setHttpServletResponse(_httpServletResponse);
						setLocale(defaultLocale);
						setPortletNamespace(StringPool.BLANK);
					}
				});
		}

		private DDMFormValues _createDDMFormFieldSettingContextDDMFormValues(
				DDMFormField ddmFormField,
				DDMForm ddmFormFieldTypeSettingsDDMForm)
			throws Exception {

			DDMFormValues ddmFormValues = new DDMFormValues(
				ddmFormFieldTypeSettingsDDMForm);

			DDMForm ddmForm = ddmFormField.getDDMForm();
			Map<String, Object> ddmFormFieldProperties =
				ddmFormField.getProperties();

			for (DDMFormField ddmFormFieldTypeSetting :
					ddmFormFieldTypeSettingsDDMForm.getDDMFormFields()) {

				ddmFormValues.addDDMFormFieldValue(
					new DDMFormFieldValue() {
						{
							setName(ddmFormFieldTypeSetting.getName());
							setValue(
								_createDDMFormFieldValue(
									ddmForm.getAvailableLocales(),
									ddmFormFieldTypeSetting,
									ddmFormFieldProperties.get(
										ddmFormFieldTypeSetting.getName())));
						}
					});
			}

			return ddmFormValues;
		}

		private Value _createDDMFormFieldValue(
			DDMFormFieldValidation ddmFormFieldValidation) {

			if (ddmFormFieldValidation == null) {
				return new UnlocalizedValue(StringPool.BLANK);
			}

			DDMFormFieldValidationExpression ddmFormFieldValidationExpression =
				ddmFormFieldValidation.getDDMFormFieldValidationExpression();

			return new UnlocalizedValue(
				JSONUtil.put(
					"errorMessage",
					LocalizedValueUtil.toJSONObject(
						LocalizedValueUtil.toLocalizedValuesMap(
							ddmFormFieldValidation.
								getErrorMessageLocalizedValue()))
				).put(
					"expression",
					JSONUtil.put(
						"name", ddmFormFieldValidationExpression.getName()
					).put(
						"value", ddmFormFieldValidationExpression.getValue()
					)
				).put(
					"parameter",
					LocalizedValueUtil.toJSONObject(
						LocalizedValueUtil.toLocalizedValuesMap(
							ddmFormFieldValidation.
								getParameterLocalizedValue()))
				).toString());
		}

		private Value _createDDMFormFieldValue(
				Set<Locale> availableLocales,
				DDMFormField ddmFormFieldTypeSetting, Object propertyValue)
			throws Exception {

			if (ddmFormFieldTypeSetting.isLocalizable()) {
				return (LocalizedValue)propertyValue;
			}

			if (Objects.equals(
					ddmFormFieldTypeSetting.getDataType(), "ddm-options")) {

				if (propertyValue == null) {
					propertyValue = new DDMFormFieldOptions();
				}

				return _createDDMFormFieldValue(
					availableLocales, (DDMFormFieldOptions)propertyValue);
			}

			if (Objects.equals(
					ddmFormFieldTypeSetting.getName(), "requiredDescription") &&
				(propertyValue == null)) {

				return new UnlocalizedValue(Boolean.TRUE.toString());
			}

			if (Objects.equals(
					ddmFormFieldTypeSetting.getType(), "validation")) {

				return _createDDMFormFieldValue(
					(DDMFormFieldValidation)propertyValue);
			}

			return new UnlocalizedValue(String.valueOf(propertyValue));
		}

		private Value _createDDMFormFieldValue(
				Set<Locale> availableLocales,
				DDMFormFieldOptions ddmFormFieldOptions)
			throws Exception {

			JSONFactory jsonFactory = _jsonFactorySnapshot.get();

			JSONObject jsonObject = jsonFactory.createJSONObject();

			for (Locale availableLocale : availableLocales) {
				jsonObject.put(
					LocaleUtil.toLanguageId(availableLocale),
					JSONUtil.toJSONArray(
						ddmFormFieldOptions.getOptionsValues(),
						optionValue -> {
							LocalizedValue localizedValue =
								ddmFormFieldOptions.getOptionLabels(
									optionValue);

							return JSONUtil.put(
								"label",
								localizedValue.getString(availableLocale)
							).put(
								"reference",
								ddmFormFieldOptions.getOptionReference(
									optionValue)
							).put(
								"value", optionValue
							);
						}));
			}

			return new UnlocalizedValue(jsonObject.toString());
		}

		private DDMFormLayout _deserializeDDMFormLayout(String content) {
			DDMFormLayoutDeserializerDeserializeRequest.Builder builder =
				DDMFormLayoutDeserializerDeserializeRequest.Builder.newBuilder(
					content);

			DDMFormLayoutDeserializer jsonDDMFormLayoutDeserializer =
				_jsonDDMFormLayoutDeserializerSnapshot.get();

			DDMFormLayoutDeserializerDeserializeResponse
				ddmFormLayoutDeserializerDeserializeResponse =
					jsonDDMFormLayoutDeserializer.deserialize(builder.build());

			return ddmFormLayoutDeserializerDeserializeResponse.
				getDDMFormLayout();
		}

		private JSONArray _getDataRulesJSONArray() {
			JSONFactory jsonFactory = _jsonFactorySnapshot.get();

			JSONArray dataRulesJSONArray = jsonFactory.createJSONArray();

			for (DataRule dataRule : _dataLayout.getDataRules()) {
				JSONObject dataRuleJSONObject = jsonFactory.createJSONObject();

				JSONArray jsonArray = jsonFactory.createJSONArray();

				for (Map<String, Object> action : dataRule.getActions()) {
					JSONObject jsonObject = jsonFactory.createJSONObject();

					action.forEach(jsonObject::put);

					jsonArray.put(jsonObject);
				}

				dataRuleJSONObject.put("actions", jsonArray);

				jsonArray = jsonFactory.createJSONArray();

				for (Map<String, Object> condition : dataRule.getConditions()) {
					JSONObject jsonObject = jsonFactory.createJSONObject();

					condition.forEach(jsonObject::put);

					jsonArray.put(jsonObject);
				}

				dataRuleJSONObject.put(
					"conditions", jsonArray
				).put(
					"logicalOperator", dataRule.getLogicalOperator()
				).put(
					"name", LocalizedValueUtil.toJSONObject(dataRule.getName())
				);

				dataRulesJSONArray.put(dataRuleJSONObject);
			}

			return dataRulesJSONArray;
		}

		private DDMForm _getDDMForm() throws Exception {
			DDMStructureLocalService ddmStructureLocalService =
				_ddmStructureLocalServiceSnapshot.get();

			DDMStructure ddmStructure = ddmStructureLocalService.getStructure(
				_dataLayout.getDataDefinitionId());

			String dataDefinitionJSON = ddmStructure.getDefinition();

			JSONFactory jsonFactory = _jsonFactorySnapshot.get();

			JSONObject jsonObject = jsonFactory.createJSONObject(
				StringUtil.replace(
					dataDefinitionJSON, "defaultValue", "predefinedValue"));

			ddmStructure.setDefinition(
				jsonObject.put(
					"availableLanguageIds",
					JSONUtil.toJSONArray(
						_availableLocales,
						availableLocale -> {
							Language language = _languageSnapshot.get();

							return language.getLanguageId(availableLocale);
						})
				).put(
					"defaultLanguageId", ddmStructure.getDefaultLanguageId()
				).toString());

			return ddmStructure.getDDMForm();
		}

		private DDMFormLayout _getDDMFormLayout() throws Exception {
			String definition = null;

			if (_dataLayout.getId() == null) {
				definition = _dataLayout.toString();
			}
			else {
				DDMStructureLayoutLocalService ddmStructureLayoutLocalService =
					_ddmStructureLayoutLocalServiceSnapshot.get();

				DDMStructureLayout ddmStructureLayout =
					ddmStructureLayoutLocalService.getStructureLayout(
						_dataLayout.getId());

				definition = ddmStructureLayout.getDefinition();
			}

			JSONFactory jsonFactory = _jsonFactorySnapshot.get();

			JSONObject jsonObject = jsonFactory.createJSONObject(
				StringUtil.replace(
					definition,
					new String[] {
						"columnSize", "dataLayoutColumns", "dataLayoutPages",
						"dataLayoutRows"
					},
					new String[] {"size", "columns", "pages", "rows"}));

			return _deserializeDDMFormLayout(jsonObject.toString());
		}

		private List<Map<String, Object>> _getNestedFields(
			Map<String, Object> field) {

			List<Map<String, Object>> nestedFields = new ArrayList<>();

			List<Map<String, Object>> fieldNestedFields =
				(List<Map<String, Object>>)field.get("nestedFields");

			if (fieldNestedFields == null) {
				return nestedFields;
			}

			for (Map<String, Object> nestedField : fieldNestedFields) {
				nestedFields.add(nestedField);

				nestedFields.addAll(_getNestedFields(nestedField));
			}

			return nestedFields;
		}

		private boolean _isFieldSet(Map<String, Object> field) {
			if (Objects.equals(field.get("type"), "fieldset")) {
				return true;
			}

			return false;
		}

		private void _populateDDMFormFieldSettingsContext(
				Map<String, DDMFormField> ddmFormFieldsMap,
				Map<String, Object> ddmFormTemplateContext,
				Locale defaultLocale)
			throws Exception {

			UnsafeConsumer<Map<String, Object>, Exception> unsafeConsumer =
				field -> {
					DDMFormField ddmFormField = ddmFormFieldsMap.get(
						MapUtil.getString(field, "fieldName"));

					if (_isFieldSet(field)) {
						ddmFormField.setProperty("rows", field.get("rows"));
					}

					field.put(
						"settingsContext",
						_createDDMFormFieldSettingContext(
							ddmFormField, defaultLocale));
				};

			List<Map<String, Object>> pages =
				(List<Map<String, Object>>)ddmFormTemplateContext.get("pages");

			for (Map<String, Object> page : pages) {
				List<Map<String, Object>> rows =
					(List<Map<String, Object>>)page.get("rows");

				for (Map<String, Object> row : rows) {
					List<Map<String, Object>> columns =
						(List<Map<String, Object>>)row.get("columns");

					for (Map<String, Object> column : columns) {
						List<Map<String, Object>> fields =
							(List<Map<String, Object>>)column.get("fields");

						for (Map<String, Object> field : fields) {
							unsafeConsumer.accept(field);

							List<Map<String, Object>> nestedFields =
								_getNestedFields(field);

							for (Map<String, Object> nestedField :
									nestedFields) {

								unsafeConsumer.accept(nestedField);
							}
						}
					}
				}
			}
		}

		private void _removeDisabledProperties(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout) {

			DataLayoutBuilderDefinition dataLayoutBuilderDefinition =
				_getDataLayoutBuilderDefinition(_contentType);

			String[] disabledProperties =
				dataLayoutBuilderDefinition.getDisabledProperties();

			if (ArrayUtil.isEmpty(disabledProperties)) {
				return;
			}

			for (String disabledProperty : disabledProperties) {
				Map<String, DDMFormField> ddmFormFieldsMap =
					ddmForm.getDDMFormFieldsMap(true);

				List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

				ddmFormFields.remove(ddmFormFieldsMap.get(disabledProperty));

				for (DDMFormLayoutPage ddmFormLayoutPage :
						ddmFormLayout.getDDMFormLayoutPages()) {

					for (DDMFormLayoutRow ddmFormLayoutRow :
							ddmFormLayoutPage.getDDMFormLayoutRows()) {

						for (DDMFormLayoutColumn ddmFormLayoutColumn :
								ddmFormLayoutRow.getDDMFormLayoutColumns()) {

							List<String> ddmFormFieldNames =
								ddmFormLayoutColumn.getDDMFormFieldNames();

							ddmFormFieldNames.remove(disabledProperty);
						}
					}
				}
			}
		}

		private final Set<Locale> _availableLocales;
		private final String _contentType;
		private final DataLayout _dataLayout;
		private final HttpServletRequest _httpServletRequest;
		private final HttpServletResponse _httpServletResponse;

	}

}