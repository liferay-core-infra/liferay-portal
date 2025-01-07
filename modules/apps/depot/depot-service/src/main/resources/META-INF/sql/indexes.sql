create unique index IX_6A3A9CFA on DepotAppCustomization (depotEntryId, ctCollectionId, enabled);
create unique index IX_47758FAE on DepotAppCustomization (depotEntryId, ctCollectionId, portletId[$COLUMN_LENGTH:75$]);

create unique index IX_E3EB2C84 on DepotEntry (groupId, ctCollectionId);

create index IX_146497CB on DepotEntryGroupRel (depotEntryId);
create index IX_7CA33F81 on DepotEntryGroupRel (toGroupId, ddmStructuresAvailable);
create unique index IX_1DD0EA9C on DepotEntryGroupRel (toGroupId, depotEntryId, ctCollectionId);
create index IX_BA106967 on DepotEntryGroupRel (toGroupId, searchable);
create unique index IX_A83D9516 on DepotEntryGroupRel (uuid_[$COLUMN_LENGTH:75$], ctCollectionId, groupId);