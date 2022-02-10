package com.cs336.pkg;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CreateAuction.
 */
@WebServlet("/WatchList")
public class WatchList extends HttpServlet {
	
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
		String address = "watchlist.jsp";
		HttpSession session = request.getSession();
		
		if (session.getAttribute("USERNAME") == null)
			address = "login.jsp";
        request.getRequestDispatcher(address).forward(request, response);
	}


	/**
	 * Do post. Add / Remove the selected auction to the user's watchlist.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String user = (String)session.getAttribute("USERNAME");
		boolean watch = request.getParameter("watch") != null && request.getParameter("watch").equals("Watch");
		if (user == null || request.getParameter("auction") == null)
			response.sendRedirect("WatchList");
		else {
		try 
		{
			int aid = Integer.parseInt(request.getParameter("auction"));
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			String insWatch = "INSERT INTO watchlist VALUE (?, ?)";
			String delWatch = "DELETE FROM watchlist WHERE auction_id = ? AND buyer_username = ?";
			
			PreparedStatement ps = con.prepareStatement(watch ? insWatch : delWatch);
			ps.setInt(1, aid);
			ps.setString(2, user);

		
			ps.executeUpdate();

			con.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		response.sendRedirect("WatchList");
		}
	}
	
	/**
	 * Watch form.
	 *
	 * @param user client username
	 * @param aid auction id
	 * @return HTML encoded form
	 */
	public static String watchForm (String user, int aid) {
		if (user == null)
			return "";
		String str = "<form method=\"post\" action=\"WatchList\">"
				   + "<input type=\"hidden\" name=\"auction\" value=\"" + aid + "\">";
		if (!isWatching(user,aid))
			str += "<input type=\"submit\" name=\"watch\" value=\"Watch\">";
		else
			str += "<input type=\"submit\" name=\"watch\" value=\"UnWatch\">";
		str += "</form>";
		return str;
	}
	
	/**
	 * Checks if user is watching an auction.
	 *
	 * @param user client username
	 * @param aid auction id
	 * @return true, if is watching
	 */
	public static boolean isWatching (String user, int aid) {
		Boolean isWatching = false;
		try 
		{
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();

			String isWatch = "SELECT auction_id FROM watchlist WHERE buyer_username = \'" + user + "\' AND auction_id = " + aid;
			ResultSet result = stmt.executeQuery(isWatch);
			
			isWatching = result.next();

			con.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return isWatching;
	}
}
