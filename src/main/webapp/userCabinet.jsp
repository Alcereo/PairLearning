<%@ page import="ru.alcereo.pairlearning.Service.SessionService" %>
<%@ page import="ru.alcereo.pairlearning.Service.UserFront" %>
<%@ page import="ru.alcereo.pairlearning.Service.TopicRowFront" %>
<%@ page import="ru.alcereo.pairlearning.Service.TopicService" %><%--
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
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
</head>
<body>

<%if (SessionService.validateSession(request.getSession().getId())){%>
<% UserFront user = SessionService.getCurrentUser(request.getSession().getId()); %>

    <h3>Личный кабинет пользователя</h3>

    Привет, <%=user.getName()%>!

<p></p>

<h3>Список тем:</h3>

    <table>
        <tr>
            <td> Наименование темы </td> <td> Изучить </td> <td> Рассказать </td>
        </tr>

        <% for (TopicRowFront topicRow: TopicService.getUserTopic(user)) { %>
            <tr>
                <td> <%=topicRow.getTopic().getTitle()%> </td>
                <td> <input type="checkbox" class="checker" id="<%=topicRow.getTopic().getId()%>" <%=(topicRow.isLearn() ? "checked" : "")%> value="learn"> </td>
                <td> <input type="checkbox" class="checker" id="<%=topicRow.getTopic().getId()%>" <%=(topicRow.isTeach() ? "checked" : "")%> value="teach"> </td>
            </tr>
        <%}%>
    </table>
<p></p>


<p></p>

<a href="/chatroom">Чат</a>

<%}else{%>
    Требуется авторизация!
    <a href="/">Гланая страница</a>
<%}%>


<script type='text/javascript'>

    $(".checker").click( function(event){
        event.preventDefault();

        var checker = $(this);

        $.post(
            "/topic/api",
            {
                action:"concrete",
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

            console.log('fail');

        });

    });

</script>

</body>
</html>
