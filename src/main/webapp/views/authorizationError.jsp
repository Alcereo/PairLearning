<%--
  Created by IntelliJ IDEA.
  User: alcereo
  Date: 28.12.16
  Time: 0:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Требуется авторизация</title>
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/utils.js"></script>
</head>
<body>

<h3> Требуется авторизация </h3>

<form id="authorization">
    <input placeholder="Логин" type="text" id="login">
    <input placeholder="Пароль" type="text" id="password">
    <input type="submit" value="Вход">
</form>

<p></p>
<div id="error_messages"></div>

<script type='text/javascript'>

    $("#authorization").submit(function (event) {

        event.preventDefault();
        $.post(
            "/users/api/auth",
            {
                action:"auth",
                login: $('#login').val(),
                passwordHash: SHA1($('#password').val())

            }).done(function (data) {

            window.location.replace("/usercabinet");

        }).fail(function (data) {

            console.log(data);
            writeError(data.responseText);

        });
    });

    var messages = document.getElementById("error_messages");

    function writeError(text){
        messages.innerHTML += "<br/>" + text;
    }

</script>

</body>
</html>
