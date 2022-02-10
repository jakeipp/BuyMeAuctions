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
	<!-- Script for Sign Up form validation -->
	<script>
	    function validatePassword(form) {
	        password1 = form.password.value;
	        password2 = form.confirm_pass.value;
	        if (password1 != password2) {
	            alert ("\nPasswords do not match");
	            return false;
	        }
	        return true;
	    }
	</script>
	
	<body>
	    <div class="content">
	
	
	        <h1>Create an Account</h1>
	
	        <%
	            //Check the message if the user was taken, if it is inform the user
	        	String error = request.getParameter("msg");
	    		if(error != null && error.equals("userTaken"))
	    			out.print("<p>Username taken, please enter a different one.<p>");
	        %>
	
	        <form method="post" onSubmit="return validatePassword(this)" action="SignUp">
	            <span>Name</span><input type="text" name="name" required>
	            <span>Date of Birth</span><input type="date" name="dob" required>
	            <span>Email</span><input type="text" name="email" required>
	            <span>Username</span><input type="text" name="username" required>
	            <span>Password</span><input type="password" name="password" required>
	            <span>Confirm Password</span><input type="password" name="confirm_pass" required>
	            <input type="submit" value="Create Account">
	        </form>
	
	
	    </div>
	</body>

</html>
