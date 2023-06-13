/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.rest.internal.configuration.admin.service.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CompanyConstants;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;

import org.osgi.service.component.annotations.Component;

/**
 * @author Joao Victor Alves
 */
@Component(service = OAuth2InAssertionManagedServiceFactoryHelper.class)
public class OAuth2InAssertionManagedServiceFactoryHelper {

	public JwsSignatureVerifier getJWSSignatureVerifier(
			long companyId, String issuer, String kid)
		throws IllegalArgumentException {

		StringBundler sb = new StringBundler(12);

		Map<String, Map<String, JwsSignatureVerifier>> jwsSignatureVerifiers =
			_jwsSignatureVerifiers.getOrDefault(
				companyId, _jwsSignatureVerifiers.get(CompanyConstants.SYSTEM));

		if (jwsSignatureVerifiers == null) {
			sb.append("No JWS signature keys in company: ");
			sb.append(companyId);

			throw new IllegalArgumentException(sb.toString());
		}

		Map<String, JwsSignatureVerifier> kidsJWSSignatureVerifiers =
			jwsSignatureVerifiers.get(issuer);

		if (kidsJWSSignatureVerifiers == null) {
			sb.append("No JWS signature keys for issuer: ");
			sb.append(issuer);
			sb.append(", in company: ");
			sb.append(companyId);

			throw new IllegalArgumentException(sb.toString());
		}

		if (!kidsJWSSignatureVerifiers.containsKey(kid)) {
			sb.append("No JWS signature key of kid: ");
			sb.append(kid);
			sb.append(", for issuer: ");
			sb.append(issuer);
			sb.append(", in company: ");
			sb.append(companyId);

			throw new IllegalArgumentException(sb.toString());
		}

		return kidsJWSSignatureVerifiers.get(kid);
	}

	public Map<String, Map<String, JwsSignatureVerifier>>
		getJWSSignatureVerifiers(Long companyId) {

		return _jwsSignatureVerifiers.get(companyId);
	}

	public Set<Long> getKeySetJWSSignatureVerifiers() {
		return _jwsSignatureVerifiers.keySet();
	}

	public String getUserAuthType(long companyId, String issuer)
		throws IllegalArgumentException {

		StringBundler sb = new StringBundler(6);

		Map<String, String> userAuthTypes = _userAuthTypes.getOrDefault(
			companyId, _userAuthTypes.get(CompanyConstants.SYSTEM));

		if (userAuthTypes == null) {
			sb.append("No user auth types in company: ");
			sb.append(companyId);

			throw new IllegalArgumentException(sb.toString());
		}

		if (!userAuthTypes.containsKey(issuer)) {
			sb.append("No user auth type for issuer: ");
			sb.append(issuer);
			sb.append(", in company: ");
			sb.append(companyId);

			throw new IllegalArgumentException(sb.toString());
		}

		return userAuthTypes.get(issuer);
	}

	public Map<String, String> getUserAuthTypes(long companyId) {
		return _userAuthTypes.get(companyId);
	}

	public void updateJWSSignatureVerifiers(
		Long companyId,
		Map<String, Map<String, JwsSignatureVerifier>> jwsSignatureVerifiers) {

		_jwsSignatureVerifiers.put(companyId, jwsSignatureVerifiers);
	}

	public void updateUserAuthTypes(
		long companyId, Map<String, String> userAuthTypes) {

		_userAuthTypes.put(companyId, userAuthTypes);
	}

	private final Map<Long, Map<String, Map<String, JwsSignatureVerifier>>>
		_jwsSignatureVerifiers = Collections.synchronizedMap(
			new LinkedHashMap<>());
	private final Map<Long, Map<String, String>> _userAuthTypes =
		Collections.synchronizedMap(new LinkedHashMap<>());

}