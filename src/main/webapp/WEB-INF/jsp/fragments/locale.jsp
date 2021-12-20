<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<div class="dropdown show">
    <a class="btn btn-secondary dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown"
       aria-haspopup="true" aria-expanded="false">
        ${pageContext.response.locale}
    </a>
    <div class="dropdown-menu" aria-labelledby="dropdownMenuLink">
        <a class="dropdown-item" href="${param.page}?locale=en">English</a>
        <a class="dropdown-item" href="${param.page}?locale=ru">Русский</a>
    </div>
</div>

<script type="text/javascript">
    let localeCode = "${pageContext.response.locale}";
</script>