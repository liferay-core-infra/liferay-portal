/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import path from 'path';

import runPreflight from '../preflight/runPreflight.mjs';
import {LIFERAY_WORKING_BRANCH, getRootDir} from '../util/constants.mjs';
import getNamedArguments from '../util/getNamedArguments.mjs';
import {getCurrentBranchName} from '../util/gitCommands.mjs';
import maybeUpdateNodeScriptsHash from '../util/maybeUpdateNodeScriptsHash.mjs';
import format from './format.mjs';

export default async function main() {
	const args = getNamedArguments({
		all: '--all',
		check: '--check',
	});

	let all = args.all;

	const currentBranchName = await getCurrentBranchName();

	if (currentBranchName === LIFERAY_WORKING_BRANCH) {
		all = true;

		console.log(
			`ℹ️ Current branch is '${currentBranchName}', checking all files...`
		);
	}

	const rootDir = await getRootDir();

	if (!args.check) {
		await maybeUpdateNodeScriptsHash();
	}

	if (path.resolve(process.cwd()) === rootDir) {
		console.log('🛫 Running preflight...');
		await runPreflight({all});
	}

	console.log('📝 Running format...');
	await format(!args.check, {all});
}
