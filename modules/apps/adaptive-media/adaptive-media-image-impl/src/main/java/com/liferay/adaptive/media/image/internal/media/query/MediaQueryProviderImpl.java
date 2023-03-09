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

package com.liferay.adaptive.media.image.internal.media.query;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.internal.configuration.AMImageAttributeMapping;
import com.liferay.adaptive.media.image.internal.processor.AMImage;
import com.liferay.adaptive.media.image.media.query.Condition;
import com.liferay.adaptive.media.image.media.query.MediaQuery;
import com.liferay.adaptive.media.image.media.query.MediaQueryProvider;
import com.liferay.adaptive.media.image.processor.AMImageAttribute;
import com.liferay.adaptive.media.image.processor.AMImageProcessor;
import com.liferay.adaptive.media.image.url.AMImageURLFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.net.URI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = MediaQueryProvider.class)
public class MediaQueryProviderImpl implements MediaQueryProvider {

	@Override
	public List<MediaQuery> getMediaQueries(FileEntry fileEntry)
		throws PortalException {

		List<MediaQuery> mediaQueries = new ArrayList<>();

		Collection<AdaptiveMedia<AMImageProcessor>> adaptiveMedias =
			_getAdaptiveMedias(fileEntry);

		AdaptiveMedia<AMImageProcessor> previousAdaptiveMedia = null;

		for (AdaptiveMedia<AMImageProcessor> adaptiveMedia : adaptiveMedias) {
			AdaptiveMedia<AMImageProcessor> hdAdaptiveMedia =
				_getHDAdaptiveMedia(adaptiveMedia, adaptiveMedias);

			mediaQueries.add(
				_getMediaQuery(
					adaptiveMedia, previousAdaptiveMedia, hdAdaptiveMedia));

			previousAdaptiveMedia = adaptiveMedia;
		}

		return mediaQueries;
	}

	private AdaptiveMedia<AMImageProcessor> _findAdaptiveMedia(
		FileEntry fileEntry,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		try {
			List<AdaptiveMedia<AMImageProcessor>> adaptiveMedias =
				_amImageFinder.getAdaptiveMedias(
					amImageQueryBuilder -> amImageQueryBuilder.forFileEntry(
						fileEntry
					).forConfiguration(
						amImageConfigurationEntry.getUUID()
					).done());

			if (adaptiveMedias.isEmpty()) {
				return null;
			}

			return adaptiveMedias.get(0);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return null;
		}
	}

	private AdaptiveMedia<AMImageProcessor>
		_getAdaptiveMediaFromConfigurationEntry(
			FileEntry fileEntry,
			AMImageConfigurationEntry amImageConfigurationEntry) {

		AdaptiveMedia<AMImageProcessor> adaptiveMedia = _findAdaptiveMedia(
			fileEntry, amImageConfigurationEntry);

		if (adaptiveMedia != null) {
			return adaptiveMedia;
		}

		Map<String, String> properties = HashMapBuilder.put(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH.getName(),
			() -> {
				Integer width = _getPropertiesValue(
					amImageConfigurationEntry, "max-width");

				if (width == null) {
					return String.valueOf(0);
				}

				return String.valueOf(width);
			}
		).put(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT.getName(),
			() -> {
				Integer height = _getPropertiesValue(
					amImageConfigurationEntry, "max-height");

				if (height == null) {
					return String.valueOf(0);
				}

				return String.valueOf(height);
			}
		).build();

		return new AMImage(
			() -> null, AMImageAttributeMapping.fromProperties(properties),
			_getFileEntryURL(fileEntry, amImageConfigurationEntry));
	}

	private Collection<AdaptiveMedia<AMImageProcessor>> _getAdaptiveMedias(
			FileEntry fileEntry)
		throws PortalException {

		List<AdaptiveMedia<AMImageProcessor>> adaptiveMedias =
			new ArrayList<>();

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				fileEntry.getCompanyId());

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			AdaptiveMedia<AMImageProcessor> adaptiveMedia =
				_getAdaptiveMediaFromConfigurationEntry(
					fileEntry, amImageConfigurationEntry);

			int widthValue = _getValue(
				adaptiveMedia, AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

			if (widthValue > 0) {
				adaptiveMedias.add(adaptiveMedia);
			}
		}

		adaptiveMedias.sort(_comparator);

		return adaptiveMedias;
	}

	private List<Condition> _getConditions(
		AdaptiveMedia<AMImageProcessor> adaptiveMedia,
		AdaptiveMedia<AMImageProcessor> previousAdaptiveMedia) {

		List<Condition> conditions = new ArrayList<>();

		int widthValue = _getValue(
			adaptiveMedia, AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

		conditions.add(new Condition("max-width", widthValue + "px"));

		if (previousAdaptiveMedia != null) {
			widthValue = _getValue(
				previousAdaptiveMedia,
				AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

			conditions.add(new Condition("min-width", widthValue + "px"));
		}

		return conditions;
	}

	private URI _getFileEntryURL(
		FileEntry fileEntry,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		try {
			return _amImageURLFactory.createFileEntryURL(
				fileEntry.getFileVersion(), amImageConfigurationEntry);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private AdaptiveMedia<AMImageProcessor> _getHDAdaptiveMedia(
		AdaptiveMedia<AMImageProcessor> originalAdaptiveMedia,
		Collection<AdaptiveMedia<AMImageProcessor>> adaptiveMedias) {

		int widthValue = _getValue(
			originalAdaptiveMedia, AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

		int originalWidth = widthValue * 2;

		int heightValue = _getValue(
			originalAdaptiveMedia, AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT);

		int originalHeight = heightValue * 2;

		for (AdaptiveMedia<AMImageProcessor> adaptiveMedia : adaptiveMedias) {
			int width = _getValue(
				adaptiveMedia, AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

			if ((width != (originalWidth - 1)) && (width != originalWidth) &&
				(width != (originalWidth + 1))) {

				continue;
			}

			int height = _getValue(
				adaptiveMedia, AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT);

			if ((height != (originalHeight - 1)) &&
				(height != originalHeight) &&
				(height != (originalHeight + 1))) {

				continue;
			}

			return adaptiveMedia;
		}

		return null;
	}

	private MediaQuery _getMediaQuery(
			AdaptiveMedia<AMImageProcessor> adaptiveMedia,
			AdaptiveMedia<AMImageProcessor> previousAdaptiveMedia,
			AdaptiveMedia<AMImageProcessor> hdAdaptiveMedia)
		throws PortalException {

		StringBundler sb = new StringBundler(4);

		List<Condition> conditions = _getConditions(
			adaptiveMedia, previousAdaptiveMedia);

		sb.append(adaptiveMedia.getURI());

		if (hdAdaptiveMedia != null) {
			sb.append(", ");
			sb.append(hdAdaptiveMedia.getURI());
			sb.append(" 2x");
		}

		return new MediaQuery(conditions, sb.toString());
	}

	private Integer _getPropertiesValue(
		AMImageConfigurationEntry amImageConfigurationEntry, String name) {

		try {
			Map<String, String> properties =
				amImageConfigurationEntry.getProperties();

			return Integer.valueOf(properties.get(name));
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(numberFormatException);
			}

			return null;
		}
	}

	private Integer _getValue(
		AdaptiveMedia<AMImageProcessor> adaptiveMedia,
		AMAttribute<AMImageProcessor, Integer> amAttribute) {

		Integer value = adaptiveMedia.getValue(amAttribute);

		if (value == null) {
			return 0;
		}

		return value;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MediaQueryProviderImpl.class);

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private AMImageFinder _amImageFinder;

	@Reference
	private AMImageURLFactory _amImageURLFactory;

	private final Comparator<AdaptiveMedia<AMImageProcessor>> _comparator =
		Comparator.comparingInt(
			adaptiveMedia -> _getValue(
				adaptiveMedia, AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH));

}