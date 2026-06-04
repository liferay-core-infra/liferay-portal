/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface Props {
	failed: number;
	passed: number;
	total: number;
}

const SummaryHeader: React.FC<Props> = ({failed, passed, total}) => {
	return (
		<div className="deployment-validation-summary">
			<div className="small text-secondary text-uppercase">
				{Liferay.Language.get('summary')}
			</div>

			<div className="align-items-baseline d-flex">
				<span className="deployment-validation-count-total h2 mb-0 mr-1">
					{total}
				</span>

				<span className="mr-3 text-secondary">
					{Liferay.Language.get('validations')}
				</span>

				<span className="deployment-validation-count-passed h2 mb-0 mr-1 text-success">
					{passed}
				</span>

				<span className="mr-3 text-secondary">
					{Liferay.Language.get('production-readiness-passed')}
				</span>

				<span className="deployment-validation-count-failed h2 mb-0 mr-1 text-danger">
					{failed}
				</span>

				<span className="text-secondary">
					{Liferay.Language.get('production-readiness-failed')}
				</span>
			</div>
		</div>
	);
};

export default SummaryHeader;
