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

package com.liferay.frontend.theme.contributor.extender.internal.servlet.taglib;

import com.liferay.frontend.theme.contributor.extender.internal.BundleWebResources;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = DynamicInclude.class)
public class ThemeContributorTopHeadDynamicInclude implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		long themeLastModified = PortalWebResourcesUtil.getLastModified(
			PortalWebResourceConstants.RESOURCE_TYPE_THEME_CONTRIBUTOR);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String portalCDNURL = themeDisplay.getCDNBaseURL();

		if (!_portal.isCDNDynamicResourcesEnabled(
				themeDisplay.getCompanyId())) {

			portalCDNURL = themeDisplay.getPortalURL();
		}

		ResourceURLsBag resourceURLsBag = _rebuild();

		String[] cssResourceURLs = resourceURLsBag.getCssResourceURLs();

		if (ArrayUtil.isNotEmpty(cssResourceURLs)) {
			if (themeDisplay.isThemeCssFastLoad()) {
				_renderComboCSS(
					themeLastModified, httpServletRequest, portalCDNURL,
					httpServletResponse.getWriter(),
					resourceURLsBag.getMergedCSSResourceURLs());
			}
			else {
				_renderSimpleCSS(
					themeLastModified, httpServletRequest, portalCDNURL,
					httpServletResponse.getWriter(), cssResourceURLs);
			}
		}

		String[] jsResourceURLs = resourceURLsBag.getJsResourceURLs();

		if (ArrayUtil.isEmpty(jsResourceURLs)) {
			return;
		}

		if (themeDisplay.isThemeJsFastLoad()) {
			_renderComboJS(
				themeLastModified, httpServletRequest, portalCDNURL,
				httpServletResponse.getWriter(),
				_resourceURLsBag.getMergedJSResourceURLs());
		}
		else {
			_renderSimpleJS(
				themeLastModified, httpServletRequest, portalCDNURL,
				httpServletResponse.getWriter(), jsResourceURLs);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_head.jsp#post");
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_comboContextPath = _portal.getPathContext() + "/combo";
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC
	)
	protected void addBundleWebResources(
		ServiceReference<BundleWebResources>
			bundleWebResourcesServiceReference) {

		synchronized (_bundleWebResourcesServiceReferences) {
			_bundleWebResourcesServiceReferences.add(
				bundleWebResourcesServiceReference);

			_resourceURLsBag = null;
		}
	}

	protected void removeBundleWebResources(
		ServiceReference<BundleWebResources>
			bundleWebResourcesServiceReference) {

		synchronized (_bundleWebResourcesServiceReferences) {
			_bundleWebResourcesServiceReferences.remove(
				bundleWebResourcesServiceReference);

			_resourceURLsBag = null;
		}
	}

	private ResourceURLsBag _rebuild() {
		ResourceURLsBag resourceURLsBag = _resourceURLsBag;

		if (resourceURLsBag != null) {
			return resourceURLsBag;
		}

		synchronized (_bundleWebResourcesServiceReferences) {
			if (_resourceURLsBag != null) {
				return _resourceURLsBag;
			}

			Collection<String> cssResourceURLs = new ArrayList<>();
			Collection<String> jsResourceURLs = new ArrayList<>();

			for (ServiceReference<BundleWebResources>
					bundleWebResourcesServiceReference :
						_bundleWebResourcesServiceReferences) {

				BundleWebResources bundleWebResources =
					_bundleContext.getService(
						bundleWebResourcesServiceReference);

				try {
					String servletContextPath =
						bundleWebResources.getServletContextPath();

					for (String cssResourcePath :
							bundleWebResources.getCssResourcePaths()) {

						cssResourceURLs.add(
							servletContextPath.concat(cssResourcePath));
					}

					for (String jsResourcePath :
							bundleWebResources.getJsResourcePaths()) {

						jsResourceURLs.add(
							servletContextPath.concat(jsResourcePath));
					}
				}
				finally {
					_bundleContext.ungetService(
						bundleWebResourcesServiceReference);
				}
			}

			StringBundler sb = new StringBundler(
				(cssResourceURLs.size() * 2) + 1);

			for (String cssResourceURL : cssResourceURLs) {
				sb.append("&");
				sb.append(cssResourceURL);
			}

			sb.append("\" rel=\"stylesheet\" type = \"text/css\" />\n");

			_resourceURLsBag = new ResourceURLsBag();

			_resourceURLsBag.setCssResourceURLs(
				cssResourceURLs.toArray(new String[0]));
			_resourceURLsBag.setMergedCSSResourceURLs(sb.toString());
			_resourceURLsBag.setJsResourceURLs(
				jsResourceURLs.toArray(new String[0]));

			sb = new StringBundler((jsResourceURLs.size() * 2) + 1);

			for (String jsResourceURL : jsResourceURLs) {
				sb.append("&");
				sb.append(jsResourceURL);
			}

			sb.append("\" type = \"text/javascript\"></script>\n");

			_resourceURLsBag.setMergedJSResourceURLs(sb.toString());

			return _resourceURLsBag;
		}
	}

	private void _renderComboCSS(
		long themeLastModified, HttpServletRequest httpServletRequest,
		String portalURL, PrintWriter printWriter,
		String mergedCSSResourceURLs) {

		printWriter.write("<link data-senna-track=\"permanent\" href=\"");

		String staticResourceURL = _portal.getStaticResourceURL(
			httpServletRequest, _comboContextPath, "minifierType=css",
			themeLastModified);

		printWriter.write(portalURL + staticResourceURL);

		printWriter.write(mergedCSSResourceURLs);
	}

	private void _renderComboJS(
		long themeLastModified, HttpServletRequest httpServletRequest,
		String portalURL, PrintWriter printWriter,
		String mergedJSResourceURLs) {

		printWriter.write("<script data-senna-track=\"permanent\" src=\"");

		String staticResourceURL = _portal.getStaticResourceURL(
			httpServletRequest, _comboContextPath, "minifierType=js",
			themeLastModified);

		printWriter.write(portalURL + staticResourceURL);

		printWriter.write(mergedJSResourceURLs);
	}

	private void _renderSimpleCSS(
		long themeLastModified, HttpServletRequest httpServletRequest,
		String portalURL, PrintWriter printWriter, String[] resourceURLs) {

		for (String resourceURL : resourceURLs) {
			printWriter.write("<link data-senna-track=\"permanent\" href=\"");
			printWriter.write(
				_portal.getStaticResourceURL(
					httpServletRequest,
					StringBundler.concat(
						portalURL, _portal.getPathProxy(), resourceURL),
					themeLastModified));
			printWriter.write("\" rel=\"stylesheet\" type = \"text/css\" />\n");
		}
	}

	private void _renderSimpleJS(
		long themeLastModified, HttpServletRequest httpServletRequest,
		String portalURL, PrintWriter printWriter, String[] resourceURLs) {

		for (String resourceURL : resourceURLs) {
			printWriter.write("<script data-senna-track=\"permanent\" src=\"");
			printWriter.write(
				_portal.getStaticResourceURL(
					httpServletRequest,
					StringBundler.concat(
						portalURL, _portal.getPathProxy(), resourceURL),
					themeLastModified));
			printWriter.write("\" type = \"text/javascript\"></script>\n");
		}
	}

	private BundleContext _bundleContext;
	private final Collection<ServiceReference<BundleWebResources>>
		_bundleWebResourcesServiceReferences = new TreeSet<>();
	private String _comboContextPath;

	@Reference
	private Portal _portal;

	private volatile ResourceURLsBag _resourceURLsBag;

	private static class ResourceURLsBag {

		public String[] getCssResourceURLs() {
			return _cssResourceURLs;
		}

		public String[] getJsResourceURLs() {
			return _jsResourceURLs;
		}

		public String getMergedCSSResourceURLs() {
			return _mergedCSSResourceURLs;
		}

		public String getMergedJSResourceURLs() {
			return _mergedJSResourceURLs;
		}

		public void setCssResourceURLs(String[] cssResourceURLs) {
			_cssResourceURLs = cssResourceURLs;
		}

		public void setJsResourceURLs(String[] jsResourceURLs) {
			_jsResourceURLs = jsResourceURLs;
		}

		public void setMergedCSSResourceURLs(String mergedCSSResourceURLs) {
			_mergedCSSResourceURLs = mergedCSSResourceURLs;
		}

		public void setMergedJSResourceURLs(String mergedJSResourceURLs) {
			_mergedJSResourceURLs = mergedJSResourceURLs;
		}

		private String[] _cssResourceURLs;
		private String[] _jsResourceURLs;
		private String _mergedCSSResourceURLs;
		private String _mergedJSResourceURLs;

	}

}