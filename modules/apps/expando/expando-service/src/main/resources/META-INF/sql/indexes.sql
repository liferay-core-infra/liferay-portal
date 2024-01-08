create unique index IX_4A7D3605 on ExpandoColumn (tableId, name[$COLUMN_LENGTH:75$], ctCollectionId);

create unique index IX_F1A1F8BF on ExpandoRow (classPK, tableId, ctCollectionId);

create unique index IX_87D370E2 on ExpandoTable (companyId, classNameId, name[$COLUMN_LENGTH:75$], ctCollectionId);

create index IX_CAD04B0D on ExpandoValue (classPK, classNameId);
create unique index IX_F7AD05C3 on ExpandoValue (rowId_, columnId, ctCollectionId);
create unique index IX_DB301E6F on ExpandoValue (tableId, classPK, columnId, ctCollectionId);
create index IX_B71E92D5 on ExpandoValue (tableId, rowId_);