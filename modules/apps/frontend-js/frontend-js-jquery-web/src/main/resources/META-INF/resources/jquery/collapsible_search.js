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
	const CollapsibleSearch = function (element) {
		const instance = this;

		instance.$element = $(element);
		instance.$close = instance.$element.find('.basic-search-close');
		instance.$input = instance.$element.find('input[type="text"]');
		instance.$submit = instance.$element.find('[type="submit"]');

		instance.$close.on(
			'click.lexicon.close.collapsible-search',
			$.proxy(instance.close, instance)
		);
		instance.$input.on(
			'blur.lexicon.collapsible-search',
			$.proxy(instance.blur, instance)
		);
		instance.$input.on(
			'focus.lexicon.collapsible-search',
			$.proxy(instance.focus, instance)
		);
		instance.$submit.on(
			'click.lexicon.submit.collapsible-search',
			$.proxy(instance.submit, instance)
		);
	};

	CollapsibleSearch.BREAKPOINT = 768;
	CollapsibleSearch.TRANSITION_DURATION = 500;

	CollapsibleSearch.prototype = {
		blur(event) {
			const $input = $(event.currentTarget);

			$input.closest('.basic-search').removeClass('focus');
		},

		close(event) {
			const instance = this;

			const basicSearch = $(event.currentTarget).closest('.basic-search');

			const basicSearchSlider = basicSearch.find('.basic-search-slider');
			const basicSearchSubmit = basicSearch.find('[type="submit"]');

			const complete = function () {
				basicSearch.removeClass('basic-search-transition');

				basicSearch.trigger('closed.lexicon.collapsible.search');
			};

			const supportsTransition = bootstrap.Util.supportsTransitionEnd();

			if (supportsTransition) {
				basicSearchSlider
					.one('bsTransitionEnd', $.proxy(complete, instance))
					.emulateTransitionEnd(
						CollapsibleSearch.TRANSITION_DURATION
					);
			}

			basicSearch.addClass('basic-search-transition').removeClass('open');

			if (!supportsTransition) {
				complete.call(instance);
			}
			else {
				basicSearchSubmit.focus();
			}
		},

		destroy() {
			const instance = this;

			instance.$close.off('click.lexicon.close.collapsible-search');
			instance.$input.off('blur.lexicon.collapsible-search');
			instance.$input.off('focus.lexicon.collapsible-search');
			instance.$submit.off('click.lexicon.submit.collapsible-search');
		},

		focus(event) {
			$(event.currentTarget).closest('.basic-search').addClass('focus');
		},

		submit(event) {
			const instance = this;

			if (window.innerWidth < CollapsibleSearch.BREAKPOINT) {
				const basicSearch = $(event.currentTarget).parents(
					'.basic-search'
				);

				const basicSearchInput = basicSearch.find('input[type="text"]');
				const basicSearchSlider = basicSearch.find(
					'.basic-search-slider'
				);

				const complete = function () {
					basicSearch.removeClass('basic-search-transition');
					basicSearchInput.focus();

					basicSearch.trigger('open.lexicon.collapsible.search');
				};

				if (!basicSearch.hasClass('open')) {
					event.preventDefault();

					const supportsTransition = bootstrap.Util.supportsTransitionEnd();

					if (supportsTransition) {
						basicSearchSlider
							.one('bsTransitionEnd', $.proxy(complete, instance))
							.emulateTransitionEnd(
								CollapsibleSearch.TRANSITION_DURATION
							);
					}

					basicSearch
						.addClass('basic-search-transition')
						.addClass('open');

					if (!supportsTransition) {
						complete.call(instance);
					}
				}
			}
		},
	};

	const Plugin = function (option) {
		return this.each(function () {
			const $this = $(this);

			let data = $this.data('lexicon.collapsible-search');

			if (!data) {
				data = new CollapsibleSearch(this);

				$this.data('lexicon.collapsible-search', data);
			}

			if (typeof option === 'string') {
				data[option]();
			}
		});
	};

	const old = $.fn.collapsibleSearch;

	$.fn.collapsibleSearch = Plugin;
	$.fn.collapsibleSearch.Constructor = CollapsibleSearch;

	$.fn.collapsibleSearch.noConflict = function () {
		$.fn.collapsibleSearch = old;

		return this;
	};

	const close = '[data-toggle="collapsible-search"] .basic-search-close';
	const input = '[data-toggle="collapsible-search"] input[type="text"]';
	const submit = '[data-toggle="collapsible-search"] [type="submit"]';

	$(document)
		.on(
			'blur.lexicon.collapsible-search.data-api',
			input,
			$.proxy(CollapsibleSearch.prototype.blur, CollapsibleSearch)
		)
		.on(
			'click.lexicon.close.collapsible-search.data-api',
			close,
			$.proxy(CollapsibleSearch.prototype.close, CollapsibleSearch)
		)
		.on(
			'click.lexicon.submit.collapsible-search.data-api',
			submit,
			$.proxy(CollapsibleSearch.prototype.submit, CollapsibleSearch)
		)
		.on(
			'focus.lexicon.collapsible-search.data-api',
			input,
			$.proxy(CollapsibleSearch.prototype.focus, CollapsibleSearch)
		);
})(jQuery);
