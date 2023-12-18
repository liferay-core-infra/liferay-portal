<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:actionURL name="/server_admin/edit_server" var="resetMailConfiguration">
	<portlet:param name="<%= Constants.CMD %>" value="resetMail" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="preferencesCompanyId" value="0" />
</portlet:actionURL>

<div class="sheet">
	<liferay-ui:icon-menu
		cssClass="float-right"
		direction="right"
		markupView="lexicon"
		showWhenSingleIcon="<%= true %>"
	>
		<liferay-ui:icon
			message="reset-default-values"
			method="post"
			url="<%= resetMailConfiguration %>"
		/>
	</liferay-ui:icon-menu>

	<div class="panel-group panel-group-flush">
		<liferay-util:include page="/system_mail_fields.jsp" servletContext="<%= application %>" />

		<aui:button-row>
			<aui:button cssClass="save-server-button" data-cmd="updateMail" primary="<%= true %>" value="save" />
		</aui:button-row>
	</div>
</div>