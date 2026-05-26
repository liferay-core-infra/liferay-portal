/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {GlobalMenuPage} from '../../../../pages/product-navigation-applications-menu/GlobalMenuPage';
import {waitForPageToBeLoaded} from '../../../../utils/waitForPageToBeLoaded';

type FilterLabel = 'Passed' | 'Failed' | 'Show Ignored';
type SummaryLabel = 'Passed' | 'Failed' | 'Ignored';

const RESULTS_RESOURCE_FRAGMENT = 'get_production_readiness_results';

export class ProductionReadinessPage {
	readonly page: Page;
	readonly globalMenuPage: GlobalMenuPage;
	readonly dashboard: Locator;
	readonly summary: Locator;
	readonly tabLink: Locator;

	constructor(page: Page) {
		this.globalMenuPage = new GlobalMenuPage(page);
		this.page = page;
		this.dashboard = page.locator('.production-readiness-dashboard');
		this.summary = page.locator('.production-readiness-summary');
		this.tabLink = page.getByRole('link', {
			exact: true,
			name: 'Production Readiness',
		});
	}

	categoryPanelButton(): Locator {
		return this.dashboard.locator('button[aria-expanded]');
	}

	async goto() {
		await this.globalMenuPage.goToHome();
		await this.globalMenuPage.goToControlPanel('Server Administration');

		await Promise.all([
			this.page.waitForResponse(
				(response) =>
					response.url().includes(RESULTS_RESOURCE_FRAGMENT) &&
					response.status() === 200
			),
			this.tabLink.click(),
		]);

		await waitForPageToBeLoaded(this.page);

		await expect(this.summary).toBeVisible();
	}

	summaryCard(label: SummaryLabel): Locator {
		return this.summary.locator('.card').filter({hasText: label});
	}

	async summaryCount(label: SummaryLabel): Promise<number> {
		const text = await this.summaryCard(label).locator('.h2').innerText();

		const value = parseInt(text.trim(), 10);

		if (Number.isNaN(value)) {
			throw new Error(
				`Summary card "${label}" did not render a numeric value (got "${text}")`
			);
		}

		return value;
	}

	filterButton(label: FilterLabel): Locator {
		return this.page.getByRole('button', {exact: true, name: label});
	}

	async setFilter(label: FilterLabel, active: boolean) {
		const button = this.filterButton(label);

		const pressed = (await button.getAttribute('aria-pressed')) === 'true';

		if (pressed !== active) {
			await button.click();
		}

		await expect(button).toHaveAttribute(
			'aria-pressed',
			active ? 'true' : 'false'
		);
	}

	ruleRow(ruleKey: string): Locator {
		return this.page.locator('tr', {
			has: this.page.getByText(ruleKey, {exact: true}),
		});
	}

	ruleToggle(ruleKey: string): Locator {
		return this.ruleRow(ruleKey).getByRole('switch');
	}

	async expandAllCategoryPanels() {
		const collapsed = this.dashboard.locator(
			'button[aria-expanded="false"]'
		);

		for (let i = 0; i < 20; i++) {
			if ((await collapsed.count()) === 0) {
				return;
			}

			await collapsed.first().click();
		}
	}

	async ensureCategoryExpanded(ruleKey: string) {
		const row = this.ruleRow(ruleKey);

		if (await row.isVisible()) {
			return;
		}

		await this.expandAllCategoryPanels();

		await expect(row).toBeVisible();
	}

	async toggleIgnore(ruleKey: string) {
		await this.ensureCategoryExpanded(ruleKey);

		const toggle = this.ruleToggle(ruleKey);

		await expect(toggle).toBeEnabled();

		await Promise.all([
			this.page.waitForResponse(
				(response) =>
					response.url().includes(RESULTS_RESOURCE_FRAGMENT) &&
					response.status() === 200
			),
			toggle.click(),
		]);

		await expect(this.summary).toBeVisible();

		await this.expandAllCategoryPanels();
	}
}
