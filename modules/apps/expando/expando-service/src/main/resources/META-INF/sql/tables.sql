create table ExpandoColumn (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	columnId LONG not null,
	companyId LONG,
	modifiedDate DATE null,
	tableId LONG,
	name VARCHAR(75) null,
	type_ INTEGER,
	defaultData TEXT null,
	typeSettings TEXT null,
	primary key (columnId, ctCollectionId)
);

create table ExpandoRow (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	rowId_ LONG not null,
	companyId LONG,
	modifiedDate DATE null,
	tableId LONG,
	classPK LONG,
	primary key (rowId_, ctCollectionId)
);

create table ExpandoTable (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	tableId LONG not null,
	companyId LONG,
	classNameId LONG,
	name VARCHAR(75) null,
	primary key (tableId, ctCollectionId)
);

create table ExpandoValue (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	valueId LONG not null,
	companyId LONG,
	tableId LONG,
	columnId LONG,
	rowId_ LONG,
	classNameId LONG,
	classPK LONG,
	data_ TEXT null,
	primary key (valueId, ctCollectionId)
);