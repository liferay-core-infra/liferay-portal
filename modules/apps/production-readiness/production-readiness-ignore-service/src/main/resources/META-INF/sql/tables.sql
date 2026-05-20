create table IgnoredRule (
	mvccVersion LONG default 0 not null,
	ignoredRuleId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	ruleKey VARCHAR(75) null,
	reason VARCHAR(75) null
);