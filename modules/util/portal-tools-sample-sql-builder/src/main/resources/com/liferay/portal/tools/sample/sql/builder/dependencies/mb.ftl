<#list dataFactory.newMBCategoryModels(groupId) as mbCategoryModel>
	${dataFactory.toInsertSQL(mbCategoryModel)}
	${dataFactory.toInsertSQL(dataFactory.newMBMailingListModel(mbCategoryModel))}

	${csvFileWriter.write("mbCategory", virtualHostModel.hostname + "," + groupModel.friendlyURL + "," + mbCategoryModel.categoryId + "," + mbCategoryModel.name + "\n")}

	<#list dataFactory.newMBThreadModels(mbCategoryModel) as mbThreadModel>
		<#list dataFactory.newMBMessageModels(mbThreadModel) as mbMessageModel>
			<@insertMBMessage _mbMessageModel=mbMessageModel />

			${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(mbMessageModel))}
		</#list>

		${dataFactory.toInsertSQL(mbThreadModel)}

		${dataFactory.toInsertSQL(dataFactory.newSubscriptionModel(mbThreadModel))}

		<@insertAssetEntry _entry=mbThreadModel />

		${dataFactory.toInsertSQL(dataFactory.newMBThreadFlagModel(mbThreadModel))}

		${csvFileWriter.write("mbThread", virtualHostModel.hostname + "," + groupModel.friendlyURL + "," + mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>