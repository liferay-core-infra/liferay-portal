/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';

import {NODE_SCRIPTS_PATH} from './constants.mjs';
import digestNodeScripts from './digestNodeScripts.mjs';
import objectSF from './objectSF.mjs';

export default async function maybeUpdateNodeScriptsHash() {
	const packageJSONPath = path.join(NODE_SCRIPTS_PATH, 'package.json');

	const packageJSON = JSON.parse(await fs.readFile(packageJSONPath, 'utf-8'));

	const expectedHash = await digestNodeScripts();

	if (packageJSON['com.liferay']['sha256'] === expectedHash) {
		return false;
	}

	packageJSON['com.liferay']['sha256'] = expectedHash;

	await fs.writeFile(packageJSONPath, objectSF(packageJSON), 'utf-8');

	console.log('🔐 Updated sha256 field of package.json');

	return expectedHash;
}
