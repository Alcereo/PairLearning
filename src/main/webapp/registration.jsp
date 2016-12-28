<%@ page import="ru.alcereo.pairlearning.Service.models.UserFront" %><%--
  Created by IntelliJ IDEA.
  User: alcereo
  Date: 23.12.16
  Time: 18:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Регистрация нового пользователя</title>
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script src="<%=request.getContextPath()%>/js/utils.js"></script>
</head>
<body>

<% if (request.getAttribute("user")==null) {%>

Регистрация нового пользователя

<form id="registration">
    <h4>
        Введите регистрационные данные
    </h4>
    <p></p>
    <input placeholder="Логин" type="text" id="login">
    <p></p>
    <input placeholder="Имя" type="text" id="name">
    <p></p>
    <input placeholder="Пароль" type="text" id="password">
    <p></p>
    <input placeholder="e-mail" type="text" id="mail">
    <p></p>
    <input type="submit" value="Регистрация">
</form>

<form id="confirmation" hidden="true">

    <h4>
        Введите код подтверждения
    </h4>
    <p></p>
    Код подтверждения был отправлен на указанный адрес электронной почты
    <p></p>
    <input placeholder="Код подтверждения" type="text" id="code">
    <p></p>
    <input type="submit" value="Подтвердить">

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

    $("#registration").submit(function (event) {

        event.preventDefault();

        $.post(
            "/registration/api",
            {
                action:"registration",
                login: $('#login').val(),
                name: $('#name').val(),
                passwordHash: SHA1($('#password').val()),
                email: $('#mail').val()
            }
        ).done(function (data) {

            $('#registration').hide();
            $('#confirmation').show();

        }).fail(function (data) {

            console.error(data);
        });

    });

    $("#confirmation").submit(function (event) {

        event.preventDefault();

        $.post(
            "/registration/api",
            {
                action: "confiramtion",
                code: $('#code').val()
            }
        ).done(function (data) {

            window.location.replace("/usercabinet");

        }).fail(function (data) {

            console.error(data);
        });

    });

</script>

</body>
</html>
