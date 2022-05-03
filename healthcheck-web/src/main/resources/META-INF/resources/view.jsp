<%@page import="com.liferay.portal.health.api.HealthcheckItem"%>
<%@page import="java.util.List"%>
<%@ include file="./init.jsp" %>

<p>
	<b><liferay-ui:message key="healthcheckweb.caption"/></b>
</p>
<p>
	<liferay-ui:message key="healthcheckweb.cta"/>
</p>

<table>
<% List<HealthcheckItem> checks = (List<HealthcheckItem>) renderRequest.getAttribute("checks");
	
	for(HealthcheckItem check: checks) {
		String style = check.isResolved()? "" : " style=\"font-weight:bold;\"";
%>
<tr><td <%=style %>>
<%=check.isResolved() %></td><td><%=check.getCategory() %></td><td><%=check.getMessage() %></td><td>
<%
	if(check.getLink() != null) {
		out.write(" (<a href=\"" + check.getLink() + "\">hint</a>)");
	} else {
		out.write(" (no&nbsp;hint)");
	}
%>
</td></tr>
<%		
	}
%>
</table>