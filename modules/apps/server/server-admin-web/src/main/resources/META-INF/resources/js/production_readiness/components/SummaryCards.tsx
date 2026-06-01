/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import {Summary} from '../types';

interface Props {
	summary: Summary;
}

const SummaryCards: React.FC<Props> = ({summary}) => {
	return (
		<ClayLayout.Row className="mb-4 production-readiness-summary">
			<ClayLayout.Col size={4}>
				<div className="card card-type-asset">
					<div className="card-body">
						<div className="card-title h2 text-success">
							{summary.passed}
						</div>

						<div className="card-subtitle text-secondary">
							{Liferay.Language.get(
								'production-readiness-passed'
							)}
						</div>
					</div>
				</div>
			</ClayLayout.Col>

			<ClayLayout.Col size={4}>
				<div className="card card-type-asset">
					<div className="card-body">
						<div className="card-title h2 text-danger">
							{summary.failed}
						</div>

						<div className="card-subtitle text-secondary">
							{Liferay.Language.get(
								'production-readiness-failed'
							)}
						</div>
					</div>
				</div>
			</ClayLayout.Col>

			<ClayLayout.Col size={4}>
				<div className="card card-type-asset">
					<div className="card-body">
						<div className="card-title h2 text-secondary">
							{summary.ignored}
						</div>

						<div className="card-subtitle text-secondary">
							{Liferay.Language.get(
								'production-readiness-ignored'
							)}
						</div>
					</div>
				</div>
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
};

export default SummaryCards;
