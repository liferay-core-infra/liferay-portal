create index IX_8B26D246 on ExpandoColumn (tableId, ctCollectionId);
create unique index IX_4A7D3605 on ExpandoColumn (tableId, name[$COLUMN_LENGTH:75$], ctCollectionId);

create index IX_BCC0D776 on ExpandoRow (classPK, ctCollectionId);
create unique index IX_488E0C53 on ExpandoRow (tableId, classPK, ctCollectionId);
create index IX_5C47920C on ExpandoRow (tableId, ctCollectionId);

create index IX_A905B6E3 on ExpandoTable (companyId, classNameId, ctCollectionId);
create unique index IX_87D370E2 on ExpandoTable (companyId, classNameId, name[$COLUMN_LENGTH:75$], ctCollectionId);

create index IX_FF8FB775 on ExpandoValue (classNameId, classPK, ctCollectionId);
create index IX_B5A9F1E5 on ExpandoValue (columnId, ctCollectionId);
create unique index IX_E6D98E43 on ExpandoValue (columnId, rowId_, ctCollectionId);
create index IX_FC7A3DFE on ExpandoValue (rowId_, ctCollectionId);
create index IX_3D37FDAA on ExpandoValue (tableId, classPK, ctCollectionId);
create unique index IX_D8C72C45 on ExpandoValue (tableId, columnId, classPK, ctCollectionId);
create index IX_8AF759DA on ExpandoValue (tableId, columnId, ctCollectionId);
create index IX_EEA372D5 on ExpandoValue (tableId, ctCollectionId);
create index IX_4E7B1F33 on ExpandoValue (tableId, rowId_, ctCollectionId);