/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPanel from '@clayui/panel';
import React from 'react';

import {RuleResult} from '../types';
import IgnoreToggle from './IgnoreToggle';
import StatusLabel from './StatusLabel';

interface Props {
	category: string;
	onToggleIgnore: (result: RuleResult) => void;
	results: RuleResult[];
	togglingRuleKeys: Set<string>;
}

const CategoryTable: React.FC<Props> = ({
	category,
	onToggleIgnore,
	results,
	togglingRuleKeys,
}) => {
	if (!results.length) {
		return null;
	}

	const failedCount = results.filter(
		(result) => result.status === 'FAIL' && !result.ignored
	).length;

	const categoryLabel = Liferay.Language.get(
		'production-readiness-category-' + category
	);

	const title = failedCount
		? `${categoryLabel} (${failedCount})`
		: categoryLabel;

	return (
		<ClayPanel
			collapsable
			displayTitle={title}
			displayType="unstyled"
			showCollapseIcon
		>
			<ClayPanel.Body>
				<table className="table table-autofit table-list">
					<caption className="sr-only">{categoryLabel}</caption>

					<thead>
						<tr>
							<th scope="col">{Liferay.Language.get('rule')}</th>

							<th scope="col">
								{Liferay.Language.get('current-value')}
							</th>

							<th scope="col">
								{Liferay.Language.get('recommended-value')}
							</th>

							<th scope="col">
								{Liferay.Language.get('status')}
							</th>

							<th scope="col">
								{Liferay.Language.get('message')}
							</th>

							<th scope="col">
								{Liferay.Language.get(
									'production-readiness-ignore'
								)}
							</th>
						</tr>
					</thead>

					<tbody>
						{results.map((result) => (
							<tr key={result.ruleKey}>
								<td>
									{Liferay.Language.get(
										'production-readiness-rule-' +
											result.ruleKey
									)}
								</td>

								<td>{result.currentValue || '—'}</td>

								<td>{result.recommendedValue || '—'}</td>

								<td>
									<StatusLabel result={result} />
								</td>

								<td>
									{result.message}

									{result.docsLink && (
										<>
											{' '}
											<a
												href={result.docsLink}
												rel="noopener noreferrer"
												target="_blank"
											>
												{Liferay.Language.get(
													'learn-more'
												)}
											</a>
										</>
									)}
								</td>

								<td>
									<IgnoreToggle
										disabled={togglingRuleKeys.has(
											result.ruleKey
										)}
										ignored={result.ignored}
										onToggle={() => onToggleIgnore(result)}
										ruleKey={result.ruleKey}
									/>
								</td>
							</tr>
						))}
					</tbody>
				</table>
			</ClayPanel.Body>
		</ClayPanel>
	);
};

export default CategoryTable;
