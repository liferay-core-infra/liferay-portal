create unique index IX_8C9BC796 on TemplateEntry (ctCollectionId, ddmTemplateId);
create unique index IX_D36C36F3 on TemplateEntry (groupId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_D011CDAB on TemplateEntry (groupId, infoItemClassName[$COLUMN_LENGTH:75$], infoItemFormVariationKey[$COLUMN_LENGTH:75$]);
create unique index IX_F7DCA9E6 on TemplateEntry (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_3AF3BA36 on TemplateEntry (uuid_[$COLUMN_LENGTH:75$]);