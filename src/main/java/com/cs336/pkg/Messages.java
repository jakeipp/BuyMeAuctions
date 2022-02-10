package com.cs336.pkg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Messages - Deals with relaying and displaying messages for users of the BuyMe site.
 */
public class Messages {
	
	/** The msgs. */
	private String[][] msgs;
	
	/**
	 * Instantiates a new messages object.
	 *
	 * @param user client username
	 */
	public Messages (String user) 
	{
		try 
		{
			
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();

			String str = "SELECT * FROM messages WHERE end_username = \'" + user + "\' ORDER BY message_num DESC";
			ResultSet result = stmt.executeQuery(str);
			result.last();
			int s = result.getRow() + 1;
			result.beforeFirst();
			msgs = new String[s][4];
			int i = 0;
			while(result.next()) 
			{
				msgs[i][0] = result.getString(1);
				msgs[i][1] = result.getString(2);
				msgs[i][2] = result.getString(4);
				msgs[i][3] = result.getString(5);
		        i++;
			}
			con.close();
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Msg array.
	 *
	 * @return Double array of messages
	 */
	public String[][] msgArray ()
	{
		return msgs;
	}
	
	/**
	 * Dismiss all messages for a user.
	 *
	 * @param user client username
	 * @return true, if successful
	 */
	static public boolean dismiss (String user)
	{
		try {
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			String str = "DELETE FROM messages WHERE end_username = \'" + user + "\'";
	        PreparedStatement ps = con.prepareStatement(str);
	        ps.executeUpdate();
	        con.close();
	        return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * Dismiss a specific message for a user.
	 *
	 * @param user client username
	 * @param id message id
	 * @return true, if successful
	 */
	static public boolean dismiss (String user, String id)
	{
		try {
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			String str = "DELETE FROM messages WHERE end_username = \'" + user + "\' AND message_num = " + id;
	        PreparedStatement ps = con.prepareStatement(str);
	        ps.executeUpdate();
	        con.close();
	        return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * To string.
	 *
	 * @return HTML encoded messages
	 */
	@Override
	public String toString() {
		String str = new String();
		int i = 0;
        while(msgs[i][0] != null && i < msgs.length)
        {
            str += (
            		"<div class=\"message\">" + 
            			"<h2 class=\"subject\">" + msgs[i][2] + "</h2>" + 
            				"<span>" + 
            					"<button type=\"submit\" name=\"dismiss\" value=\"" + msgs[i][0] + "\" class=\"msg-button\">Dismiss</button>" +
            					"<button type=\"submit\" name=\"goto\" value=\"" + msgs[i][1] + "\" class=\"msg-button\">Go To</button>" +
            				"</span>" +
            		"</div>" +
            		"<div class=\"message-content\">" + msgs[i][3] + "</div><hr>"
            		);
            i++;
        }
        str += "<div><button type=\"submit\" name=\"dismiss\" value=\"all\" class=\"msg-button\">Dismiss All</button><div>";
        if(i == 0)
        	return ("<div class=\"message\"><h2 class=\"subject\">No Messages</h2></div>");
        else
        	return str;
	}
	
	/**
	 * Creates a message.
	 *
	 * @param user client username
	 * @param aid auction id referenced
	 * @param subject subject of the message
	 * @param desc description of the message
	 * @return true, if successful
	 */
	public static boolean createMessage (String user, int aid, String subject, String desc) {
		try {
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			String str = "INSERT INTO messages (auction_id, end_username, subject, description) VALUE (?, ?, ?, ?)";
	        PreparedStatement ps = con.prepareStatement(str);
	        ps.setInt(1, aid);
	        ps.setString(2, user);
	        ps.setString(3, subject);
	        ps.setString(4, desc);
	        ps.executeUpdate();
	        con.close();
	        return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
	}
}
