
<%@ page import="ru.alcereo.pairlearning.Service.models.UserFront" %><%--
  Created by IntelliJ IDEA.
  User: alcereo
  Date: 24.12.16
  Time: 14:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Комната чата</title>
</head>
<body>

<a href="${pageContext.request.contextPath}/usercabinet">Личный кабинет</a>

<p></p>

<h3>Чат с пользователем</h3>
<p></p>

Имя в чате: ${user.name}
<p></p>
<div>
    <input type="text" id="messageinput"/>
</div>
<div>
    <button type="button" onclick="openSocket();" >Open</button>
    <button type="button" onclick="send();" >Send</button>
    <button type="button" onclick="closeSocket();" >Close</button>
</div>
<!-- Server responses get written here -->
<div id="messages"></div>


<!-- Script to utilise the ChatSocketConnection -->
<script type="text/javascript">

    var chatSocketConnection;
    var messages = document.getElementById("messages");


    function openSocket(){
        // Ensures only one connection is open at a time
        if(chatSocketConnection !== undefined && chatSocketConnection.readyState !== WebSocket.CLOSED){
            writeResponse("ChatSocketConnection is already opened.");
            return;
        }
        // Create a new instance of the websocket
        chatSocketConnection = new WebSocket("ws://localhost:8085/socket");

        /**
         * Binds functions to the listeners for the websocket.
         */
        chatSocketConnection.onopen = function(event){
            // For reasons I can't determine, onopen gets called twice
            // and the first time event.data is undefined.
            // Leave a comment if you know the answer.
            if(event.data === undefined)
                return;

            writeResponse(event.data);
        };

        chatSocketConnection.onmessage = function(event){
            writeResponse(event.data);
        };

        chatSocketConnection.onclose = function(event){
            writeResponse("Connection closed");
        };
    }

    /**
     * Sends the value of the text input to the server
     */
    function send(){
        var text = document.getElementById("messageinput").value;
        chatSocketConnection.send(text);
    }

    function closeSocket(){
        chatSocketConnection.close();
    }

    function writeResponse(text){
        messages.innerHTML += "<br/>" + text;
    }

</script>

</body>
</html>
