<%--
  Created by IntelliJ IDEA.
  User: alcereo
  Date: 28.12.16
  Time: 2:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Ошибка работы сервиса</title>
</head>
<body>
    <h3>К сожалению произошла ошибка.</h3>

    <p>
        <%=request.getAttribute("errorDescription")%>
    </p>
</body>
</html>
