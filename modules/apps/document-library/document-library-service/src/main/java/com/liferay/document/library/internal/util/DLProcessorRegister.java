package com.liferay.document.library.internal.util;

import com.liferay.document.library.kernel.util.DLProcessor;
import com.liferay.document.library.kernel.util.DLProcessorRegistry;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(service = {})
public class DLProcessorRegister {

	@Activate
	protected void activate() throws Exception {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		for (String dlProcessorClassName : _DL_FILE_ENTRY_PROCESSORS) {
			DLProcessor dlProcessor = (DLProcessor)InstanceFactory.newInstance(
				classLoader, dlProcessorClassName);

			dlProcessor.afterPropertiesSet();

			_dlProcessorRegistry.register(dlProcessor);

			_dlProcessors.add(dlProcessor);
		}
	}

	@Deactivate
	protected void deactivate() throws Exception {
		UnsafeConsumer.accept(
			_dlProcessors,
			dlProcessor -> {
				_dlProcessorRegistry.unregister(dlProcessor);

				dlProcessor.destroy();
			},
			Exception.class);

		_dlProcessors.clear();
	}

	private static final String[] _DL_FILE_ENTRY_PROCESSORS =
		PropsUtil.getArray(PropsKeys.DL_FILE_ENTRY_PROCESSORS);

	@Reference
	private DLProcessorRegistry _dlProcessorRegistry;

	private final List<DLProcessor> _dlProcessors = new ArrayList<>(
		_DL_FILE_ENTRY_PROCESSORS.length);

}