/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import {fetchResults, ignoreRule, unignoreRule} from './api';
import CategorySection from './components/CategorySection';
import FilterPills from './components/FilterPills';
import SummaryHeader from './components/SummaryHeader';
import {
	FilterValue,
	ProductionReadinessDashboardProps,
	ResultsPayload,
	RuleResult,
} from './types';

function applyFilter(
	results: RuleResult[],
	filterValue: FilterValue
): RuleResult[] {
	return results.filter((result) => {
		if (filterValue === 'failed') {
			return !result.ignored && result.status === 'FAIL';
		}
		else if (filterValue === 'ignored') {
			return result.ignored;
		}
		else if (filterValue === 'passed') {
			return !result.ignored && result.status === 'PASS';
		}
		else {
			return true;
		}
	});
}

function groupByCategory(results: RuleResult[]): Array<{
	category: string;
	categoryLabel: string;
	results: RuleResult[];
}> {
	const byCategory = new Map<
		string,
		{categoryLabel: string; results: RuleResult[]}
	>();

	for (const result of results) {
		const category = result.category || Liferay.Language.get('other');

		const bucket = byCategory.get(category) ?? {
			categoryLabel: result.category
				? result.categoryLabel
				: Liferay.Language.get('other'),
			results: [],
		};

		bucket.results.push(result);

		byCategory.set(category, bucket);
	}

	return Array.from(byCategory.entries()).map(
		([category, {categoryLabel, results: categoryResults}]) => ({
			category,
			categoryLabel,
			results: categoryResults,
		})
	);
}

export function ProductionReadinessDashboard(
	props: ProductionReadinessDashboardProps
) {
	const [error, setError] = useState<string | null>(null);
	const [filterValue, setFilterValue] = useState<FilterValue>('all');
	const [loading, setLoading] = useState(true);
	const [payload, setPayload] = useState<ResultsPayload | null>(null);
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
					await ignoreRule(props.baseResourceURL, result.ruleKey);
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

		const sorted = [...applyFilter(payload.results, filterValue)].sort(
			(a, b) => a.category.localeCompare(b.category)
		);

		return groupByCategory(sorted);
	}, [filterValue, payload]);

	if (loading) {
		return <ClayLoadingIndicator />;
	}

	let content;

	if (error) {
		content = (
			<ClayEmptyState
				description={error}
				title={Liferay.Language.get('an-unexpected-error-occurred')}
			/>
		);
	}
	else if (!payload || !payload.results.length) {
		content = (
			<ClayEmptyState
				description={Liferay.Language.get(
					'no-production-readiness-rules-are-deployed'
				)}
				title={Liferay.Language.get('no-results-were-found')}
			/>
		);
	}
	else {
		const {failed, ignored, passed} = payload.summary;

		content = (
			<>
				<div className="align-items-start d-flex justify-content-between mb-4">
					<SummaryHeader
						failed={failed}
						ignored={ignored}
						passed={passed}
						total={failed + ignored + passed}
					/>

					<FilterPills
						onChange={setFilterValue}
						value={filterValue}
					/>
				</div>

				<div className="production-readiness-sections">
					{grouped.map(({category, categoryLabel, results}) => (
						<CategorySection
							categoryLabel={categoryLabel}
							key={category}
							onToggleIgnore={onToggleIgnore}
							results={results}
							togglingRuleKeys={togglingRuleKeys}
						/>
					))}
				</div>
			</>
		);
	}

	return (
		<div className="production-readiness-dashboard">
			<div className="mb-3">
				<ClayLink
					aria-label={sub(
						Liferay.Language.get('x-opens-new-window'),
						Liferay.Language.get(
							'click-here-to-learn-more-about-what-settings-liferay-recommends-checking-before-a-dxp-deployment'
						)
					)}
					href="https://www.liferay.com/resources/product-info/Liferay+DXP+Go+Live+Checklist"
					rel="noopener noreferrer"
					target="_blank"
				>
					{Liferay.Language.get(
						'click-here-to-learn-more-about-what-settings-liferay-recommends-checking-before-a-dxp-deployment'
					)}

					<ClayIcon className="ml-1" symbol="shortcut" />
				</ClayLink>
			</div>

			{content}
		</div>
	);
}
