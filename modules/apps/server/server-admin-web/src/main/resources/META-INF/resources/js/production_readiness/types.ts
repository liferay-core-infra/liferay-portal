/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type RuleStatus = 'PASS' | 'FAIL';

export type RuleSeverity = 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';

export interface RuleResult {
	category: string;
	currentValue: string | null;
	docsLink: string;
	ignoreReason?: string;
	ignored: boolean;
	ignoredAt?: string;
	ignoredBy?: string;
	message: string;
	recommendedValue: string | null;
	ruleKey: string;
	severity: RuleSeverity;
	status: RuleStatus;
}

export interface Summary {
	failed: number;
	ignored: number;
	passed: number;
}

export interface ResultsPayload {
	results: RuleResult[];
	summary: Summary;
}

export interface ProductionReadinessDashboardProps {
	baseResourceURL: string;
	companyId: number;
}

export interface FilterState {
	showFailed: boolean;
	showIgnored: boolean;
	showPassed: boolean;
}
