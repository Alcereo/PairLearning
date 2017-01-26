<%@ page import="ru.alcereo.pairlearning.Service.models.UserFront" %><%--
  Created by IntelliJ IDEA.
  User: alcereo
  Date: 23.12.16
  Time: 18:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>Личный кабинет пользователя</title>
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
</head>
<body>

    <h3>Личный кабинет пользователя</h3>

    Привет, ${user.name}!

<p></p>

<h3>Список тем:</h3>

    <table>
        <tr>
            <td> Наименование темы </td> <td> Изучить </td> <td> Рассказать </td>
        </tr>
        <c:forEach items="${topicRows}" var="row">
            <tr>
            <td> ${row.topic.title} </td>
            <td> <input type="checkbox" class="checker" id="${row.topic.id}" <c:if test="${row.learn}">checked</c:if> value="learn"> </td>
            <td> <input type="checkbox" class="checker" id="${row.topic.id}" <c:if test="${row.teach}">checked</c:if> value="teach"> </td>
            </tr>
        </c:forEach>
    </table>
<p></p>


<p></p>

<a href="${pageContext.request.contextPath}/chatroom">Чат</a>
<p></p>
<form id="logout" action="${pageContext.request.contextPath}/logout" method="post">
    <input type="submit" value="Выход">
</form>

<p></p>
<div id="error_messages"></div>

<script type='text/javascript'>

    $(".checker").click( function(event){
        event.preventDefault();

        var checker = $(this);

        $.post(
            "/topic/api/concrete",
            {
                id: this.id,
                value: this.value,
                predicate: checker.is(':checked')
            }
        ).done(function (data) {

            if (checker.is(':checked'))
                checker.prop( "checked", false );
            else
                checker.prop( "checked", true );

        }).fail(function (data) {

            console.log(data);
            writeError(data.responseText);

        });

    });

</script>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/logout.js"></script>--%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/errorWrite.js"></script>

</body>
</html>
