/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {ProductionReadinessDashboard} from '../src/main/resources/META-INF/resources/js/production_readiness';

const BASE_URL = '/o/portal/x';

const SAMPLE_PAYLOAD = {
	results: [
		{
			category: 'security',
			currentValue: 'false',
			docsLink: 'https://example.com/security',
			ignored: false,
			message: 'Security is not enabled',
			recommendedValue: 'true',
			ruleKey: 'security-enabled',
			severity: 'HIGH',
			status: 'FAIL',
		},
		{
			category: 'security',
			currentValue: 'AES',
			docsLink: 'https://example.com/encryption',
			ignored: false,
			message: 'Password encryption is enabled',
			recommendedValue: 'AES',
			ruleKey: 'password-encryption',
			severity: 'LOW',
			status: 'PASS',
		},
		{
			category: 'performance',
			currentValue: '4G',
			docsLink: 'https://example.com/heap',
			ignoreReason: 'tuned for staging',
			ignored: true,
			ignoredAt: '2026-05-01T00:00:00Z',
			ignoredBy: 'Lily Chi',
			message: 'Heap size differs from recommendation',
			recommendedValue: '8G',
			ruleKey: 'heap-size-upper-limit',
			severity: 'MEDIUM',
			status: 'FAIL',
		},
	],
	summary: {
		failed: 1,
		ignored: 1,
		passed: 1,
	},
};

jest.mock('frontend-js-web', () => ({
	createResourceURL: jest.fn(
		(baseURL: string, params: Record<string, string>) => ({
			toString: () =>
				`${baseURL}?${new URLSearchParams(params).toString()}`,
		})
	),
	fetch: jest.fn(),
}));

const {fetch: mockFetch} = jest.requireMock('frontend-js-web') as {
	fetch: jest.Mock;
};

function mockFetchOnce(payload: unknown) {
	mockFetch.mockResolvedValueOnce({
		json: () => Promise.resolve(payload),
		ok: true,
	});
}

describe('ProductionReadinessDashboard', () => {
	beforeEach(() => {
		mockFetch.mockReset();
	});

	it('renders summary counts and rules grouped by category', async () => {
		mockFetchOnce(SAMPLE_PAYLOAD);

		render(
			<ProductionReadinessDashboard
				baseResourceURL={BASE_URL}
				companyId={42}
			/>
		);

		await waitFor(() =>
			expect(
				screen.getByText('production-readiness-rule-security-enabled')
			).toBeInTheDocument()
		);

		expect(
			screen.getByText('1', {selector: '.h2.text-success'})
		).toBeInTheDocument();
		expect(
			screen.getByText('1', {selector: '.h2.text-danger'})
		).toBeInTheDocument();
		expect(
			screen.getByText('1', {selector: '.h2.text-secondary'})
		).toBeInTheDocument();

		expect(
			screen.getByText('production-readiness-category-security (1)')
		).toBeInTheDocument();
		expect(
			screen.getByText('production-readiness-category-performance', {
				selector: '.panel-title',
			})
		).toBeInTheDocument();
		expect(
			screen.getByText('production-readiness-rule-password-encryption')
		).toBeInTheDocument();
		expect(
			screen.getByText('production-readiness-rule-heap-size-upper-limit')
		).toBeInTheDocument();
	});

	it('renders an empty state when no rules are deployed', async () => {
		mockFetchOnce({
			results: [],
			summary: {failed: 0, ignored: 0, passed: 0},
		});

		render(
			<ProductionReadinessDashboard
				baseResourceURL={BASE_URL}
				companyId={42}
			/>
		);

		await waitFor(() =>
			expect(
				screen.getByText('no-production-readiness-rules-are-deployed')
			).toBeInTheDocument()
		);
	});

	it('toggling ignore calls the ignore endpoint and refetches', async () => {
		mockFetchOnce(SAMPLE_PAYLOAD);
		mockFetchOnce({ruleKey: 'security-enabled'});
		mockFetchOnce({
			...SAMPLE_PAYLOAD,
			results: SAMPLE_PAYLOAD.results.map((result) =>
				result.ruleKey === 'security-enabled'
					? {...result, ignored: true}
					: result
			),
			summary: {failed: 1, ignored: 2, passed: 1},
		});

		render(
			<ProductionReadinessDashboard
				baseResourceURL={BASE_URL}
				companyId={42}
			/>
		);

		await waitFor(() =>
			expect(
				screen.getByText('production-readiness-rule-security-enabled')
			).toBeInTheDocument()
		);

		const row = screen
			.getByText('production-readiness-rule-security-enabled')
			.closest('tr')!;

		const toggle = within(row).getByRole('switch');

		await userEvent.click(toggle);

		await waitFor(() => {
			expect(mockFetch).toHaveBeenCalledTimes(3);
		});

		const ignoreCall = mockFetch.mock.calls[1];
		const requestURL = ignoreCall[0] as string;

		expect(requestURL).toContain('ignore_production_readiness_rule');
		expect(ignoreCall[1]).toMatchObject({method: 'POST'});
	});
});
