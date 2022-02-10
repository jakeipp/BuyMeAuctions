package com.cs336.pkg;

/**
 * Nav - Contains the navigation for the BuyMe site.
 */
public class Nav {
	
	/** The user. */
	private String user;
	
	/**
	 * Instantiates a new nav.
	 *
	 * @param user client username
	 */
	public Nav (String user) {
		this.user = user;
	}
	
	/**
	 * To string.
	 *
	 * @return Applicable navigation options encoded in HTML
	 */
	@Override
	public String toString() {
		if (user != null)
			return (
    				  "<li class=\"dropdown\"><a href=\"MyAuctions\">My Auctions</a>"
    				+ "	<div class=\"dropdown-content\">"
    				+ "		<a class=\"dropdown-item\" href=\"WonAuctions\">Won</a>"
    				+ "		<a class=\"dropdown-item\" href=\"CreateAuction\">Create</a>"
    				+ "		<a class=\"dropdown-item\" href=\"WatchList\">Watchlist</a>"
    				+ "	</div>"
    				+ "</li>" 
    				+ "<li><label for=\"messages-toggle\"><a>Messages</a></label></li>" 
    				+ "<li class=\"dropdown\"><a style=\"min-width: 10ch;\" href=\"#\">User: " + user + "</a>"
    				+ "	<div class=\"dropdown-content\">"
    				+ "		<a class=\"dropdown-item\" href=\"Logout\">Logout</a>"
    				+ "	</div>"
    				+ "</li>" 
    	            
    				);
		else 
			return (
    				"<li><a href=\"Login\">Login</a></li>" +
    	            "<li><a href=\"SignUp\">Sign Up</a></li>"
    				);
		
	}
}
