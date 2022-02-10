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
	        //Check if the user is logged in, if not then redirect to login page
	        if(session.getAttribute("USERNAME") == null)
	            response.sendRedirect("login.jsp");
	    %>
	
	    <div class="content">
	
	
	        <h1>Welcome, 
	
	        <%
	            //Ouput the users name and privilege level
	        	if (session.getAttribute("ADMIN") != null)
	        		out.print("Administrator: ");
	       		if (session.getAttribute("REP") != null)
	    			out.print("Customer Service Rep: ");
	            if (session.getAttribute("NAME") != null)
	                out.print(session.getAttribute("NAME"));
	        %>
	
	        </h1>
	
	
	    </div>
	</body>

</html>
