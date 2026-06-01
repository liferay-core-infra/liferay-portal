/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {productionReadinessPagesTest} from './fixtures/productionReadinessPagesTest';

const TARGET_RULE_KEY = 'analyzer-plugins';

export const test = mergeTests(productionReadinessPagesTest, loginTest());

test('LPD-87225 - Production Readiness dashboard renders summary and tables and passes a11y review.', async ({
	productionReadinessPage,
}) => {
	await productionReadinessPage.goto();

	for (const label of ['Passed', 'Failed', 'Ignored'] as const) {
		await expect(productionReadinessPage.summaryCard(label)).toBeVisible();

		expect(
			await productionReadinessPage.summaryCount(label)
		).toBeGreaterThanOrEqual(0);
	}

	expect(
		await productionReadinessPage.categoryPanelButton().count()
	).toBeGreaterThan(0);

	for (const label of ['Passed', 'Failed', 'Show Ignored'] as const) {
		await expect(productionReadinessPage.filterButton(label)).toBeVisible();
	}

	await checkAccessibility({page: productionReadinessPage.page});
});

test('LPD-87225 - Ignoring a rule updates the summary counts and respects the Show Ignored filter.', async ({
	productionReadinessPage,
}) => {
	await productionReadinessPage.goto();

	await productionReadinessPage.setFilter('Show Ignored', true);

	await productionReadinessPage.ensureCategoryExpanded(TARGET_RULE_KEY);

	const toggle = productionReadinessPage.ruleToggle(TARGET_RULE_KEY);

	const initiallyOn =
		(await toggle.getAttribute('aria-checked')) === 'true' ||
		(await toggle.isChecked().catch(() => false));

	if (initiallyOn) {
		await productionReadinessPage.toggleIgnore(TARGET_RULE_KEY);
	}

	const passedBaseline = await productionReadinessPage.summaryCount('Passed');
	const failedBaseline = await productionReadinessPage.summaryCount('Failed');
	const ignoredBaseline =
		await productionReadinessPage.summaryCount('Ignored');

	await productionReadinessPage.toggleIgnore(TARGET_RULE_KEY);

	await expect(
		productionReadinessPage.ruleToggle(TARGET_RULE_KEY)
	).toBeChecked();

	expect(await productionReadinessPage.summaryCount('Passed')).toBe(
		passedBaseline
	);
	expect(await productionReadinessPage.summaryCount('Failed')).toBe(
		failedBaseline - 1
	);
	expect(await productionReadinessPage.summaryCount('Ignored')).toBe(
		ignoredBaseline + 1
	);

	await productionReadinessPage.setFilter('Show Ignored', false);

	await expect(productionReadinessPage.ruleRow(TARGET_RULE_KEY)).toHaveCount(
		0
	);

	await productionReadinessPage.setFilter('Show Ignored', true);

	await expect(
		productionReadinessPage.ruleRow(TARGET_RULE_KEY)
	).toBeVisible();

	await productionReadinessPage.toggleIgnore(TARGET_RULE_KEY);

	await expect(
		productionReadinessPage.ruleToggle(TARGET_RULE_KEY)
	).not.toBeChecked();

	expect(await productionReadinessPage.summaryCount('Passed')).toBe(
		passedBaseline
	);
	expect(await productionReadinessPage.summaryCount('Failed')).toBe(
		failedBaseline
	);
	expect(await productionReadinessPage.summaryCount('Ignored')).toBe(
		ignoredBaseline
	);
});
