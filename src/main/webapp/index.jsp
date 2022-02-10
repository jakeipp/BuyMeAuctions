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
	<!-- Filtering -->
	<%
		AuctionQuery stype = null;
		CreateAuction ca = null;
		String command = "";
		String table = request.getParameter("table");
		if (table == null || table.equals("Any"))
			table = null;
		String seller = request.getParameter("seller_username");
		boolean sellerCheck = seller != null && !seller.equals("");
		
		String title = request.getParameter("title");
		boolean titleCheck = title != null && !title.equals("");
		
		String closed = request.getParameter("closed");
		boolean isClosed = closed != null && closed.equals("on");
		
		
		
		boolean where = false;
		if (!isClosed) {
			command += "WHERE final_price = -1 ";
			where = true;
		}
		if (sellerCheck) {
			if (!where) {
				command += "WHERE seller_username like \'%" + seller + "%\' ";
				where = true;
			}
			else {
				command += "AND seller_username like \'%" + seller + "%\' ";
			}
		}
		if (titleCheck) {
			if (!where) {
				command += "WHERE title like \'%" + title + "%\' ";
				where = true;
			}
			else {
				command += "AND title like \'%" + title + "%\' ";
			}
		}
		
		AuctionQuery standard = new AuctionQuery(command);
		
		if (table != null) {
			ca = new CreateAuction();
			
			for(String field : ca.fields(table)) {
				String param = request.getParameter(field);
				if (!(field.equals("item_id") || field.equals("seller_username") || field.equals("title")) && param != null && !param.equals("Any"))
					if(where) command += "AND " + field + " like \'%" + param + "%\' ";
					else { command += "WHERE " + field + " like \'%" + param + "%\' "; where = true; }
			}
			stype = new AuctionQuery(table, command);
		}
	%>
	
	<body>
		<div class="wrapper">
			<div class="filter-spacer">
				<div class="filter-pane">
					<h2>Filter / Sort: </h2>
					<form method="get" action="" class="filter-form">
						<span>Seller</span>
						<input type="text" name="seller_username" <% if (sellerCheck) out.print("value=\""+ seller + "\""); %>>
						<span>Item Keyword</span>
						<input type="text" name="title" <% if (titleCheck) out.print("value=\""+ title + "\""); %>>
						<span>Item Type</span>
						<select name="table">
							<option>Any</option>
							<%
								for (String type : standard.itemTypes())
									out.print("<option value=\"" + type + "\"" + (type.equals(table) ? " selected=\"selected\"" : "") + ">" + type.substring(5) + "</option>");
							%>
						</select>
						<label for="closed">Show Closed Auctions</label>
						<input type=checkbox name="closed" id="closed" <% if(isClosed) out.print("checked"); %>>
						<%
							if (table != null)
								out.print(ca.createFilter(table));
						%>
						<input type="submit" value="Go">
					</form>
				</div>
			</div>
			<div class="content">
		        <h1>Welcome to BuyMe!</h1>
		        <%
		        	
		        	
		        	
		        	if (stype != null)
		        		out.print(stype);
		        	else
		        		out.print(standard);
		        %>
	    	</div>
		</div>
	    
	</body>
	
	<!-- JS for Auction Accordions -->
	<script type="text/javascript" src="js/auctionAcc.js"></script>
	
</html>
