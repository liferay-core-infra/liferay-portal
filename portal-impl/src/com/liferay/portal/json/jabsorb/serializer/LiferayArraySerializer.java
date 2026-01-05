/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json.jabsorb.serializer;

import org.jabsorb.serializer.MarshallException;
import org.jabsorb.serializer.SerializerState;
import org.jabsorb.serializer.impl.ArraySerializer;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Lily Chi
 */
public class LiferayArraySerializer extends ArraySerializer {

	@Override
	public Object marshall(SerializerState state, Object parent, Object object)
		throws MarshallException {

		try {
			JSONArray jsonArray = (JSONArray)super.marshall(
				state, parent, object);

			if (object instanceof int[]) {
				jsonArray.put("[Ljava.lang.Integer;");
			}
			else if (object instanceof long[]) {
				jsonArray.put("[Ljava.lang.Long;");
			}
			else if (object instanceof short[]) {
				jsonArray.put("[Ljava.lang.Short;");
			}
			else if (object instanceof byte[]) {
				jsonArray.put("[Ljava.lang.Byte;");
			}
			else if (object instanceof float[]) {
				jsonArray.put("[Ljava.lang.Float;");
			}
			else if (object instanceof double[]) {
				jsonArray.put("[Ljava.lang.Double;");
			}
			else if (object instanceof char[]) {
				jsonArray.put("[Ljava.lang.Character;");
			}
			else if (object instanceof boolean[]) {
				jsonArray.put("[Ljava.lang.Boolean;");
			}
			else if (object instanceof Object[]) {
				jsonArray.put("[Ljava.lang.Object;");
			}

			return jsonArray;
		}
		catch (JSONException jsonException) {
			throw new MarshallException(
				jsonException.getMessage() + " threw json exception",
				jsonException);
		}
	}

}