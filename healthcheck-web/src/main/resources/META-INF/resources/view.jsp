<%@page import="com.liferay.portal.health.api.HealthcheckItem"%>
<%@page import="java.util.List"%>
<%@ include file="./init.jsp" %>

<p>
	<b><liferay-ui:message key="healthcheckweb.caption"/></b>
</p>

<ul>
<% List<HealthcheckItem> checks = (List<HealthcheckItem>) renderRequest.getAttribute("checks");


	for(HealthcheckItem check: checks) {
%>
<li>
<%=check.isResolved() %>, <%=check.getMessage() %>
</li>
<%		
	}
%>
</ul>