/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import {fetchResults, ignoreRule, unignoreRule} from './api';
import CategoryTable from './components/CategoryTable';
import FilterToolbar from './components/FilterToolbar';
import SummaryCards from './components/SummaryCards';
import {
	FilterState,
	ProductionReadinessDashboardProps,
	ResultsPayload,
	RuleResult,
} from './types';

const DEFAULT_FILTER_STATE: FilterState = {
	showFailed: true,
	showIgnored: true,
	showPassed: true,
};

function applyFilter(
	results: RuleResult[],
	filterState: FilterState
): RuleResult[] {
	return results.filter((result) => {
		if (result.ignored) {
			return filterState.showIgnored;
		}

		return result.status === 'PASS'
			? filterState.showPassed
			: filterState.showFailed;
	});
}

function groupByCategory(
	results: RuleResult[]
): Array<{category: string; results: RuleResult[]}> {
	const byCategory = new Map<string, RuleResult[]>();

	for (const result of results) {
		const category = result.category || Liferay.Language.get('other');

		const bucket = byCategory.get(category) ?? [];

		bucket.push(result);

		byCategory.set(category, bucket);
	}

	return Array.from(byCategory.entries())
		.sort(([a], [b]) => a.localeCompare(b))
		.map(([category, categoryResults]) => ({
			category,
			results: categoryResults,
		}));
}

export function ProductionReadinessDashboard(
	props: ProductionReadinessDashboardProps
) {
	const [payload, setPayload] = useState<ResultsPayload | null>(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);
	const [filterState, setFilterState] =
		useState<FilterState>(DEFAULT_FILTER_STATE);
	const [togglingRuleKeys, setTogglingRuleKeys] = useState<Set<string>>(
		new Set()
	);

	const load = useCallback(() => {
		setLoading(true);
		setError(null);

		return fetchResults(props.baseResourceURL)
			.then((data) => {
				setPayload(data);
				setLoading(false);
			})
			.catch((fetchError) => {
				setError(String(fetchError));
				setLoading(false);
			});
	}, [props.baseResourceURL]);

	useEffect(() => {
		load();
	}, [load]);

	const onToggleIgnore = useCallback(
		async (result: RuleResult) => {
			setTogglingRuleKeys((current) => {
				const next = new Set(current);

				next.add(result.ruleKey);

				return next;
			});

			try {
				if (result.ignored) {
					await unignoreRule(props.baseResourceURL, result.ruleKey);
				}
				else {
					await ignoreRule(props.baseResourceURL, result.ruleKey, '');
				}

				await load();
			}
			catch (toggleError) {
				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'your-request-failed-to-complete'
					),
					type: 'danger',
				});
			}
			finally {
				setTogglingRuleKeys((current) => {
					const next = new Set(current);

					next.delete(result.ruleKey);

					return next;
				});
			}
		},
		[load, props.baseResourceURL]
	);

	const grouped = useMemo(() => {
		if (!payload) {
			return [];
		}

		return groupByCategory(applyFilter(payload.results, filterState));
	}, [filterState, payload]);

	if (loading) {
		return <ClayLoadingIndicator />;
	}

	if (error) {
		return (
			<ClayEmptyState
				description={error}
				title={Liferay.Language.get('an-unexpected-error-occurred')}
			/>
		);
	}

	if (!payload || !payload.results.length) {
		return (
			<ClayEmptyState
				description={Liferay.Language.get(
					'no-production-readiness-rules-are-deployed'
				)}
				title={Liferay.Language.get('no-results-were-found')}
			/>
		);
	}

	return (
		<div className="production-readiness-dashboard">
			<SummaryCards summary={payload.summary} />

			<FilterToolbar
				filterState={filterState}
				onChange={setFilterState}
				onRefresh={load}
			/>

			<div className="mt-3">
				{grouped.map(({category, results}) => (
					<CategoryTable
						category={category}
						key={category}
						onToggleIgnore={onToggleIgnore}
						results={results}
						togglingRuleKeys={togglingRuleKeys}
					/>
				))}
			</div>
		</div>
	);
}
