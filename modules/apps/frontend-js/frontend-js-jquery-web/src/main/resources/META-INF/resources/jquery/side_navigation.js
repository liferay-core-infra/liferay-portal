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

+(function ($) {
	const doc = $(document);

	let listenerAdded = false;

	// Make sure we only add one resize listener to the page,
	// no matter how many components we have

	const addResizeListener = function () {
		if (!listenerAdded) {
			$(window).on(
				'resize',
				debounce(() => {
					doc.trigger('screenChange.lexicon.sidenav');
				}, 150)
			);

			listenerAdded = true;
		}
	};

	var debounce = function (fn, delay) {
		let id;

		return function () {
			const instance = this;

			const args = arguments;

			const later = function () {
				id = null;

				fn.apply(instance, args);
			};

			clearTimeout(id);

			id = setTimeout(later, delay);
		};
	};

	const getBreakpointRegion = function () {
		const screenXs = 480;
		const screenSm = 768;
		const screenMd = 992;
		const screenLg = 1200;

		const windowWidth = window.innerWidth;
		let region = '';

		if (windowWidth >= screenLg) {
			region = 'lg';
		}
		else if (windowWidth >= screenMd) {
			region = 'md';
		}
		else if (windowWidth >= screenSm) {
			region = 'sm';
		}
		else if (windowWidth >= screenXs) {
			region = 'xs';
		}
		else {
			region = 'xxs';
		}

		return region;
	};

	const guid = (function () {
		let counter = 0;

		return function (toggler, ns) {
			let strId = toggler.attr('id');

			if (!strId) {
				strId = ns + counter++;

				toggler.attr('id', strId);
			}

			return strId;
		};
	})();

	const toInt = function (str) {
		return parseInt(str, 10) || 0;
	};

	const SideNavigation = function (toggler, options) {
		this.init(toggler, options);
	};

	SideNavigation.TRANSITION_DURATION = 500;

	SideNavigation.prototype = {
		_bindUI() {
			const instance = this;

			if (!instance.useDataAttribute) {
				addResizeListener();
				instance._onScreenChange();
			}

			if (instance.options.useDelegate) {
				instance._onDelegateClickTrigger();
			}
			else {
				instance._onClickTrigger();
			}

			instance._onClickSidenavClose();
		},

		_focusElement(el) {

			// ios 8 fixed element disappears when trying to scroll

			el.focus();
		},

		_getSidenavWidth() {
			const instance = this;

			const options = instance.options;

			const widthOriginal = options.widthOriginal;

			let width = widthOriginal;
			const winWidth = window.innerWidth;

			if (winWidth < widthOriginal + 40) {
				width = winWidth - 40;
			}

			return width;
		},

		_getSimpleSidenavType() {
			const instance = this;

			const options = instance.options;

			const desktop = instance._isDesktop();
			const type = options.type;
			const typeMobile = options.typeMobile;

			if (desktop && type === 'fixed-push') {
				return 'desktop-fixed-push';
			}
			else if (!desktop && typeMobile === 'fixed-push') {
				return 'mobile-fixed-push';
			}

			return 'fixed';
		},

		_isDesktop() {
			return window.innerWidth >= this.options.breakpoint;
		},

		_isSidenavRight() {
			const instance = this;
			const options = instance.options;

			const container = $(options.container);
			const isSidenavRight = container.hasClass('sidenav-right');

			return isSidenavRight;
		},

		_isSimpleSidenavClosed() {
			const instance = this;
			const options = instance.options;

			const openClass = options.openClass;

			const container = $(options.container);

			return !container.hasClass(openClass);
		},

		_loadUrl(sidenav, url, eventTarget) {
			const instance = this;

			let urlLoaded = sidenav.data('url-loaded');

			const readyState = urlLoaded ? urlLoaded.readyState : 0;

			eventTarget = eventTarget || sidenav;

			const sidebarBody = sidenav.find('.sidebar-body').first();

			if (
				!readyState &&
				sidebarBody.length &&
				(typeof url === 'string' || $.isPlainObject(url))
			) {
				sidebarBody.append(
					'<div class="sidenav-loading">' +
						instance.options.loadingIndicatorTPL +
						'</div>'
				);

				urlLoaded = $.ajax(url).done((response) => {
					sidebarBody.append(response);

					eventTarget.trigger('urlLoaded.lexicon.sidenav');

					sidebarBody.find('.sidenav-loading').remove();
				});

				sidenav.data('url-loaded', urlLoaded);
			}

			return urlLoaded;
		},

		_onClickSidenavClose() {
			const instance = this;

			const options = instance.options;

			const containerSelector = options.container;

			const closeButton = $(containerSelector)
				.find('.sidenav-close')
				.first();
			const closeButtonSelector =
				'#' + guid(closeButton, 'generatedLexiconSidenavCloseId');
			const dataCloseButtonSelector = 'lexicon.' + closeButtonSelector;

			if (!doc.data(dataCloseButtonSelector)) {
				doc.data(dataCloseButtonSelector, 'true');

				doc.on(
					'click.close.lexicon.sidenav',
					closeButtonSelector,
					(event) => {
						event.preventDefault();

						instance.toggle();
					}
				);
			}

			instance.closeButtonSelector = closeButtonSelector;
			instance.dataCloseButtonSelector = dataCloseButtonSelector;
		},

		_onClickTrigger() {
			const instance = this;

			const el = instance.toggler;

			el.on('click.lexicon.sidenav', () => {
				instance.toggle();
			});
		},

		_onDelegateClickTrigger() {
			const instance = this;

			const toggler = instance.toggler;

			const togglerSelector =
				'#' + guid(toggler, 'generatedLexiconSidenavTogglerId');

			const dataTogglerSelector = 'lexicon.' + togglerSelector;

			if (!doc.data(dataTogglerSelector)) {
				doc.data(dataTogglerSelector, 'true');

				doc.on('click.lexicon.sidenav', togglerSelector, (event) => {
					instance.toggle();

					event.preventDefault();
				});
			}

			instance.togglerSelector = togglerSelector;
			instance.dataTogglerSelector = dataTogglerSelector;
		},

		_onScreenChange() {
			const instance = this;
			const options = instance.options;

			const container = $(options.container);
			const toggler = instance.toggler;

			let screenStartDesktop = instance._setScreenSize();

			doc.on('screenChange.lexicon.sidenav', () => {
				const desktop = instance._setScreenSize();
				const sidenavRight = instance._isSidenavRight();
				const type = desktop ? options.type : options.typeMobile;

				const fixedMenu = type === 'fixed' || type === 'fixed-push';

				const menu = container.find('.sidenav-menu').first();

				let menuWidth;

				const originalMenuWidth = options.widthOriginal;

				const positionDirection = options.rtl ? 'left' : 'right';

				container.toggleClass('sidenav-fixed', fixedMenu);

				if (
					(!desktop && screenStartDesktop) ||
					(desktop && !screenStartDesktop)
				) {
					instance.hideSidenav();

					instance.clearStyle(['min-height', 'height']);

					container.addClass('closed').removeClass('open');
					toggler.removeClass('active').removeClass('open');

					screenStartDesktop = false;

					if (desktop) {
						if (sidenavRight) {
							menu.css(positionDirection, originalMenuWidth).css(
								'width',
								originalMenuWidth
							);
						}

						screenStartDesktop = true;
					}
				}

				const closed = container.hasClass('closed');

				if (!desktop) {
					menuWidth = originalMenuWidth;

					if (window.innerWidth <= originalMenuWidth) {
						menuWidth = window.innerWidth - options.gutter - 25;
					}

					if (sidenavRight) {
						if (closed) {
							menu.css(positionDirection, menuWidth);
						}

						menu.css('width', menuWidth);
					}

					screenStartDesktop = false;
				}

				if (!closed) {
					instance.clearStyle(['min-height', 'height']);

					instance.showSidenav();
					instance.setHeight();
				}
			});
		},

		_onSidenavTransitionEnd(el, fn) {
			const instance = this;

			const transitionEnd = 'bsTransitionEnd';

			const complete = function () {
				el.removeClass('sidenav-transition');

				if (fn) {
					fn();
				}
			};

			if (!bootstrap.Util.supportsTransitionEnd()) {
				complete.call(instance);
			}
			else {
				el.one(transitionEnd, () => {
					complete();
				}).emulateTransitionEnd(SideNavigation.TRANSITION_DURATION);
			}
		},

		_renderNav() {
			const instance = this;
			const options = instance.options;

			const container = $(options.container);
			const slider = container.find(options.navigation).first();
			const menu = slider.find('.sidenav-menu').first();

			const closed = container.hasClass('closed');
			const sidenavRight = instance._isSidenavRight();
			const width = instance._getSidenavWidth();

			if (closed) {
				menu.css('width', width);

				if (sidenavRight) {
					const positionDirection = options.rtl ? 'left' : 'right';

					menu.css(positionDirection, width);
				}
			}
			else {
				instance.showSidenav();
				instance.setHeight();
			}

			container.removeClass('sidenav-js-fouc');
		},

		_renderUI() {
			const instance = this;
			const options = instance.options;

			const container = $(options.container);
			const toggler = instance.toggler;

			const mobile = instance.mobile;
			const type = mobile ? options.typeMobile : options.type;

			if (!instance.useDataAttribute) {
				if (mobile) {
					container.addClass('closed').removeClass('open');
					toggler.removeClass('active').removeClass('open');
				}

				if (options.position === 'right') {
					container.addClass('sidenav-right');
				}

				if (type !== 'relative') {
					container.addClass('sidenav-fixed');
				}

				instance._renderNav();
			}

			container.css('display', ''); // Force Reflow for IE11 Browser Bug
		},

		_setScreenSize() {
			const instance = this;

			const screenSize = getBreakpointRegion();

			const desktop =
				screenSize === 'sm' ||
				screenSize === 'md' ||
				screenSize === 'lg';

			instance.mobile = !desktop;
			instance.desktop = desktop;

			return desktop;
		},

		clearStyle(attribute) {
			const instance = this;

			const options = instance.options;

			const container = $(options.container);
			const content = container.find(options.content).first();
			const navigation = container.find(options.navigation).first();

			const menu = container.find('.sidenav-menu').first();

			const els = content.add(navigation).add(menu);

			if (Array.isArray(attribute)) {
				for (let i = 0; i < attribute.length; i++) {
					els.css(attribute[i], '');
				}
			}
			else {
				els.css(attribute, '');
			}
		},

		destroy() {
			const instance = this;

			const options = instance.options;

			const container = $(instance.options.container);

			// Detach sidenav close

			doc.off(
				'click.close.lexicon.sidenav',
				instance.closeButtonSelector
			);
			doc.data(instance.dataCloseButtonSelector, null);

			// Detach toggler

			if (options.useDelegate) {
				doc.off('click.lexicon.sidenav', instance.togglerSelector);
				doc.data(instance.dataTogglerSelector, null);
			}
			else {
				container.off('click.lexicon.sidenav');
			}

			// Remove Side Navigation

			container.data('lexicon.sidenav', null);
		},

		hide() {
			const instance = this;

			if (instance.useDataAttribute) {
				instance.hideSimpleSidenav();
			}
			else {
				instance.toggleNavigation(false);
			}
		},

		hideSidenav() {
			const instance = this;
			const options = instance.options;

			const container = $(options.container);
			const content = container.find(options.content).first();
			const navigation = container.find(options.navigation).first();
			const menu = navigation.find('.sidenav-menu').first();

			const sidenavRight = instance._isSidenavRight();

			let positionDirection = options.rtl ? 'right' : 'left';

			if (sidenavRight) {
				positionDirection = options.rtl ? 'left' : 'right';
			}

			const paddingDirection = 'padding-' + positionDirection;

			content.css(paddingDirection, '').css(positionDirection, '');

			navigation.css('width', '');

			if (sidenavRight) {
				menu.css(positionDirection, instance._getSidenavWidth());
			}
		},

		hideSimpleSidenav() {
			const instance = this;

			const options = instance.options;

			const simpleSidenavClosed = instance._isSimpleSidenavClosed();

			if (!simpleSidenavClosed) {
				const content = $(options.content).first();
				const sidenav = $(options.container);

				const closedClass = options.closedClass;
				const openClass = options.openClass;

				const toggler = instance.toggler;

				const target =
					toggler.attr('href') || toggler.attr('data-target');

				sidenav.trigger({
					toggler: $(instance.togglerSelector),
					type: 'closedStart.lexicon.sidenav',
				});

				instance._onSidenavTransitionEnd(content, () => {
					sidenav.removeClass('sidenav-transition');
					toggler.removeClass('sidenav-transition');

					sidenav.trigger({
						toggler: $(instance.togglerSelector),
						type: 'closed.lexicon.sidenav',
					});
				});

				if (content.hasClass(openClass)) {
					content
						.addClass('sidenav-transition')
						.addClass(closedClass)
						.removeClass(openClass);
				}

				sidenav.addClass('sidenav-transition');
				toggler.addClass('sidenav-transition');

				sidenav.addClass(closedClass).removeClass(openClass);

				$('[data-target="' + target + '"]')
					.removeClass(openClass)
					.removeClass('active');
				$('[href="' + target + '"]')
					.removeClass(openClass)
					.removeClass('active');
			}
		},

		init(toggler, options) {
			const instance = this;

			const useDataAttribute = toggler.data('toggle') === 'sidenav';

			options = $.extend({}, $.fn.sideNavigation.defaults, options);

			options.breakpoint = toInt(options.breakpoint);
			options.container =
				options.container ||
				toggler.data('target') ||
				toggler.attr('href');
			options.gutter = toInt(options.gutter);
			options.heightType =
				options.heightType ||
				(options.equalHeight ? 'equalHeight' : false);
			options.rtl = doc.attr('dir') === 'rtl';
			options.width = toInt(options.width);
			options.widthOriginal = options.width;

			// instantiate using data attribute

			if (useDataAttribute) {
				options.closedClass = toggler.data('closed-class') || 'closed';
				options.content = toggler.data('content');
				options.equalHeight = false; // equalHeight option is deprecated
				options.loadingIndicatorTPL =
					toggler.data('loading-indicator-tpl') ||
					options.loadingIndicatorTPL;
				options.openClass = toggler.data('open-class') || 'open';
				options.toggler = toggler;
				options.type = toggler.data('type');
				options.typeMobile = toggler.data('type-mobile');
				options.url = toggler.data('url');
				options.useDelegate = toggler.data('use-delegate');
				options.width = '';

				if (options.useDelegate === undefined) {
					options.useDelegate = true;
				}
			}

			instance.toggler = toggler;
			instance.options = options;
			instance.useDataAttribute = useDataAttribute;

			instance._bindUI();
			instance._renderUI();
		},

		setEqualHeight() {
			const instance = this;

			const options = instance.options;

			const container = $(options.container);
			const content = options.content;
			const navigation = options.navigation;

			const type = instance.mobile ? options.typeMobile : options.type;

			if (type !== 'fixed' && type !== 'fixed-push') {
				const contentNode = container.find(content).first();
				const navNode = container.find(navigation).first();
				const sideNavMenuNode = container.find('.sidenav-menu').first();

				const tallest = Math.max(
					contentNode.outerHeight(),
					navNode.outerHeight()
				);

				contentNode.css('min-height', tallest);

				navNode.css({
					'height': '100%',
					'min-height': tallest,
				});

				sideNavMenuNode.css({
					'height': '100%',
					'min-height': tallest,
				});
			}
		},

		setFullHeight() {
			const instance = this;

			const options = instance.options;

			const container = $(options.container);
			const navigation = options.navigation;

			const type = instance.mobile ? options.typeMobile : options.type;

			if (type === 'relative') {
				const navNode = container.find(navigation).first();
				const sidenavMenuNode = container.find('.sidenav-menu').first();

				let minHeight = doc.innerHeight() - navNode.offset().top;

				if (
					sidenavMenuNode.innerHeight() + navNode.offset().top >
					doc.innerHeight()
				) {
					minHeight = sidenavMenuNode.innerHeight();
				}

				navNode.css({
					'height': '100%',
					'min-height': minHeight,
				});

				sidenavMenuNode.css({
					'height': '100%',
					'min-height': minHeight,
				});
			}
		},

		setHeight() {
			const instance = this;

			const options = instance.options;

			if (options.heightType === 'equalHeight') {
				instance.setEqualHeight();
			}
			else if (options.heightType === 'fullHeight') {
				instance.setFullHeight();
			}
		},

		show() {
			const instance = this;

			if (instance.useDataAttribute) {
				instance.showSimpleSidenav();
			}
			else {
				instance.toggleNavigation(true);
			}
		},

		showSidenav() {
			const instance = this;
			const mobile = instance.mobile;
			const options = instance.options;

			const container = $(options.container);
			const content = container.find(options.content).first();
			const navigation = container.find(options.navigation).first();
			const menu = navigation.find('.sidenav-menu').first();

			const sidenavRight = instance._isSidenavRight();
			const width = instance._getSidenavWidth();

			const offset = width + options.gutter;

			const url = options.url;

			if (url) {
				container.one('urlLoaded.lexicon.sidenav', () => {
					instance.setHeight();
				});

				instance._loadUrl(menu, url, container);
			}

			navigation.css('width', width);
			menu.css('width', width);

			let positionDirection = options.rtl ? 'right' : 'left';

			if (sidenavRight) {
				positionDirection = options.rtl ? 'left' : 'right';
			}

			const paddingDirection = 'padding-' + positionDirection;

			const pushContentCssProperty = mobile
				? positionDirection
				: paddingDirection;
			const type = mobile ? options.typeMobile : options.type;

			if (type !== 'fixed') {
				let navigationStartX = container.hasClass('open')
					? navigation.offset().left - options.gutter
					: navigation.offset().left - offset;

				const contentStartX = content.offset().left;
				const contentWidth = content.innerWidth();

				let padding = '';

				if (
					(options.rtl && sidenavRight) ||
					(!options.rtl && options.position === 'left')
				) {
					navigationStartX = navigation.offset().left + offset;

					if (navigationStartX > contentStartX) {
						padding = navigationStartX - contentStartX;
					}
				}
				else if (
					(options.rtl && options.position === 'left') ||
					(!options.rtl && sidenavRight)
				) {
					if (navigationStartX < contentStartX + contentWidth) {
						padding =
							contentStartX + contentWidth - navigationStartX;

						if (padding >= offset) {
							padding = offset;
						}
					}
				}

				content.css(pushContentCssProperty, padding);
			}
		},

		showSimpleSidenav() {
			const instance = this;

			const options = instance.options;

			const simpleSidenavClosed = instance._isSimpleSidenavClosed();

			if (simpleSidenavClosed) {
				const content = $(options.content).first();
				const sidenav = $(options.container);

				const closedClass = options.closedClass;
				const openClass = options.openClass;

				const toggler = options.toggler;

				const url = toggler.data('url');

				if (url) {
					instance._loadUrl(sidenav, url);
				}

				sidenav.trigger({
					toggler: $(instance.togglerSelector),
					type: 'openStart.lexicon.sidenav',
				});

				instance._onSidenavTransitionEnd(content, () => {
					sidenav.removeClass('sidenav-transition');
					toggler.removeClass('sidenav-transition');

					sidenav.trigger({
						toggler: $(instance.togglerSelector),
						type: 'open.lexicon.sidenav',
					});
				});

				content
					.addClass('sidenav-transition')
					.addClass(openClass)
					.removeClass(closedClass);
				sidenav.addClass('sidenav-transition');
				toggler.addClass('sidenav-transition');

				sidenav.addClass(openClass).removeClass(closedClass);
				toggler.addClass('active').addClass(openClass);
			}
		},

		toggle() {
			const instance = this;

			if (instance.useDataAttribute) {
				instance.toggleSimpleSidenav();
			}
			else {
				instance.toggleNavigation();
			}
		},

		toggleNavigation(force) {
			const instance = this;
			const options = instance.options;

			const container = $(options.container);
			const menu = container.find('.sidenav-menu').first();
			const toggler = instance.toggler;

			const width = options.width;

			const closed =
				$.type(force) === 'boolean'
					? force
					: container.hasClass('closed');
			const sidenavRight = instance._isSidenavRight();

			const widthMethod = closed ? 'showSidenav' : 'hideSidenav';

			if (closed) {
				container.trigger({
					toggler,
					type: 'openStart.lexicon.sidenav',
				});
			}
			else {
				container.trigger({
					toggler,
					type: 'closedStart.lexicon.sidenav',
				});
			}

			instance._onSidenavTransitionEnd(container, () => {
				const menu = container.find('.sidenav-menu').first();

				if (container.hasClass('closed')) {
					instance.clearStyle(['min-height', 'height']);

					toggler
						.removeClass('open')
						.removeClass('sidenav-transition');

					container.trigger({
						toggler,
						type: 'closed.lexicon.sidenav',
					});
				}
				else {
					toggler.addClass('open').removeClass('sidenav-transition');

					container.trigger({
						toggler,
						type: 'open.lexicon.sidenav',
					});
				}

				if (instance.mobile) {
					instance._focusElement(menu);
				}
			});

			if (closed) {
				instance.setHeight();

				menu.css('width', width);

				const positionDirection = options.rtl ? 'left' : 'right';

				if (sidenavRight) {
					menu.css(positionDirection, '');
				}
			}

			container.addClass('sidenav-transition');
			toggler.addClass('sidenav-transition');

			instance[widthMethod](container);

			container
				.toggleClass('closed', !closed)
				.toggleClass('open', closed);
			toggler.toggleClass('active', closed).toggleClass('open', closed);
		},

		toggleSimpleSidenav() {
			const instance = this;

			const simpleSidenavClosed = instance._isSimpleSidenavClosed();

			if (simpleSidenavClosed) {
				instance.showSimpleSidenav();
			}
			else {
				instance.hideSimpleSidenav();
			}
		},

		visible() {
			const instance = this;

			let closed;

			if (instance.useDataAttribute) {
				closed = instance._isSimpleSidenavClosed();
			}
			else {
				const container = $(instance.options.container);

				closed = container.hasClass('sidenav-transition')
					? !container.hasClass('closed')
					: container.hasClass('closed');
			}

			return !closed;
		},
	};

	const old = $.fn.sideNavigation;

	const initialize = function (toggler, options, selector) {
		let data = toggler.data('lexicon.sidenav');

		if (!data) {
			if (!options) {
				options = {};
			}

			options.selector = selector;

			data = new SideNavigation(toggler, options);

			toggler.data('lexicon.sidenav', data);
		}

		return data;
	};

	const Plugin = function (options) {
		const instance = this;

		const selector = instance.selector;

		let retVal = instance;
		const methodCall = typeof options === 'string';
		const returnInstance = options === 'instance';
		const args = $.makeArray(arguments).slice(1);

		if (methodCall) {
			this.each(function () {
				const $this = $(this);

				const data = $this.data('lexicon.sidenav');

				if (data) {
					if (returnInstance) {
						retVal = data;

						return false;
					}

					let methodRetVal;

					if (
						$.isFunction(data[options]) &&
						options.indexOf('_') !== 0
					) {
						methodRetVal = data[options].apply(data, args);
					}

					if (methodRetVal !== data && methodRetVal !== undefined) {
						if (methodRetVal.jquery) {
							retVal = retVal.pushStack(methodRetVal.get());
						}
						else {
							retVal = methodRetVal;
						}

						return false;
					}
				}
				else if (returnInstance) {
					retVal = null;

					return false;
				}
			});
		}
		else {
			this.each(function () {
				initialize($(this), options, selector);
			});
		}

		return retVal;
	};

	Plugin.noConflict = function () {
		$.fn.sideNavigation = old;

		return this;
	};

	/**
	 * Plugin options
	 * @property {String|Number}  breakpoint   The window width that defines the desktop size.
	 * @property {String}         content      The class or ID of the content container.
	 * @property {String}         container    The class or ID of the sidenav container.
	 * @property {String|Number}  gutter       The space between the sidenav-slider and the sidenav-content.
	 * @property {String|Boolean} equalHeight  The height of content and navigation should be equal. This is deprecated.
	 * @property {String}         heightType   Calculates the height of sidenav when type is relative. Possible values: `fullHeight`, `equalHeight`
	 * @property {String}         navigation   The class or ID of the navigation container.
	 * @property {String}         position     The position of the sidenav-slider. Possible values: left, right
	 * @property {String}         type         The type of sidenav in desktop. Possible values: relative, fixed, fixed-push
	 * @property {String}         typeMobile   The type of sidenav in mobile. Possible values: relative, fixed, fixed-push
	 * @property {String|Boolean} useDelegate  The type of reference to use on the toggler event handler. Value false, directly binds click to the toggler.
	 * @property {String|Object}  url          The URL or $.ajax config object to fetch the content to inject into .sidebar-body
	 * @property {String|Number}  width        The width of the side navigation.
	 */

	Plugin.defaults = {
		breakpoint: 768,
		content: '.sidenav-content',
		equalHeight: true, // equalHeight option is deprecated, use heightType instead
		gutter: '0px',
		heightType: null,
		loadingIndicatorTPL:
			'<div class="loading-animation loading-animation-md"></div>',
		navigation: '.sidenav-menu-slider',
		position: 'left',
		type: 'relative',
		typeMobile: 'relative',
		url: null,
		useDelegate: true,
		width: '225px',
	};

	Plugin.Constructor = SideNavigation;

	$.fn.sideNavigation = Plugin;

	$(() => {
		const sidenav = $('[data-toggle="sidenav"]');

		Plugin.call(sidenav);
	});
})(jQuery);
