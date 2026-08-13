/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttributeTable;
import com.liferay.dynamic.data.mapping.model.DDMFieldTable;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.Portlet;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Reproduces the customer scenario behind LPD-94079: a web content article
 * backed by a DDM structure heavy enough to populate thousands of DDMField and
 * DDMFieldAttribute rows per version, added once, updated five times (six
 * versions total), then removed through the same MVC action command the web
 * content UI invokes ({@code /journal/delete_articles_and_folders}, a permanent
 * all-versions delete).
 *
 * <p>
 * The volume mirrors the customer data: one repeatable fieldset repeated many
 * times so that each version stores 5,600+ {@code DDMField} rows and 8,500+
 * {@code DDMFieldAttribute} rows across four locales. The add, each update, and
 * the delete are timed and logged so the insert/update/delete cost can be
 * observed end-to-end.
 * </p>
 *
 * @author Brian Chan
 */
@RunWith(Arquillian.class)
public class JournalArticleAddUpdateDeletePerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddUpdateDeleteHeavyArticle() throws Exception {
		DDMForm ddmForm = _createDDMForm();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(), ddmForm,
			_DEFAULT_LOCALE);

		String content = _generateContent(ddmStructure, ddmForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		int initialDDMFieldCount = _getRowCount(DDMFieldTable.INSTANCE);
		int initialDDMFieldAttributeCount = _getRowCount(
			DDMFieldAttributeTable.INSTANCE);

		// Insert (mirrors the customer script's addArticle call; the content
		// XML is built above, outside the timer, just as the script reads its
		// XML file before timing the add)

		JournalArticle article = null;

		try (LoggingTimer loggingTimer = new LoggingTimer("Add article")) {
			article = _journalArticleLocalService.addArticle(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				HashMapBuilder.put(
					_DEFAULT_LOCALE, "Performance Test Article"
				).build(),
				HashMapBuilder.put(
					_DEFAULT_LOCALE, RandomTestUtil.randomString()
				).build(),
				content, ddmStructure.getStructureId(), null, serviceContext);
		}

		if (_log.isInfoEnabled()) {
			int ddmFieldCount = _getRowCount(
				DDMFieldTable.INSTANCE, DDMFieldTable.INSTANCE.storageId,
				article.getId());
			int ddmFieldAttributeCount = _getRowCount(
				DDMFieldAttributeTable.INSTANCE,
				DDMFieldAttributeTable.INSTANCE.storageId, article.getId());

			_log.info(
				StringBundler.concat(
					"Each version stores ", ddmFieldCount,
					" DDMField rows and ", ddmFieldAttributeCount,
					" DDMFieldAttribute rows"));
		}

		Assert.assertTrue(
			_getRowCount(DDMFieldTable.INSTANCE) >=
				(initialDDMFieldCount + _MIN_DDM_FIELD_ROWS));
		Assert.assertTrue(
			_getRowCount(DDMFieldAttributeTable.INSTANCE) >=
				(initialDDMFieldAttributeCount +
					_MIN_DDM_FIELD_ATTRIBUTE_ROWS));

		// Update

		for (int i = 1; i <= _UPDATE_COUNT; i++) {
			Map<Locale, String> titleMap = HashMapBuilder.put(
				_DEFAULT_LOCALE, "Performance Test Article (update " + i + ")"
			).build();

			try (LoggingTimer loggingTimer = new LoggingTimer(
					"Update article " + i)) {

				article = _journalArticleLocalService.updateArticle(
					TestPropsValues.getUserId(), _group.getGroupId(),
					article.getFolderId(), article.getArticleId(),
					article.getVersion(), titleMap, null, content,
					article.getLayoutUuid(), serviceContext);
			}
		}

		Assert.assertEquals(
			_UPDATE_COUNT + 1,
			_journalArticleLocalService.getArticlesCount(
				_group.getGroupId(), article.getArticleId()));

		Assert.assertTrue(
			_getRowCount(DDMFieldTable.INSTANCE) >=
				(initialDDMFieldCount +
					((_UPDATE_COUNT + 1) * _MIN_DDM_FIELD_ROWS)));
		Assert.assertTrue(
			_getRowCount(DDMFieldAttributeTable.INSTANCE) >=
				(initialDDMFieldAttributeCount +
					((_UPDATE_COUNT + 1) * _MIN_DDM_FIELD_ATTRIBUTE_ROWS)));

		// Delete first 5 versions

		List<JournalArticle> articles = _journalArticleLocalService.getArticles(
			_group.getGroupId(), article.getArticleId());

		List<String> deleteRowIds = new ArrayList<>();

		for (JournalArticle tempArticle : articles) {
			if (tempArticle.getVersion() < article.getVersion()) {
				deleteRowIds.add(
					tempArticle.getArticleId() + "_version_" +
						tempArticle.getVersion());
			}
		}

		Assert.assertEquals(5, deleteRowIds.size());

		try (LoggingTimer loggingTimer = new LoggingTimer(
				"Delete article versions 1 to 5")) {

			_deleteArticleVersions(deleteRowIds);
		}

		Assert.assertEquals(
			1,
			_journalArticleLocalService.getArticlesCount(
				_group.getGroupId(), article.getArticleId()));

		// Move remaining 1-version article to trash

		try (LoggingTimer loggingTimer = new LoggingTimer(
				"Move article to trash")) {

			_moveArticleToTrash(article.getArticleId());
		}

		JournalArticle remainingArticle =
			_journalArticleLocalService.fetchLatestArticle(
				article.getResourcePrimKey(),
				WorkflowConstants.STATUS_IN_TRASH);

		Assert.assertNotNull(remainingArticle);
		Assert.assertEquals(
			WorkflowConstants.STATUS_IN_TRASH, remainingArticle.getStatus());

		// Permanently delete article from trash

		try (LoggingTimer loggingTimer = new LoggingTimer(
				"Delete article from trash")) {

			_deleteArticleFromTrash(article);
		}

		Assert.assertNull(
			_journalArticleLocalService.fetchArticle(
				_group.getGroupId(), article.getArticleId()));

		Assert.assertEquals(
			0,
			_ddmFieldLocalService.getDDMFormValuesCount(
				ddmStructure.getStructureId()));
	}

	private DDMForm _createDDMForm() {
		Set<Locale> availableLocales = new LinkedHashSet<>();

		availableLocales.add(_DEFAULT_LOCALE);

		for (Locale locale : _OTHER_LOCALES) {
			availableLocales.add(locale);
		}

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			availableLocales, _DEFAULT_LOCALE);

		for (int i = 0; i < _SINGLETON_FIELD_COUNT; i++) {
			ddmForm.addDDMFormField(_createTextDDMFormField("field" + i));
		}

		DDMFormField fieldSetDDMFormField = new DDMFormField(
			_FIELD_SET_NAME, "fieldset");

		fieldSetDDMFormField.setLocalizable(false);
		fieldSetDDMFormField.setRepeatable(true);

		_setLabel(fieldSetDDMFormField, _FIELD_SET_NAME);

		List<DDMFormField> nestedDDMFormFields =
			fieldSetDDMFormField.getNestedDDMFormFields();

		for (int i = 0; i < _FIELD_SET_CHILD_COUNT; i++) {
			nestedDDMFormFields.add(
				_createTextDDMFormField(_FIELD_SET_NAME + "Field" + i));
		}

		ddmForm.addDDMFormField(fieldSetDDMFormField);

		return ddmForm;
	}

	private DDMFormField _createTextDDMFormField(String name) {
		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			name, true, false, false);

		_setLabel(ddmFormField, name);

		return ddmFormField;
	}

	private DDMFormFieldValue _createTextDDMFormFieldValue(
		String name, boolean multilingual) {

		LocalizedValue localizedValue = new LocalizedValue(_DEFAULT_LOCALE);

		localizedValue.addString(
			_DEFAULT_LOCALE, RandomTestUtil.randomString());

		if (multilingual) {
			for (Locale locale : _OTHER_LOCALES) {
				localizedValue.addString(locale, RandomTestUtil.randomString());
			}
		}

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setFieldReference(name);
		ddmFormFieldValue.setName(name);
		ddmFormFieldValue.setValue(localizedValue);

		return ddmFormFieldValue;
	}

	private void _deleteArticleFromTrash(JournalArticle article)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.setParameter(
			ActionRequest.ACTION_NAME, "deleteEntries");
		mockLiferayPortletActionRequest.setParameter(
			"className", JournalArticle.class.getName());
		mockLiferayPortletActionRequest.setParameter(
			"classPK", String.valueOf(article.getResourcePrimKey()));

		_trashPortlet.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());
	}

	private void _deleteArticleVersions(List<String> deleteRowIds)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest() {

				@Override
				public HttpServletRequest getOriginalHttpServletRequest() {
					return new MockHttpServletRequest();
				}

			};

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"rowIds", deleteRowIds.toArray(new String[0]));

		_deleteArticlesMVCActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());
	}

	private void _moveArticleToTrash(String articleId) throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"rowIdsJournalArticle", articleId);

		_moveArticlesAndFoldersToTrashMVCActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());
	}

	private String _generateContent(DDMStructure ddmStructure, DDMForm ddmForm)
		throws Exception {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm, ddmForm.getAvailableLocales(), _DEFAULT_LOCALE);

		for (int i = 0; i < _SINGLETON_FIELD_COUNT; i++) {
			ddmFormValues.addDDMFormFieldValue(
				_createTextDDMFormFieldValue("field" + i, false));
		}

		for (int i = 0; i < _FIELD_SET_INSTANCE_COUNT; i++) {
			DDMFormFieldValue fieldSetDDMFormFieldValue =
				new DDMFormFieldValue();

			fieldSetDDMFormFieldValue.setFieldReference(_FIELD_SET_NAME);
			fieldSetDDMFormFieldValue.setName(_FIELD_SET_NAME);

			for (int j = 0; j < _FIELD_SET_CHILD_COUNT; j++) {
				fieldSetDDMFormFieldValue.addNestedDDMFormFieldValue(
					_createTextDDMFormFieldValue(
						_FIELD_SET_NAME + "Field" + j,
						j < _FIELD_SET_MULTILINGUAL_CHILD_COUNT));
			}

			ddmFormValues.addDDMFormFieldValue(fieldSetDDMFormFieldValue);
		}

		Fields fields = _ddmFormValuesToFieldsConverter.convert(
			ddmStructure, ddmFormValues);

		return _journalConverter.getContent(
			ddmStructure, fields, _group.getGroupId());
	}

	private int _getRowCount(Table<?> table) {
		return _ddmFieldLocalService.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				table
			));
	}

	private int _getRowCount(
		Table<?> table, Column<?, Long> storageIdColumn, long storageId) {

		return _ddmFieldLocalService.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				table
			).where(
				storageIdColumn.eq(storageId)
			));
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _setLabel(DDMFormField ddmFormField, String label) {
		LocalizedValue localizedValue = new LocalizedValue(_DEFAULT_LOCALE);

		localizedValue.addString(_DEFAULT_LOCALE, label);

		for (Locale locale : _OTHER_LOCALES) {
			localizedValue.addString(locale, label);
		}

		ddmFormField.setLabel(localizedValue);
	}

	private static final Locale _DEFAULT_LOCALE = LocaleUtil.US;

	private static final int _FIELD_SET_CHILD_COUNT = 18;

	private static final int _FIELD_SET_INSTANCE_COUNT = 320;

	private static final int _FIELD_SET_MULTILINGUAL_CHILD_COUNT = 5;

	private static final String _FIELD_SET_NAME = "repeatableFieldSet";

	private static final int _MIN_DDM_FIELD_ATTRIBUTE_ROWS = 8500;

	private static final int _MIN_DDM_FIELD_ROWS = 5600;

	private static final Locale[] _OTHER_LOCALES = {
		LocaleUtil.GERMANY, LocaleUtil.FRANCE, LocaleUtil.SPAIN
	};

	private static final int _SINGLETON_FIELD_COUNT = 30;

	private static final int _UPDATE_COUNT = 5;

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleAddUpdateDeletePerformanceTest.class);

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@Inject(filter = "mvc.command.name=/journal/delete_articles")
	private MVCActionCommand _deleteArticlesMVCActionCommand;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalConverter _journalConverter;

	@Inject(filter = "mvc.command.name=/journal/move_articles_and_folders_to_trash")
	private MVCActionCommand _moveArticlesAndFoldersToTrashMVCActionCommand;

	@Inject(filter = "jakarta.portlet.name=com_liferay_trash_web_portlet_TrashPortlet")
	private Portlet _trashPortlet;

}