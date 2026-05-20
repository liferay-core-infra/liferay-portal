/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayManagementToolbar from '@clayui/management-toolbar';
import React from 'react';

import {FilterState} from '../types';

interface Props {
	filterState: FilterState;
	onChange: (next: FilterState) => void;
	onRefresh: () => void;
}

const FilterToolbar: React.FC<Props> = ({filterState, onChange, onRefresh}) => {
	const toggle = (key: keyof FilterState) =>
		onChange({...filterState, [key]: !filterState[key]});

	return (
		<ClayManagementToolbar>
			<ClayManagementToolbar.ItemList>
				<ClayManagementToolbar.Item>
					<ClayButton
						aria-pressed={filterState.showPassed}
						displayType={
							filterState.showPassed ? 'success' : 'secondary'
						}
						onClick={() => toggle('showPassed')}
						small
					>
						{Liferay.Language.get('production-readiness-passed')}
					</ClayButton>
				</ClayManagementToolbar.Item>

				<ClayManagementToolbar.Item>
					<ClayButton
						aria-pressed={filterState.showFailed}
						displayType={
							filterState.showFailed ? 'danger' : 'secondary'
						}
						onClick={() => toggle('showFailed')}
						small
					>
						{Liferay.Language.get('production-readiness-failed')}
					</ClayButton>
				</ClayManagementToolbar.Item>

				<ClayManagementToolbar.Item>
					<ClayButton
						aria-pressed={filterState.showIgnored}
						displayType={
							filterState.showIgnored ? 'info' : 'secondary'
						}
						onClick={() => toggle('showIgnored')}
						small
					>
						{Liferay.Language.get(
							'production-readiness-show-ignored'
						)}
					</ClayButton>
				</ClayManagementToolbar.Item>
			</ClayManagementToolbar.ItemList>

			<ClayManagementToolbar.ItemList expand>
				<ClayManagementToolbar.Item className="ml-auto">
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('refresh')}
						displayType="secondary"
						onClick={onRefresh}
						small
						symbol="reload"
					/>
				</ClayManagementToolbar.Item>
			</ClayManagementToolbar.ItemList>
		</ClayManagementToolbar>
	);
};

export default FilterToolbar;
