package com.cs336.pkg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * AuctionQuery - Used to query the database and get relevant auctions. 
 */
public class AuctionQuery {
	
	/** The auctions. */
	private ArrayList<ArrayList<Auction>> auctions;

	/**
	 * Instantiates a new auction query.
	 */
	public AuctionQuery () 
	{
		this("");
	}
	
	/**
	 * Instantiates a new auction query.
	 *
	 * @param command SQL where command
	 */
	public AuctionQuery (String command) 
	{
		try 
		{
			closeAuctions();
			auctions = new ArrayList<ArrayList<Auction>>();
			
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();

			String str = "show tables like \'item_%\'";
			ResultSet result = stmt.executeQuery(str);
			while (result.next()) {
				ArrayList<Auction> title = new ArrayList<Auction>();
				title.add(new Auction(result.getString(1)));
				auctions.add(title);
			}
			for (ArrayList<Auction> auctionList : auctions) {
				String table = auctionList.get(0).type();
				str = ("select * from auction join item using (auction_id, seller_username) join " +
						table + " using (item_id, seller_username) " + command + " ORDER BY final_price, end_date, end_time");
				result = stmt.executeQuery(str);
				int numCol = result.getMetaData().getColumnCount();
				ArrayList<String> template = new ArrayList<String>();
				for (int i = 1; i <= numCol; i++) {
					String label = result.getMetaData().getColumnLabel(i);
					if (!( label.equals("auction_id") || label.equals("item_id") || label.equals("min_price") 
							|| label.equals("final_price") )) 
						template.add(label);
				}
				auctionList.set(0, new Auction(table, template, null, -1));
				while (result.next()) {
					ArrayList<String> fields = new ArrayList<String>();
					for (String label : template) {
						fields.add(result.getString(label));
					}
					auctionList.add(new Auction(table, template, fields, result.getInt("auction_id")));
				}
			}
			
			
			con.close();
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Instantiates a new auction query.
	 *
	 * @param table the table for the item type
	 * @param command SQL where command
	 */
	public AuctionQuery (String table, String command) 
	{
		try 
		{
			closeAuctions();
			auctions = new ArrayList<ArrayList<Auction>>();
			auctions.add(new ArrayList<Auction>());
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();
			
			String str = ("select * from auction join item using (auction_id, seller_username) join " +
					table + " using (item_id, seller_username) " + command + " ORDER BY final_price, end_date, end_time");
			ResultSet result = stmt.executeQuery(str);
			int numCol = result.getMetaData().getColumnCount();
			ArrayList<String> template = new ArrayList<String>();
			for (int i = 1; i <= numCol; i++) {
				String label = result.getMetaData().getColumnLabel(i);
				if (!( label.equals("auction_id") || label.equals("item_id") || label.equals("min_price") 
						|| label.equals("final_price") )) 
					template.add(label);
			}
			while (result.next()) {
				ArrayList<String> fields = new ArrayList<String>();
				for (String label : template) {
					fields.add(result.getString(label));
				}
				auctions.get(0).add(new Auction(table, template, fields, result.getInt("auction_id")));
			}
			
			
			con.close();
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Instantiates a new auction query.
	 *
	 * @param auction_id the auction id
	 */
	public AuctionQuery (Integer auction_id) 
	{
		this("WHERE auction_id = \'" + auction_id.toString() + "\'");
	}
	
	/**
	 * Item types.
	 *
	 * @return a list of the types of items in the auctions
	 */
	public ArrayList<String> itemTypes ()
	{
		ArrayList<String> types  = new ArrayList<String>();
		for (ArrayList<Auction> auction : auctions)
			types.add(auction.get(0).type());
		return types;
	
	}
	
	/**
	 * Auction labels.
	 *
	 * @param type the type of the item in the auction
	 * @return list of auction labels
	 */
	public ArrayList<String> auctionLabels (String type)
	{
		ArrayList<String> labels = null;
		for (ArrayList<Auction> auction : auctions)
			if (auction.get(0).type().equals(type))
				labels = auction.get(0).template();
		return labels;
	
	}
	
	/**
	 * Auctions.
	 *
	 * @return auctions
	 */
	public ArrayList<ArrayList<Auction>> auctions () {
		return auctions;
	}
	
	/**
	 * Auctions.
	 *
	 * @param type the type of the auctions
	 * @return auctions of type type
	 */
	public ArrayList<ArrayList<Auction>> auctions (String type) {
		ArrayList<ArrayList<Auction>> newAuc = new ArrayList<ArrayList<Auction>>();
		for (ArrayList<Auction> list : auctions)
			if (list.get(0).type().equals(type) && list.size() > 1)
				newAuc.add(list);
		auctions = newAuc;
		return auctions;
	}
	
	
	/**
	 * To string.
	 *
	 * @return HTML encoded auction list
	 */
	@Override
	public String toString() {
		
		if (auctions.size() == 0)
			return "<h2>No auctions available with specified filter settings</h2>";
		String str = new String();
		
		str += "<ul class=\"auctions\">";
		
		
		int i = 0;
		for (ArrayList<Auction> auctionList : auctions) {
			boolean closed = false;
				for (Auction auction : auctionList) {
					if (auction.fields() == null)
						continue;
					
					closed = isClosed(auction.auction_id());
					str += "<li>"
						+ "<div class=\"auction\">"
						+ "<div>"
						+ "<a class=\"item-title\" href=\"Auction?auction=" + auction.auction_id() + "\">" + auction.field("title") + "</a>";
						
					str += closed ? "<span>" : "";
					
					str += "<span class=\"item-type\">" + auction.field("type") + "</span>";

					str += closed ? "<span class=\"item-closed\">CLOSED</span></span>" : "";
					
					str	+= "</div>"
						+ "<div>"
						+ "<span>Ends: " + auction.field("end_date") + "</span>"
						+ "<span class=\"item-price\">$" + Bid.currentPrice(auction.auction_id()) + "</span>"
						+ "</div>"
						+ "</div>"
						+ "<div class=\"auction-desc\">"
						+ "<span>Seller: " + auction.field("seller_username") + "</span>"
						+ "<span>Manufacterer: " + auction.field("manufacterer") + "</span>"
						+ "<span>Year: " + auction.field("model_year") + "</span>"
						+ "<span>" + auction.field("description") + "</span>"
						+ "</div>"
						+ "</li>";
					i++;
			}
		}
		
		str += "</ul>";
				
		return i == 0 ? "<h3>No auctions available</h3>" : str;
	}
	
	/**
	 * Item name.
	 *
	 * @param aid auction id
	 * @return name of the item in the auction
	 */
	public static String itemName (int aid) {
		try 
		{
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();
			String price = "SELECT title FROM item WHERE auction_id = " + aid;
			ResultSet result = stmt.executeQuery(price);
			String name = "";
			if(result.next())
				name = result.getString(1);
			con.close();
			return name;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Close auctions. Runs close() on all auctions.
	 */
	public static void closeAuctions () {
		try 
		{
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();
			String getClosed = "SELECT auction_id FROM auction "
							 + "WHERE final_price = -1 "
							 + "AND (end_date < date(now()) "
							 + "OR (end_date = date(now()) AND end_time <= time(now())))";
			ResultSet result = stmt.executeQuery(getClosed);
			while (result.next()) {
				close (result.getInt(1));
			}
			con.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Close. Closes the selected auction and notifies the buyer and seller if the reserve is met.
	 *
	 * @param aid auction id
	 */
	public static void close (int aid) {
		try 
		{
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();
			String closeAuc = "UPDATE auction SET buyer_username = ?, final_price = ? WHERE auction_id = " + aid;
			String aucInfo = "SELECT seller_username, min_price, bid_amount, b.buyer_username "
						   + "FROM auction a "
						   + "LEFT JOIN bids b USING (auction_id) "
						   + "WHERE auction_id = " + aid + " "
						   + "AND bid_amount >= all (select bid_amount from bids where auction_id = a.auction_id)";
			ResultSet result = stmt.executeQuery(aucInfo);
			result.next();
			String seller = result.getString(1);
			int reserve = result.getInt(2);
			int final_bid = result.getInt(3);
			String buyer = result.getString(4);
			PreparedStatement ps = con.prepareStatement(closeAuc);
			
			if (reserve > final_bid) {
				ps.setString(1, seller);
				Messages.createMessage(seller, aid, "Reserve Not Met", "Your auction for " + itemName(aid) 
										+ " has closed and your reserve was not met.");
			}
			else {
				ps.setString(1, buyer);
				Messages.createMessage(buyer, aid, "Auction Won!", "Your bid for " + itemName(aid) 
										+ " was the final bid and you have won the auction.");
				Messages.createMessage(seller, aid, "Auction Closed", "Your auction for " + itemName(aid) 
										+ " has closed successfully with a final price of $" + final_bid + ".");
				
			}
			
			ps.setInt(2, final_bid);
			ps.executeUpdate();
			
			
			con.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if an auction is closed.
	 *
	 * @param aid auction id
	 * @return true, if auction is closed
	 */
	public static boolean isClosed (int aid) {
		try 
		{
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();
			String aucInfo = "SELECT buyer_username "
						   + "FROM auction "
						   + "WHERE auction_id = " + aid;
			ResultSet result = stmt.executeQuery(aucInfo);
			result.next();
			String buyer = result.getString(1);
			
			con.close();
			return (buyer != null);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
}
