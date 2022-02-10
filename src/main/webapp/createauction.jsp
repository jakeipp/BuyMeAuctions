<%@ page import="java.sql.*" contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cs336.pkg.*" %>
<%@ page import="java.util.*" %>

<html>

	<head>
	    <link rel="stylesheet" href="css/main.css" type="text/css"/>
	    <title>Buy Me - SignUp</title>
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
	    <div class="content">
	    
	    	<h1>Create an Auction<br>
	    	<%
	    		CreateAuction auc = new CreateAuction();
				String type = request.getParameter("type");
				
				
	    	%>
	
	        </h1>
			<% 
				out.print(auc.typeSelector(type));
				out.print(auc.createForm(type));
			%>
	
	        
	        
	
	
	    </div>
	</body>

</html>
