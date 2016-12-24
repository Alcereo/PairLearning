<%@ page import="ru.alcereo.pairlearning.Service.SessionProvider" %><%--
  Created by IntelliJ IDEA.
  User: alcereo
  Date: 23.12.16
  Time: 18:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Личный кабинет пользователя</title>
</head>
<body>

<%if (SessionProvider.validateSession(request.getSession().getId())){%>
    <h3>Личный кабинет пользователя</h3>
    Привет, <%=SessionProvider.getCurrentUser(request.getSession().getId()).getName()%>!

<a href="/ChatRoom.jsp">Чат</a>
<%}else{%>
    Требуется авторизация!
    <a href="/">Гланая страница</a>
<%}%>

</body>
</html>
