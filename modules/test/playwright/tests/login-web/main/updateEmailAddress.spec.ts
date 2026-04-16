/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';

export const test = mergeTests(apiHelpersTest, loginTest());

test('LPD-82642: Verify that referer parameter is sanitized to prevent Open Redirect', async ({
	apiHelpers,
	page,
}) => {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	const randomString = getRandomString().toLowerCase();
	const newEmail = `${randomString}@liferay.com`;
	const maliciousUrl = 'http://www.google.nl';

	try {
		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(
			`${liferayConfig.environment.baseUrl}/c/portal/update_email_address?referer=${maliciousUrl}`
		);

		const refererInput = page.locator('input[name$="referer"]');

		await expect(refererInput).not.toHaveValue(maliciousUrl);

		await page.getByLabel('Email Address', {exact: true}).fill(newEmail);
		await page.getByLabel('Enter Again').fill(newEmail);

		await page.getByRole('button', {name: 'Save'}).click();

		await page.waitForLoadState('networkidle');

		const finalUrl = page.url();

		expect(finalUrl).not.toContain(maliciousUrl);

		expect(finalUrl).toContain(liferayConfig.environment.baseUrl);
	}
	finally {
		await performLogout(page);

		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));

		delete userData[user.alternateName];
	}
});
