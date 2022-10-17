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

package com.liferay.portal.template.velocity.internal;

import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.template.BaseTemplateResourceLoader;
import com.liferay.portal.template.TemplateResourceParser;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Igor Spasic
 * @author Peter Fellwock
 */
@Component(
	immediate = true,
	service = {
		TemplateResourceLoader.class, VelocityTemplateResourceLoader.class
	}
)
public class VelocityTemplateResourceLoader extends BaseTemplateResourceLoader {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		init(
			TemplateConstants.LANG_TYPE_VM,
			(Set<TemplateResourceParser>)_templateResourceParsers,
			_velocityTemplateResourceCache);
	}

	@Deactivate
	protected void deactivate() {
		destroy();
	}

	@Reference(
		fieldOption = FieldOption.UPDATE,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(lang.type=" + TemplateConstants.LANG_TYPE_VM + ")"
	)
	private volatile Collection<TemplateResourceParser>
		_templateResourceParsers = new CopyOnWriteArraySet<>();

	@Reference
	private VelocityTemplateResourceCache _velocityTemplateResourceCache;

}