<%@ page import="java.sql.*" contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cs336.pkg.*" %>
<%@ page import="java.util.*" %>

<html>

<head>
    <link rel="stylesheet" href="css/main.css" type="text/css"/>
    <title>Buy Me</title>
</head>

<header>
    <h1 class="logo"><a href="/BuyMe">BuyMe</a></h1>
    <input type="checkbox" id="messages-toggle" class="messages-toggle">
    <nav>
        <ul>
			<li><a href="Login">Login</a></li>
			<li><a href="SignUp">Sign Up</a></li>
        </ul>
    </nav>
    <form class="messages-container" method="get" action="MessageAction">
        <div class="message">
        	<h2 class="subject">
        		Please Sign in to View Messages
        	</h2>
        </div>
    </form>
</header>


<body>
    <div class="content">
        <h1>Successfully Logged Out.</h1>
    </div>
</body>

</html>
