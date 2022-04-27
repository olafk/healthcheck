<%@page import="com.liferay.portal.health.api.HealthcheckItem"%>
<%@page import="java.util.List"%>
<%@ include file="./init.jsp" %>

<p>
	<b><liferay-ui:message key="healthcheckweb.caption"/></b>
</p>

<ul>
<% List<HealthcheckItem> checks = (List<HealthcheckItem>) renderRequest.getAttribute("checks");
	
	for(HealthcheckItem check: checks) {
		String style = check.isResolved()? "" : " style=\"font-weight:bold;\"";
%>
<li <%=style %>>
<%=check.isResolved() %>, <%=check.getCategory() %>, <%=check.getMessage() %>
<%
	if(check.getLink() != null) {
		out.write(" (<a href=\"" + check.getLink() + "\">hint</a>)");
	} else {
		out.write(" (no hint)");
	}

%>
</li>
<%		
	}
%>
</ul>