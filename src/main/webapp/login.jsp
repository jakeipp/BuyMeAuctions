<%@ page import="java.sql.*" contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cs336.pkg.*" %>
<%@ page import="java.util.*" %>

<html>

	<head>
	    <link rel="stylesheet" href="css/main.css" type="text/css"/>
	    <title>Buy Me - Login</title>
	</head>
	
	<header>
	    <h1 class="logo"><a href="/BuyMe">BuyMe</a></h1>
	    <input type="checkbox" id="messages-toggle" class="messages-toggle">
	    <nav>
	        <ul>
				<%
	            	//Display appropriate nav options for user
	            	out.print(new Nav((String)session.getAttribute("USERNAME")));
	            %>
	        </ul>
	    </nav>
	    <form class="messages-container" method="get" action="MessageAction">
	        <%
	        	//If user is signed in, display messages
	            if(session.getAttribute("USERNAME") != null)
	            	out.print(new Messages((String)session.getAttribute("USERNAME")));
	            else
	            	out.print("<div class=\"message\"><h2 class=\"subject\">Please Sign in to View Messages</h2></div>");
	        %>
	    </form>
	</header>
	
	<!-- JS for Message Accordions -->
	<script type="text/javascript" src="js/messageAcc.js"></script>
	
	<body>
	
	    <%
	        //Check if user is already logged in and redirect them to welcome page if they are
	        if(session.getAttribute("USERNAME") != null)
	            response.sendRedirect("Welcome");
	    %>
	    <div class="content">
	
	
	   		<%
	            //Check if msg is success, if it is the account was created successfully
	   			String msg = request.getParameter("msg");
	   			if (msg != null && msg.equals("success"))
	   				out.print("<h1>Account Created Successfully!</h1>");
	   		%>
	
	        <h1>Login</h1>
	
	        <%
	            //Check if the message is invalid, if it is the username or password is invalid
	        	if (msg != null && msg.equals("invalid"))
					out.print("<p>Invalid Username / Password</p>");
	        %>
	
	        <form method="post" action="Login">
	            <span>Username </span><input type="text" name="username" required>
	            <span>Password </span><input type="password" name="password" required>
	            <input type="submit" value="Login">
	        </form>
	
	
	    </div>
	</body>

</html>
