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

package com.liferay.portal.osgi.web.wab.generator.internal;

import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.osgi.web.wab.generator.ClientExtensionGenerator;
import com.liferay.portal.osgi.web.wab.generator.internal.artifact.ClientExtensionArtifactURLTransformer;
import com.liferay.portal.osgi.web.wab.generator.internal.handler.ClientExtensionURLStreamHandlerService;
import com.liferay.portal.osgi.web.wab.generator.internal.processor.ClientExtensionProcessor;

import java.io.File;
import java.io.IOException;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;

/**
 * @author Gregory Amerson
 */
@Component(immediate = true, service = ClientExtensionGenerator.class)
public class ClientExtensionGeneratorImpl implements ClientExtensionGenerator {

	@Override
	public File generate(File file, Map<String, String[]> parameters)
		throws IOException {

		ClientExtensionProcessor clientExtensionProcessor =
			new ClientExtensionProcessor(file, parameters);

		return clientExtensionProcessor.getProcessedFile();
	}

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		_registerURLStreamHandlerService(bundleContext);

		_registerArtifactUrlTransformer(bundleContext);
	}

	@Deactivate
	protected void deactivate(BundleContext bundleContext) throws Exception {
		_serviceRegistration.unregister();

		_serviceRegistration = null;
	}

	private void _registerArtifactUrlTransformer(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			FileInstaller.class, new ClientExtensionArtifactURLTransformer(),
			null);
	}

	private void _registerURLStreamHandlerService(BundleContext bundleContext) {
		bundleContext.registerService(
			URLStreamHandlerService.class.getName(),
			new ClientExtensionURLStreamHandlerService(this),
			HashMapDictionaryBuilder.<String, Object>put(
				URLConstants.URL_HANDLER_PROTOCOL,
				new String[] {"clientextension"}
			).build());
	}

	private ServiceRegistration<FileInstaller> _serviceRegistration;

}