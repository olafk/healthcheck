<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.health.api.HealthcheckItem"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@ include file="./init.jsp"%>
<div class="container-fluid container-fluid-max-xl sheet" style="">
	<p>
		<b><liferay-ui:message key="healthcheckweb.caption" /></b>
	</p>
	<p>
		<liferay-ui:message key="healthcheckweb.cta" />
	</p>
	<%
int failedChecks = (int) renderRequest.getAttribute("failedChecks");
int succeededChecks = (int) renderRequest.getAttribute("succeededChecks");
int ignoredChecks = (int) renderRequest.getAttribute("ignoredChecks");
Set<String> theIgnoredChecks = (Set<String>) renderRequest.getAttribute("the-ignored-checks");
List<HealthcheckItem> checks = (List<HealthcheckItem>) renderRequest.getAttribute("checks");
%>

	<div
		class="row align-items-lg-start align-items-sm-start align-items-start align-items-md-start flex-lg-row flex-sm-row flex-row flex-md-row">
		<div class="col col-lg-3 col-sm-12 col-12 col-md-4"></div>
		<div class="col col-lg-2 col-sm-4 col-6 col-md-2"
			style="text-align: center;">
			<div style="font-size: 48px;">
				<clay:icon symbol="exclamation-circle" />
				<br />
				<%= failedChecks %>
			</div>
			<liferay-ui:message key="failed" />
		</div>
		<div class="col col-lg-2 col-sm-4 col-6 col-md-2"
			style="text-align: center;">
			<div style="font-size: 48px;">
				<clay:icon symbol="check-circle" />
				<br />
				<%= succeededChecks %>
			</div>
			<liferay-ui:message key="succeeded" />
		</div>
		<div class="col col-lg-2 col-sm-4 col-6 col-md-2"
			style="text-align: center;">
			<div style="font-size: 48px;">
				<clay:icon symbol="folder" />
				<br />
				<%= ignoredChecks %>
			</div>
			<liferay-ui:message key="ignored" />
		</div>
		<div class="col col-lg-3 col-sm-12 col-12 col-md-4"></div>
	</div>

	<table>
		<% for(HealthcheckItem check: checks) {
		String style = check.isResolved()? "" : "font-weight:bold;";
		String symbol = check.isResolved()? "check-circle" : "exclamation-circle";
%>
		<tr style="border: 1px solid grey; <%=style %>">
			<td style="min-width: 3em; text-align: center;"><clay:icon
					symbol="<%=symbol %>" /></td>
			<td><%=check.getCategory() %></td>
			<td style="word-wrap: anywhere;"><%=check.getMessage() %></td>
			<td style="padding: 2px; word-wrap: normal;">
				<%
		if(check.getLink() != null) {
		out.write("(<a href=\"" + check.getLink() + "\" target=\"_blank\">hint</a>)");
			} else {
		out.write("(no&nbsp;hint)");
			}
	%>
			</td>
			<td style="padding: 2px;"><aui:button-row>
					<portlet:actionURL name="ignoreMessage" var="ignoreAction">
						<portlet:param name="ignore" value="<%=check.getKey()%>" />
					</portlet:actionURL>
					<aui:button onClick="<%=ignoreAction%>" value="ignore" />
				</aui:button-row></td>
		</tr>
		<%		
	}
%>
	</table>
	<% if(ignoredChecks>0) { %>
	<div style="margin-top: 2rem;">
		<portlet:actionURL name="resetIgnore" var="resetIgnoreAction" />
		<aui:button onClick="<%=resetIgnoreAction%>" value="reset-ignore" />
	</div>
	<!--
Ignored <%=ignoredChecks%> healthcheck(s):
<%
   for(String theCheck : theIgnoredChecks) {
      out.write(theCheck);
      out.write("\n");
   }
%>-->
	<%   } %>
</div>
