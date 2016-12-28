<%@ page import="ru.alcereo.pairlearning.Service.models.UserFront" %><%--
  Created by IntelliJ IDEA.
  User: alcereo
  Date: 23.12.16
  Time: 17:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Сервис парного обучения</title>
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/utils.js"></script>
</head>
<body>

<h3>Главная страница сервиса парного обучения</h3>

<% if (request.getAttribute("user")==null) {%>
<p></p>
<a href="/registration">Регистрация</a>

<p></p>

<form id="authorization">
    <input placeholder="Логин" type="text" id="login">
    <input placeholder="Пароль" type="text" id="password">
    <input type="submit" value="Вход">
</form>

<%}else{%>
<p></p>
Вы зашли, как: <%=((UserFront)request.getAttribute("user")).getName()%>
<p></p>
<form action="${pageContext.request.contextPath}/users/api" method="post">
    <input name="action" value="exit" hidden>
    <input type="submit" value="Выход">
</form>


<%}%>

<script type='text/javascript'>

    $("#authorization").submit(function (event) {

        event.preventDefault();
        $.post(
            "/users/api",
            {
                action:"auth",
                login: $('#login').val(),
                passwordHash: SHA1($('#password').val())

            }).done(function (data) {

            window.location.replace("/usercabinet");

        }).fail(function (data) {

            console.log('fail');

        });
    });

</script>

</body>
</html>
