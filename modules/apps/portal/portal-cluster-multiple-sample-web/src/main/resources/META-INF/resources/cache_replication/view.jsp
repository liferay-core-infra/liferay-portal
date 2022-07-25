<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
Map<String, String> cacheEntries = (Map<String, String>)request.getAttribute(ClusterSampleWebKeys.CLUSTER_SAMPLE_CACHE_ENTRIES);

ClusterSampleData clusterSampleData = new ClusterSampleData();
%>

<p>Following data is from the server that generated this response:</p>
<ul>
	<li>
		<b>Computer Name:</b> <%= clusterSampleData.getComputerName() %>
	</li>
	<li>
		<b>Liferay Home:</b> <%= clusterSampleData.getLiferayHome() %>
	</li>
</ul>

<c:if test="<%= cacheEntries.isEmpty() %>">
	<h1>There is no entry in the cache</h1>
</c:if>

<c:if test="<%= !cacheEntries.isEmpty() %>">
	<table class="table table-autofit table-bordered table-sm">
		<thead>
			<tr>
				<th colspan="2">
					<h1>Cache Entries</h1>
				</th>
				<th>
					<liferay-portlet:actionURL name="/cluster_sample_cache_replication/edit_cache" var="removeAllCacheEntriesURL">
						<portlet:param name="<%= Constants.CMD %>" value="remove_all_cache_entries" />
					</liferay-portlet:actionURL>

					<aui:button href="<%= removeAllCacheEntriesURL %>" value="Remove All" />
				</th>
			</tr>
			<tr>
				<th class="table-head-title">CACHE KEY</th>
				<th class="table-head-title">CACHE VALUE</th>
				<th class="table-head-title">ACTION</th>
			</tr>
		</thead>

		<tbody>
			<c:forEach items="<%= cacheEntries %>" var="cacheEntry">
				<tr>
					<td>${cacheEntry.key}</td>
					<td>${cacheEntry.value}</td>
					<td>
						<aui:button-row>
							<liferay-portlet:actionURL name="/cluster_sample_cache_replication/edit_cache" var="removeCacheEntryURL">
								<portlet:param name="currentKey" value="${cacheEntry.key}" />
								<portlet:param name="<%= Constants.CMD %>" value="remove_cache_entry" />
							</liferay-portlet:actionURL>

							<aui:button href="<%= removeCacheEntryURL %>" value="remove" />
						</aui:button-row>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>

<liferay-portlet:actionURL name="/cluster_sample_cache_replication/edit_cache" var="putCacheEntryURL">
	<portlet:param name="<%= Constants.CMD %>" value="put_cache_entry" />
</liferay-portlet:actionURL>

<aui:form action="<%= putCacheEntryURL %>" method="post" name="fm">
	<aui:input label="Cache key" name="key" required="<%= true %>" type="text" />
	<aui:input label="Cache value" name="value" required="<%= true %>" type="text" />

	<aui:button-row>
		<aui:button type="submit" value="Put/Update Cache" />
	</aui:button-row>
</aui:form>