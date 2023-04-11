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

package com.liferay.headless.builder.internal.operation.handler;

import com.liferay.headless.builder.internal.constants.HeadlessBuilderConstants;
import com.liferay.headless.builder.internal.operation.Operation;
import com.liferay.headless.builder.internal.util.URLUtil;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(
	property = HeadlessBuilderConstants.OPERATION_NAME + "=getByPrimaryKey",
	service = OperationHandler.class
)
public class GetByPrimaryKeyOperationHandler implements OperationHandler {

	@Override
	public Response handle(
			HttpServletRequest httpServletRequest, Operation operation)
		throws Exception {

		Operation.Response response = operation.getResponse(
			httpServletRequest.getHeader(HttpHeaders.ACCEPT),
			Response.Status.OK.getStatusCode());

		InfoItemObjectProvider<?> infoItemObjectProvider = _getInfoItemService(
			response.getEntityName(), InfoItemObjectProvider.class);

		try {
			Map<String, String> pathParameters = URLUtil.getPathParameters(
				httpServletRequest.getRequestURI(),
				operation.getPathConfiguration());

			Object object = infoItemObjectProvider.getInfoItem(
				GetterUtil.getLong(pathParameters.get("id")));

			InfoItemFieldValuesProvider infoItemFieldValuesProvider =
				_getInfoItemService(
					response.getEntityName(),
					InfoItemFieldValuesProvider.class);

			return Response.status(
				Response.Status.OK
			).entity(
				_getEntity(
					infoItemFieldValuesProvider.getInfoItemFieldValues(object),
					response)
			).build();
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			String message = noSuchInfoItemException.getMessage();

			Throwable throwable = noSuchInfoItemException.getCause();

			if (throwable != null) {
				message = throwable.getMessage();
			}

			return Response.status(
				Response.Status.NOT_FOUND
			).entity(
				new Problem(Response.Status.NOT_FOUND, message)
			).build();
		}
	}

	private Map<String, Object> _getEntity(
		InfoItemFieldValues infoItemFieldValues, Operation.Response response) {

		Map<String, Object> entity = new HashMap<>();

		Map<String, InfoField> infoFields = response.getInfoFields();

		for (Map.Entry<String, InfoField> entry : infoFields.entrySet()) {
			entity.put(
				entry.getKey(),
				_getValue(infoItemFieldValues, entry.getValue()));
		}

		return entity;
	}

	private <T> T _getInfoItemService(String className, Class<T> serviceClass)
		throws Exception {

		T infoItemService = _infoItemServiceRegistry.getFirstInfoItemService(
			serviceClass, className);

		if (infoItemService == null) {
			throw new NoSuchInfoItemException(
				serviceClass.getSimpleName() + " is not defined for " +
					className);
		}

		return infoItemService;
	}

	private Object _getValue(
		InfoItemFieldValues infoItemFieldValues, InfoField infoField) {

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(infoField.getName());

		return infoFieldValue.getValue();
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}