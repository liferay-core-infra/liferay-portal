<#if (dataFactory.maxContentLayoutCount > 0) && (dataFactory.maxFragmentsPerLayoutCount > 0)>
	<#assign
		journalArticleResourceModel = dataFactory.newJournalArticleResourceModel(groupId)

		journalArticleModel = dataFactory.newJournalArticleModel(journalArticleResourceModel, 0, 1)
	/>

	${dataFactory.toInsertSQL(journalArticleResourceModel)}

	<@insertJournalArticle
		_insertAssetEntry = true
		_journalArticleModel = journalArticleModel
		_journalDDMStructureModel = defaultJournalDDMStructureModel
		_journalDDMTemplateModel = defaultJournalDDMTemplateModel
	/>

	<#list dataFactory.newContentLayoutModels(groupId) as contentLayoutModel>

		<#assign layoutPageTemplateStructureModel = dataFactory.newLayoutPageTemplateStructureModel(contentLayoutModel) />

		${dataFactory.toInsertSQL(layoutPageTemplateStructureModel)}

		<#assign fragmentEntryLinkModels = dataFactory.newFragmentEntryLinkModels(journalArticleModel, contentLayoutModel) />

		<#list fragmentEntryLinkModels as fragmentEntryLinkModel>
			${dataFactory.toInsertSQL(fragmentEntryLinkModel)}
		</#list>

		<#assign layoutPageTemplateStructureRelModel = dataFactory.newLayoutPageTemplateStructureRelModel(contentLayoutModel, layoutPageTemplateStructureModel, fragmentEntryLinkModels) />

		${dataFactory.toInsertSQL(layoutPageTemplateStructureRelModel)}

		${csvFileWriter.write("fragment", virtualHostModel.hostname + "," + groupModel.friendlyURL + "," + contentLayoutModel.friendlyURL + "\n")}
	</#list>
</#if>