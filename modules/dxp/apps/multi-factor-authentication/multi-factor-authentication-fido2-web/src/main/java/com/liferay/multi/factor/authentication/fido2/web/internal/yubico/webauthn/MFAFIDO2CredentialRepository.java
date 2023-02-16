/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.multi.factor.authentication.fido2.web.internal.yubico.webauthn;

import com.liferay.multi.factor.authentication.fido2.credential.model.MFAFIDO2CredentialEntry;
import com.liferay.multi.factor.authentication.fido2.credential.service.MFAFIDO2CredentialEntryLocalService;
import com.liferay.multi.factor.authentication.fido2.web.internal.util.ConvertUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.PublicKeyCredentialType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Arthur Chan
 */
public class MFAFIDO2CredentialRepository implements CredentialRepository {

	public MFAFIDO2CredentialRepository(
		MFAFIDO2CredentialEntryLocalService mfaFIDO2CredentialEntryLocalService,
		UserLocalService userLocalService) {

		_mfaFIDO2CredentialEntryLocalService =
			mfaFIDO2CredentialEntryLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(
		String userName) {

		Optional<Long> userIdOptional = _getUserId(userName);

		return userIdOptional.map(
			_mfaFIDO2CredentialEntryLocalService::
				getMFAFIDO2CredentialEntriesByUserId
		).map(
			List::stream
		).map(
			stream -> stream.map(
				this::_buildPublicKeyCredentialDescriptor
			).collect(
				Collectors.toSet()
			)
		).orElse(
			Collections.emptySet()
		);
	}

	@Override
	public Optional<ByteArray> getUserHandleForUsername(String userName) {
		Optional<Long> userIdOptional = _getUserId(userName);

		return userIdOptional.map(ConvertUtil::toByteArray);
	}

	@Override
	public Optional<String> getUsernameForUserHandle(
		ByteArray userHandleByteArray) {

		return Optional.ofNullable(
			_userLocalService.fetchUserById(
				ConvertUtil.toLong(userHandleByteArray))
		).map(
			User::getScreenName
		);
	}

	@Override
	public Optional<RegisteredCredential> lookup(
		ByteArray credentialIdByteArray, ByteArray userHandleByteArray) {

		return Optional.ofNullable(
			_mfaFIDO2CredentialEntryLocalService.
				fetchMFAFIDO2CredentialEntryByUserIdAndCredentialKey(
					ConvertUtil.toLong(userHandleByteArray),
					credentialIdByteArray.getBase64())
		).filter(
			mfaFIDO2CredentialEntry -> Objects.equals(
				mfaFIDO2CredentialEntry.getCredentialKey(),
				credentialIdByteArray.getBase64())
		).map(
			this::_buildRegisteredCredential
		);
	}

	@Override
	public Set<RegisteredCredential> lookupAll(
		ByteArray credentialIdByteArray) {

		Set<RegisteredCredential> registeredCredentials = new HashSet<>();

		List<MFAFIDO2CredentialEntry> mfaFIDO2CredentialEntries =
			_mfaFIDO2CredentialEntryLocalService.
				getMFAFIDO2CredentialEntriesByCredentialKey(
					credentialIdByteArray.getBase64());

		for (MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry :
				mfaFIDO2CredentialEntries) {

			registeredCredentials.add(
				_buildRegisteredCredential(mfaFIDO2CredentialEntry));
		}

		return registeredCredentials;
	}

	private PublicKeyCredentialDescriptor _buildPublicKeyCredentialDescriptor(
		MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry) {

		return PublicKeyCredentialDescriptor.builder(
		).id(
			ByteArray.fromBase64(mfaFIDO2CredentialEntry.getCredentialKey())
		).type(
			PublicKeyCredentialType.PUBLIC_KEY
		).build();
	}

	private RegisteredCredential _buildRegisteredCredential(
		MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry) {

		return RegisteredCredential.builder(
		).credentialId(
			ByteArray.fromBase64(mfaFIDO2CredentialEntry.getCredentialKey())
		).userHandle(
			ConvertUtil.toByteArray(mfaFIDO2CredentialEntry.getUserId())
		).publicKeyCose(
			ByteArray.fromBase64(mfaFIDO2CredentialEntry.getPublicKeyCOSE())
		).signatureCount(
			mfaFIDO2CredentialEntry.getSignatureCount()
		).build();
	}

	private Long _getUserId(String userName) {
		User user = _userLocalService.fetchUserByScreenName(
			CompanyThreadLocal.getCompanyId(), userName);

		if (user == null) {
			return null;
		}

		return user.getUserId();
	}

	private final MFAFIDO2CredentialEntryLocalService
		_mfaFIDO2CredentialEntryLocalService;
	private final UserLocalService _userLocalService;

}