<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
const i18n = [];
<c:choose>
    <c:when test="${pageContext.request.servletPath == '/WEB-INF/jsp/users.jsp'}">
        i18n["addTitle"] = '<spring:message code="user.add"/>';
        i18n["editTitle"] = '<spring:message code="user.edit"/>';
    </c:when>
    <c:otherwise>
        i18n["addTitle"] = '<spring:message code="meal.add"/>';
        i18n["editTitle"] = '<spring:message code="meal.edit"/>';
    </c:otherwise>
</c:choose>

<c:forEach var="key"
           items='<%=new String[]{"common.deleted","common.saved","common.enabled","common.disabled","common.errorStatus","common.confirm"}%>'>
    i18n["${key}"] = "<spring:message code="${key}"/>";
</c:forEach>