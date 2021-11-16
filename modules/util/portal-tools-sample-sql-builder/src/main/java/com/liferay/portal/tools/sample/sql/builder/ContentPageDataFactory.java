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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.fragment.model.FragmentEntryLinkModel;
import com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureModel;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelModel;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.model.impl.LayoutModelImpl;
import com.liferay.portal.service.impl.LayoutLocalServiceImpl;
import com.liferay.util.SimpleCounter;

import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class ContentPageDataFactory extends BaseDataFactory {

	public static ContentPageDataFactory getInstance() {
		return _contentPageDataFactory;
	}

	public List<LayoutModel> newContentPageLayoutModels(
		long groupId, String name) {

		List<LayoutModel> layoutModels = new ArrayList<>();

		LayoutModel publicLayoutModel = _newContentPageLayoutModel(
			groupId, name, 0, 0);

		layoutModels.add(publicLayoutModel);
		layoutModels.add(
			_newContentPageLayoutModel(
				groupId, name + "1", getClassNameId(Layout.class),
				publicLayoutModel.getPlid()));

		return layoutModels;
	}

	public List<FragmentEntryLinkModel> newFragmentEntryLinkModels(
			List<LayoutModel> layoutModels)
		throws Exception {

		List<FragmentEntryLinkModel> fragmentEntryLinkModels =
			new ArrayList<>();

		String headingRenderNamespace = StringUtil.randomId();
		String imageRenderNamespace = StringUtil.randomId();
		String paragraphRenderNamespace = StringUtil.randomId();

		for (LayoutModel layoutModel : layoutModels) {
			fragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModel, _HEADING_RENDER_KEY,
					readFile(
						_getFragmentComponentInputStream("heading", "css")),
					readFile(
						_getFragmentComponentInputStream("heading", "html")),
					readFile("heading_configuration.json"),
					readFile("heading_editValue.json"), 0,
					headingRenderNamespace));

			fragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModel, _PARAGRAPH_RENDER_KEY,
					readFile(
						_getFragmentComponentInputStream("paragraph", "css")),
					readFile(
						_getFragmentComponentInputStream("paragraph", "html")),
					readFile("paragraph_configuration.json"),
					_replaceReleaseInfo(readFile("paragraph_editValue.json")),
					0, paragraphRenderNamespace));

			fragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModel, _IMAGE_RENDER_KEY, "",
					readFile(_getFragmentComponentInputStream("image", "html")),
					readFile("image_configuration.json"),
					readFile("image_editValue.json"), 0, imageRenderNamespace));
		}

		return fragmentEntryLinkModels;
	}

	public LayoutPageTemplateStructureRelModel
		newLayoutPageTemplateStructureRelModel(
			LayoutModel layoutModel,
			LayoutPageTemplateStructureModel layoutPageTemplateStructureModel,
			List<FragmentEntryLinkModel> fragmentEntryLinkModels,
			String templateFileName) {

		List<FragmentEntryLinkModel> targetFragmentEntryLinkModels =
			new ArrayList<>();

		for (FragmentEntryLinkModel model : fragmentEntryLinkModels) {
			if (model.getPlid() == layoutModel.getPlid()) {
				targetFragmentEntryLinkModels.add(model);
			}
		}

		LayoutPageTemplateStructureRelModel
			layoutPageTemplateStructureRelModel =
				new LayoutPageTemplateStructureRelModelImpl();

		// UUID

		layoutPageTemplateStructureRelModel.setUuid(SequentialUUID.generate());

		// PK fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureRelId(
			counter.get());

		// Group instance

		layoutPageTemplateStructureRelModel.setGroupId(
			layoutPageTemplateStructureModel.getGroupId());

		// Audit fields

		layoutPageTemplateStructureRelModel.setCompanyId(COMPANY_ID);
		layoutPageTemplateStructureRelModel.setUserId(SAMPLE_USER_ID);
		layoutPageTemplateStructureRelModel.setUserName(SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setCreateDate(new Date());
		layoutPageTemplateStructureRelModel.setModifiedDate(new Date());

		// Other fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureId(
			layoutPageTemplateStructureModel.
				getLayoutPageTemplateStructureId());
		layoutPageTemplateStructureRelModel.setSegmentsExperienceId(0L);

		layoutPageTemplateStructureRelModel.setData(
			_generateJsonData(targetFragmentEntryLinkModels, templateFileName));

		return layoutPageTemplateStructureRelModel;
	}

	protected FragmentEntryLinkModel newFragmentEntryLinkModel(
		LayoutModel layoutModel, String renderKey, String css, String html,
		String configuration, String editValue, int position,
		String nameSpace) {

		FragmentEntryLinkModel fragmentEntryLinkModel =
			new FragmentEntryLinkModelImpl();

		// UUID

		fragmentEntryLinkModel.setUuid(SequentialUUID.generate());

		// PK fields

		fragmentEntryLinkModel.setFragmentEntryLinkId(counter.get());

		// Group instance

		fragmentEntryLinkModel.setGroupId(layoutModel.getGroupId());

		// Audit fields

		fragmentEntryLinkModel.setCompanyId(COMPANY_ID);
		fragmentEntryLinkModel.setUserId(SAMPLE_USER_ID);
		fragmentEntryLinkModel.setUserName(SAMPLE_USER_NAME);
		fragmentEntryLinkModel.setCreateDate(new Date());
		fragmentEntryLinkModel.setModifiedDate(new Date());

		// Other fields

		fragmentEntryLinkModel.setFragmentEntryId(0);
		fragmentEntryLinkModel.setClassNameId(getClassNameId(Layout.class));
		fragmentEntryLinkModel.setClassPK(layoutModel.getPlid());
		fragmentEntryLinkModel.setPlid(layoutModel.getPlid());
		fragmentEntryLinkModel.setRendererKey(renderKey);
		fragmentEntryLinkModel.setConfiguration(configuration);
		fragmentEntryLinkModel.setCss(css);
		fragmentEntryLinkModel.setHtml(html);
		fragmentEntryLinkModel.setConfiguration(configuration);
		fragmentEntryLinkModel.setEditableValues(editValue);
		fragmentEntryLinkModel.setNamespace(nameSpace);
		fragmentEntryLinkModel.setPosition(position);

		return fragmentEntryLinkModel;
	}

	private ContentPageDataFactory() {
	}

	private String _generateJsonData(
		List<FragmentEntryLinkModel> fragmentEntryLinkModels,
		String templateFileName) {

		String data = null;

		try {
			data = readFile(templateFileName);

			for (FragmentEntryLinkModel fragmentEntryLinkModel :
					fragmentEntryLinkModels) {

				String rendererKey = fragmentEntryLinkModel.getRendererKey();

				if (rendererKey.equals(_HEADING_RENDER_KEY)) {
					data = StringUtil.replace(
						data, "${headingFragmentEntryLinkId}",
						String.valueOf(
							fragmentEntryLinkModel.getFragmentEntryLinkId()));
				}
				else if (rendererKey.equals(_PARAGRAPH_RENDER_KEY)) {
					data = StringUtil.replace(
						data, "${paragraphFragmentEntryLinkId}",
						String.valueOf(
							fragmentEntryLinkModel.getFragmentEntryLinkId()));
				}
				else {
					data = StringUtil.replace(
						data, "${imageFragmentEntryLinkId}",
						String.valueOf(
							fragmentEntryLinkModel.getFragmentEntryLinkId()));
				}
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		return data;
	}

	private InputStream _getFragmentComponentInputStream(
			String fragmentName, String suffix)
		throws Exception {

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		URL url = classLoader.getResource(
			StringBundler.concat(
				"com/liferay/fragment/collection/contributor/basic/component",
				"/dependencies/", fragmentName, "/index.", suffix));

		return url.openStream();
	}

	private LayoutModel _newContentPageLayoutModel(
		long groupId, String name, long classNameId, long classPK) {

		SimpleCounter simpleCounter = layoutIdCounters.computeIfAbsent(
			LayoutLocalServiceImpl.getCounterName(groupId, false),
			counterName -> new SimpleCounter());

		LayoutModel layoutModel = new LayoutModelImpl();

		// UUID

		layoutModel.setUuid(SequentialUUID.generate());

		// PK fields

		layoutModel.setPlid(layoutPlidCounter.get());

		// Group instance

		layoutModel.setGroupId(groupId);

		// Audit fields

		layoutModel.setCompanyId(COMPANY_ID);
		layoutModel.setUserId(SAMPLE_USER_ID);
		layoutModel.setUserName(SAMPLE_USER_NAME);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());

		// Other fields

		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_CONTENT);
		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + name);
		layoutModel.setClassNameId(classNameId);
		layoutModel.setClassPK(classPK);

		if (classNameId != 0) {
			layoutModel.setHidden(true);
			layoutModel.setSystem(true);
		}

		UnicodeProperties typeSettingsUnicodeProperties = new UnicodeProperties(
			true);

		typeSettingsUnicodeProperties.setProperty("published", "true");

		layoutModel.setTypeSettings(
			StringUtil.replace(
				typeSettingsUnicodeProperties.toString(), '\n', "\\n"));

		layoutModel.setLastPublishDate(new Date());

		return layoutModel;
	}

	private String _replaceReleaseInfo(String resource) throws Exception {
		StringBundler sb = new StringBundler(3);

		sb.append("Welcome to");
		sb.append(ReleaseInfo.getReleaseInfo());
		sb.append(StringPool.PERIOD);

		return StringUtil.replace(resource, "${paragraphValue}", sb.toString());
	}

	private static final String _HEADING_RENDER_KEY = "BASIC_COMPONENT-heading";

	private static final String _IMAGE_RENDER_KEY = "BASIC_COMPONENT-image";

	private static final String _PARAGRAPH_RENDER_KEY =
		"BASIC_COMPONENT-paragraph";

	private static final ContentPageDataFactory _contentPageDataFactory =
		new ContentPageDataFactory();

}