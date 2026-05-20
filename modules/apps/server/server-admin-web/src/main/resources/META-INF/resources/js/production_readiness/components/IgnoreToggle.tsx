/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayToggle} from '@clayui/form';
import React from 'react';

interface Props {
	disabled?: boolean;
	ignored: boolean;
	onToggle: (next: boolean) => void;
	ruleKey: string;
}

const IgnoreToggle: React.FC<Props> = ({
	disabled = false,
	ignored,
	onToggle,
	ruleKey,
}) => {
	const label = ignored
		? Liferay.Language.get('production-readiness-unignore')
		: Liferay.Language.get('production-readiness-ignore');

	return (
		<ClayToggle
			aria-label={`${label}: ${ruleKey}`}
			disabled={disabled}
			label={label}
			onToggle={onToggle}
			toggled={ignored}
		/>
	);
};

export default IgnoreToggle;
