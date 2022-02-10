package com.cs336.pkg;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Bid - Servlet and general purpose class with methods relating to operations on bids.
 */
@WebServlet("/Bid")
public class Bid extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
   
	/**
	 * Do get.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String address = "index.jsp";
		
        request.getRequestDispatcher(address).forward(request, response);
	}
	
	/**
	 * Do post.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String user = (String)session.getAttribute("USERNAME");
		String auction = request.getParameter("auction");
		String amountStr = request.getParameter("amount");
		String isReccuringStr = request.getParameter("isReccuring");
		String max_bidStr = request.getParameter("max_bid");
		int aid = Integer.parseInt(auction);
		int amount = Integer.parseInt(amountStr);
		boolean isReccuring = Boolean.parseBoolean(isReccuringStr);
		int max_bid = Integer.parseInt(max_bidStr);
		String message = "invalidLogin";
		if (user != null && auction != null && amountStr != null)
			message = createBid (user, aid, amount, isReccuring, max_bid);
		response.sendRedirect("Auction?auction=" + aid + "&msg=" + message);

		
		
		
	}

	/**
	 * Creates a bid.
	 *
	 * @param user buyer username
	 * @param aid auction id
	 * @param amount amount of the bid
	 * @param isReccuring is recurring
	 * @param max_bid max bid
	 * @return the message to be passed to the webpage
	 */
	public static String createBid (String user, int aid, int amount, boolean isReccuring, int max_bid) {
		try 
		{
			int max = getRecurring(user,aid);
			if (max > 0)
				return "reccurSet" + max;
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();
			ResultSet result;
			int price = currentPrice(aid);
			String checkBid = "SELECT seller_username, bid_inc FROM auction WHERE auction_id = " + aid;
			result = stmt.executeQuery(checkBid);
			result.next();
			int inc = result.getInt(2);
			if (result.getString(1).equals(user))
				return "sameUser";
			if ((amount - price) % inc != 0)
				return "invalidInc";
			if (amount <= price)
				return "insufficientAmt";
			
			String last = "SELECT buyer_username, bid_amount FROM bids WHERE auction_id = " + aid 
						+ " AND isReccuring = FALSE order by bid_amount DESC";
			result = stmt.executeQuery(last);
			if(result.next() && !result.getString("buyer_username").equals(user))
				Messages.createMessage(result.getString("buyer_username"), aid, "You have been outbid!"
						, "Someone has placed a higher bid than you for $" + amount + " on the item " + AuctionQuery.itemName(aid));
			
			String insBid = "INSERT INTO bids (buyer_username, auction_id, bid_amount, isReccuring, max_bid) VALUE (?, ?, ?, ?, ?)";
			
			PreparedStatement ps = con.prepareStatement(insBid);
			ps.setString(1, user);
			ps.setInt(2, aid);
			ps.setInt(3, amount);
			ps.setBoolean(4, isReccuring);
			ps.setInt(5, max_bid);
			
			ps.executeUpdate();
			con.close();
			
			updateRecBids(aid, inc);
			
			return "success";
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return "failure";
		}
	}
	
	/**
	 * Update recurring bids.
	 *
	 * @param aid auction id
	 */
	/*
	 * function updates recurring bids in an auction.
	 */
	private static void updateRecurringBids(int aid) {
		try {
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			
			//obtain bid_inc from the auction
			String checkBid = "SELECT bid_inc FROM auction WHERE auction_id = " + aid;
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = stmt.executeQuery(checkBid);
			rs.next();
			int bid_inc = rs.getInt("bid_inc");
			
			/*
			 * obtain all bids lower than the largest max_bid
			 * update them so that they all equal to their max
			 * since the largest max build will be at least one increment larger, 
			 * all bids with a lower max will be set to that max
			 * also message those that have changed
			 */
			String msgBidsLow = "SELECT DISTINCT buyer_username "
					+ "FROM bids "
					+ "WHERE isReccuring = true "
					+ "AND auction_id = " + aid + " "
					+ "AND bid_amount < max_bid "
					+ "AND max_bid < (SELECT MAX(max_bid) "
									+ "FROM bids "
									+ "WHERE isReccuring = true "
									+ "AND auction_id = " + aid + ")";
			rs = stmt.executeQuery(msgBidsLow);
			while(rs.next()) {
				Messages.createMessage(rs.getString("buyer_username"), aid, "Outbid in Auction", "description");
			}
			
			String reccBidsLow = "UPDATE bids "
					+ "SET bid_amount = max_bid "
					+ "WHERE isReccuring = true "
					+ "AND auction_id = " + aid + " "
					+ "AND max_bid < (SELECT MAX(max_bid) "
									+ "FROM (SELECT auction_id, isReccuring, max_bid FROM bids "
											+ "WHERE auction_id = " + aid + " "
											+ "AND isReccuring = true) as t)";
			PreparedStatement preparedStmt = con.prepareStatement(reccBidsLow);
			preparedStmt.executeUpdate();
			
			//obtain the largest bid in the auction
			String checkMaxBid = "SELECT buyer_username, bid_amount, auction_id, bid_time "
							+ "FROM bids "
							+ "WHERE auction_id = " + aid + " "
							+ "AND bid_amount = (SELECT MAX(bid_amount) FROM bids "
												+ "WHERE auction_id = " + aid + ")";
			rs = stmt.executeQuery(checkMaxBid);
			rs.next();
			int max_bid_amount = rs.getInt("bid_amount");
			String max_bid_user = rs.getString("buyer_username");
			
			/*
			 * get the maximum bids, and set them to either the largest bid + bid_inc
			 * or to its max, depending on which is smaller
			 */
			String highest_max_bid_query = "SELECT MAX(max_bid) AS max FROM bids WHERE auction_id = " + aid + " AND isReccuring = true";
			rs = stmt.executeQuery(highest_max_bid_query);
			rs.next();
			int final_bid = Math.min(max_bid_amount + bid_inc, rs.getInt("max"));
			//message the highest bid or the highest recurring max bids that they have been outbid
			if(max_bid_amount + bid_inc < rs.getInt("max")) {
				Messages.createMessage(max_bid_user, aid, "Outbid in Auction", "description");
			}else {
				String msgBidsHigh = "SELECT DISTINCT buyer_username "
						+ "FROM bids "
						+ "WHERE isReccuring = true "
						+ "AND auction_id = " + aid + " "
						+ "AND bid_amount < max_bid "
						+ "AND max_bid = (SELECT MAX(max_bid) "
										+ "FROM bids "
										+ "WHERE isReccuring = true "
										+ "AND auction_id = " + aid + ")";
				rs = stmt.executeQuery(msgBidsHigh);
				while(rs.next()) {
					Messages.createMessage(rs.getString("buyer_username"), aid, "Outbid in Auction", "description");
				}
			}
			
			//update highest recurring max_bid's
			String reccBidsHigh = "UPDATE bids "
					+ "SET bid_amount = " + final_bid + " "
					+ "WHERE isReccuring = true "
					+ "AND auction_id = " + aid + " "
					+ "AND max_bid = (SELECT MAX(max_bid) "
									+ "FROM (SELECT auction_id, isReccuring, max_bid FROM bids "
											+ "WHERE auction_id = " + aid + " "
											+ "AND isReccuring = true) as t)";
			preparedStmt = con.prepareStatement(reccBidsHigh);
			preparedStmt.executeUpdate();
			
			con.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update recurring bids.
	 *
	 * @param aid auction id
	 * @param inc bid increment
	 */
	private static void updateRecBids(int aid, int inc) {
		try 
		{
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();
			String getRecBids = "SELECT * FROM bids "
							  + "WHERE auction_id = " + aid
							  + " AND isReccuring"
							  + " ORDER BY bid_time";
			ResultSet result = stmt.executeQuery(getRecBids);
			while (result.next()) {
				updateBid(result.getString(1), result.getInt(2), result.getInt(3), result.getString(4), result.getInt(6), inc);
			}
			con.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the recurring bid amount for the user.
	 *
	 * @param user buyer username
	 * @param aid auction id
	 * @return max amount for recurring bid or -1 for no recurring bid
	 */
	private static int getRecurring(String user, int aid) {
		try 
		{
			int max = -1;
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();
			String getRecBids = "SELECT max_bid FROM bids "
							  + "WHERE auction_id = " + aid
							  + " AND buyer_username = \'" + user + "\'" 
							  + " AND isReccuring";
			ResultSet result = stmt.executeQuery(getRecBids);
			if(result.next())
				max = result.getInt(1);
			con.close();
			return max;
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Update bid.
	 *
	 * @param user buyer username
	 * @param aid auction id
	 * @param amount bid amount
	 * @param time bid time
	 * @param max max bid
	 * @param inc bid increment
	 */
	private static void updateBid(String user, int aid, int amount, String time, int max, int inc) {
		try 
		{
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			int price = currentPrice(aid);
			
			String checkRec = "SELECT isReccuring FROM bids "
							+ "WHERE buyer_username = ? "
							+ "AND auction_id = ? "
							+ "AND bid_time = ? "
							+ "AND bid_amount = ?";
			PreparedStatement ps = con.prepareStatement(checkRec);
			ps.setString(1, user);
			ps.setInt(2, aid);
			ps.setString(3, time);
			ps.setInt(4, amount);
			ResultSet result = ps.executeQuery();
			if(result.next() && result.getBoolean(1) == false) {
				con.close();
				return;
			}
			String update = "UPDATE bids SET isReccuring = FALSE "
						  + "WHERE buyer_username = ? "
						  + "AND auction_id = ? "
						  + "AND bid_time = ? "
						  + "AND bid_amount < ?";
			ps = con.prepareStatement(update);
			ps.setString(1, user);
			ps.setInt(2, aid);
			ps.setString(3, time);
			ps.setInt(4, price);
			ps.executeUpdate();
			con.close();
		
			if (price > amount) {
				String itemName = AuctionQuery.itemName(aid);
				if(price + inc <= max) {
					createBid(user, aid, price + inc, (price + inc != max), max);
					if(price + inc == max)
						Messages.createMessage(user, aid, "Max Bid Reached", "Your reccuring bid on " + itemName
								+ " has reached a max bid of $" + max + ".");
				}
				else {
					Messages.createMessage(user, aid, "Max Bid Reached", "Your reccuring bid on " + itemName
										+ " has reached a max bid of $" + max + ".");
					if(price + inc > max)
						Messages.createMessage(user, aid, "You have been outbid!"
								, "Someone has placed a higher bid than you for $" + price + " on the item " + itemName);
				}
			}
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Current price for auction.
	 *
	 * @param aid auction id
	 * @return current price of auction
	 */
	public static int currentPrice (int aid) {
		try 
		{
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();
			String price = "SELECT bid_amount FROM bids WHERE auction_id = " + aid + " order by bid_amount DESC";
			String starting_price = "SELECT starting_price FROM auction WHERE auction_id = " + aid;
			ResultSet result = stmt.executeQuery(price);
			int amount = -1;
			if(result.next())
				amount = result.getInt("bid_amount");
			result = stmt.executeQuery(starting_price);
			result.next();
			int starting = result.getInt("starting_price");
			con.close();
			
			return (amount > starting ? amount : starting);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * History of bidding for an auction.
	 *
	 * @param user client username
	 * @param aid auction id
	 * @return HTML encoded bid history
	 */
	public static String history (String user, int aid) {
		try 
		{
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();
			String history = "<h3>Bid History:</h3><div class=\"bid-history\">";
			String hist = "SELECT * FROM bids WHERE auction_id = " + aid + " order by bid_amount DESC";
			ResultSet result = stmt.executeQuery(hist);
			int i = 0;
			while(result.next()) {
				history += "<span>$" + result.getString("bid_amount") + " @ " + result.getString("bid_time");
				if (user != null && user.equals(result.getString("buyer_username")))
					history += " (You)";
				history += "</span>";
				i++;
			}
			if (i == 0)
				history += "<span>No bids! Be the first to bid on this item</span>";
			history += "</div>";
			con.close();
			return history;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
}
