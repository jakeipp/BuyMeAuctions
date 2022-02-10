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
	            	//Display appropriate options for user
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
	        <%
		        AuctionQuery query = new AuctionQuery(Integer.parseInt((String)request.getParameter("auction")));
				String user = (String)session.getAttribute("USERNAME");
	        	Auction focus = null;
	        	for (ArrayList<Auction> auctionList : query.auctions()) {
	        		int i = 0;
	        		for (Auction auction : auctionList) {
	        			if (i > 0)
	        				focus = auction;
	        			i++;
	        		}
	        	}
	        	if (focus != null) {
	        		out.print(focus);
	        		String msg = request.getParameter("msg");
	        		if (msg != null) {
	        			if(msg.equals("success"))
        					out.print("<h3>Success to bid</h3>");
        				if(msg.equals("invalidLogin"))
        					out.print("<h3>Please Login to Bid</h3>");
        				if(msg.equals("insufficientAmt"))
        						out.print("<h3>Insufficient Bid Amount, Please bid higher</h3>");
        				if(msg.contains("reccurSet"))
        						out.print("<h3>You already have a reccuring bid setup with a max of $" + msg.substring(9) + "</h3>");
        				if(msg.equals("invalidInc"))
        						out.print("<h3>Invalid Increment!</h3>");
        				if(msg.equals("sameUser"))
    						out.print("<h3>You can't bid on your own auction.</h3>");
	        		}
	        		out.print(WatchList.watchForm(user, focus.auction_id()));
	        		out.print(Bid.history(user, focus.auction_id()));
	        	}
	        %>
	    </div>
	</body>
	
	<!-- JS for Auction Accordions -->
	<script type="text/javascript" src="js/auctionAcc.js"></script>
	
</html>
