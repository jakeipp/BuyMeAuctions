package com.cs336.pkg;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CreateAuction.
 */
@WebServlet("/CreateAuction")
public class CreateAuction extends HttpServlet {
	
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
		String address = "createauction.jsp";
		HttpSession session = request.getSession();
		
		if (session.getAttribute("USERNAME") == null)
			address = "login.jsp";
        request.getRequestDispatcher(address).forward(request, response);
	}


	/**
	 * Do post. Inserts a new auction, item, and item type into the DB given the client is logged in and submits the expected parameters
	 *
	 * @param request the request - should contain all fields from the auction form
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.getAttribute("USERNAME") == null)
			response.sendRedirect("Login");
		else {
		try 
		{
			CreateAuction auc = new CreateAuction();
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			String type = (String)request.getParameter("table");
			String insAuc = "INSERT INTO auction(auction_id, seller_username, min_price, starting_price, end_date, end_time, bid_inc) VALUE (null, ?, ?, ?, ?, ?, ?)";
			String insItem = "INSERT INTO item VALUE (null, ?, LAST_INSERT_ID(), ?, ?, ?, ?, ?)";
			String insType = "INSERT INTO " + type + " value (LAST_INSERT_ID(), ?, ?";
			for (String field : auc.labels.get(auc.types.indexOf(type)))
				if (!(field.equals("item_id") || field.equals("seller_username") || field.equals("type")))
				insType += ", ?";
			insType += ");";
			
			
			PreparedStatement ps = con.prepareStatement(insAuc);
			ps.setString(1, (String)session.getAttribute("USERNAME"));
			ps.setString(2, (String)request.getParameter("min_price"));
			ps.setString(3, (String)request.getParameter("starting_price"));
			ps.setString(4, (String)request.getParameter("end_date"));
			ps.setString(5, (String)request.getParameter("end_time"));
			ps.setString(6, (String)request.getParameter("bid_inc"));
			PreparedStatement ps2 = con.prepareStatement(insItem);
			ps2.setString(1, (String)session.getAttribute("USERNAME"));
			ps2.setString(2, (String)request.getParameter("quantity"));
			ps2.setString(3, (String)request.getParameter("title"));
			ps2.setString(4, (String)request.getParameter("descript"));
			ps2.setString(5, (String)request.getParameter("manufacterer"));
			ps2.setString(6, (String)request.getParameter("model_year"));
			PreparedStatement ps3 = con.prepareStatement(insType);
			ps3.setString(1, (String)session.getAttribute("USERNAME"));
			ps3.setString(2, (String)request.getParameter("type"));
			
			int i = 3;
			for (String field : auc.labels.get(auc.types.indexOf(type)))
				if (!(field.equals("item_id") || field.equals("seller_username") || field.equals("type"))) {
					ps3.setString(i, (String)request.getParameter(field)); 
					i++;
				}
					
			
			
			ps.executeUpdate();
			ps2.executeUpdate();
			ps3.executeUpdate();
			
			con.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		response.sendRedirect("MyAuctions?msg=success");
		}
	}
	
	/** The types. */
	private ArrayList<String> types;
	
	/** The labels. */
	private ArrayList<ArrayList<String>> labels;
	
	/** The enums. */
	private ArrayList<ArrayList<String>> enums;
	
	/** The labels types. */
	private ArrayList<ArrayList<String>> labelsTypes;
	
	
	/**
	 * Instantiates a new creates the auction.
	 */
	public CreateAuction () {
		super();
		try 
		{
			labels = new ArrayList<ArrayList<String>>();
			enums = new ArrayList<ArrayList<String>>();
			labelsTypes = new ArrayList<ArrayList<String>>();
			types = new ArrayList<String>();
			
			ApplicationDB db = new ApplicationDB();
			Connection con = db.getConnection();
			Statement stmt = con.createStatement();

			String str = "show tables like \'item_%\'";
			ResultSet result = stmt.executeQuery(str);
			
			while (result.next()) {
				types.add(result.getString(1));
				labels.add(new ArrayList<String>());;
				enums.add(new ArrayList<String>());
				labelsTypes.add(new ArrayList<String>());
			}
			
			
			
			int i = 0;
			for (ArrayList<String> fields : labels) {
				
				str = ("select * from " + types.get(i));
				result = stmt.executeQuery(str);
				int numCol = result.getMetaData().getColumnCount();
				
				for (int j = 1; j <= numCol; j++) {
					String label = result.getMetaData().getColumnLabel(j);
					String labelType = (result.getMetaData().getColumnTypeName(j));
					switch (labelType) 
					{
						case "INT":
							labelType = "type=\"number\" min=\"0\"";
							break;
						case "FLOAT":
							labelType = "type=\"number\" step=\"0.1\" min=\"0\"";
							break;
						case "VARCHAR":
							labelType = "type=\"text\"";
							break;
					}
					
					fields.add(label);
					labelsTypes.get(i).add(labelType);
				}
				
				str = (
						"SELECT SUBSTRING(COLUMN_TYPE,5)"
						+ " FROM information_schema.COLUMNS"
						+ " WHERE TABLE_NAME=\'"+ types.get(i) + "\'"
						+ " AND COLUMN_NAME=\'type\'"
						);
				result = stmt.executeQuery(str);
				result.next();
				String option = "";
				for (char c : result.getString(1).toCharArray()) {
					if (c == '(' || c == '\'') continue;
					if (c != ',' && c != ')') option += c;
					else {
						enums.get(i).add(option);
						option = "";
					}
				}
				
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
	 * Type specific fields.
	 *
	 * @return the array list
	 */
	public ArrayList<ArrayList<String>> typeSpecificFields () {
		return labels;
	}
	
	/**
	 * Fields.
	 *
	 * @param type the type
	 * @return the array list
	 */
	public ArrayList<String> fields (String type) {
		return labels.get(types.indexOf(type));
	}

	/**
	 * Types.
	 *
	 * @return the array list
	 */
	public ArrayList<String> types () {
		return types;
	}
	
	/**
	 * Enums.
	 *
	 * @param type the type
	 * @return the array list
	 */
	public ArrayList<String> enums (String type) {
		return enums.get(types.indexOf(type));
	}
	
	/**
	 * Type selector.
	 *
	 * @param selected the selected
	 * @return the string
	 */
	public String typeSelector (String selected) {
		String str =  "<h2>Select an Item Type:</h2>"
					+ "<form class=\"type-selector\" action=\"CreateAuction\" method=\"get\">";
		for (String type : types) {
			if (type.equals(selected))
				str += "<button type=\"submit\" name=\"type\" value=\"" + type + "\" class=\"type-selected\">" + type.substring(5) + "</button>";
			else
				str += "<button type=\"submit\" name=\"type\" value=\"" + type + "\" >" + type.substring(5) + "</button>";
		}
		str += "</form>";
		return str;
	}
	
	
	/**
	 * Creates the auction form with type specific fields.
	 *
	 * @param selected the selected type of item to be in the auction
	 * @return HTML encoded form for creating an auction
	 */
	public String createForm (String selected) {
		if (selected == null)
			return "";
		String str = "<form method=\"post\" action=\"CreateAuction\">"
				+ "<h2>Describe your item:</h2>"
				+ "<span>Title</span><input type=\"text\" name=\"title\" required>"
				+ "<span>Description</span><textarea type=\"text\" name=\"descript\" required></textarea>"
				+ "<span>Manufacterer</span><input type=\"text\" name=\"manufacterer\" required>"
				+ "<span>Year</span><input type=\"number\" min=\"0\" max=\"3000\" name=\"model_year\" required>"
				+ "<span>Quantity</span><input type=\"number\" min=\"0\" name=\"quantity\" required>"
				+ "<input type=\"hidden\" name=\"table\" value=\"" + selected + "\">"
				+ "<span>Type of " + selected.substring(5) + "</span><select name=\"type\">";
		for (String subType : enums.get(types.indexOf(selected)))
			str += "<option>" + subType + "</option>";
		str +=  "</select>";
		int i = 0;
		for(String field : labels.get(types.indexOf(selected))) {
			String fieldType = labelsTypes.get(types.indexOf(selected)).get(i);
			if (!(field.equals("item_id") || field.equals("seller_username") || field.equals("type")))
				str += "<span style=\"text-transform: capitalize\">" + field.replace('_', ' ') + "</span>" 
					 + "<input " + fieldType + " name=\"" + field + "\" required>";
			i++;
		}
		str +=  "<h2>Set Parameters of your Auction:</h2>"
			+ "<span>Reserve Price</span><input type=\"number\" name=\"min_price\" required>"
			+ "<span>Starting Price</span><input type=\"number\" name=\"starting_price\" required>"
			+ "<span>Ending Date</span><input type=\"date\" name=\"end_date\" required>"
			+ "<span>Ending Time</span><input type=\"time\" name=\"end_time\" required>"
			+ "<span>Bid Increment</span><input type=\"number\" name=\"bid_inc\" required>"
			+ "<input type=\"submit\" value=\"Create Auction\">"
			+ "</form>";
		return str;

	}
	
	/**
	 * Creates the filter and populates with the selected type labels.
	 *
	 * @param selected the selected auction item type
	 * @return HTML encoded filter form elements
	 */
	public String createFilter (String selected) {
		if (selected == null)
			return "";
		String str = "<span>Type of " + selected.substring(5) + "</span><select name=\"type\">"
				   + "<option>Any</option>";
		for (String subType : enums.get(types.indexOf(selected)))
			str += "<option>" + subType + "</option>";
		str +=  "</select>";
		int i = 0;
		for(String field : labels.get(types.indexOf(selected))) {
			String fieldType = labelsTypes.get(types.indexOf(selected)).get(i);
			if (!(field.equals("item_id") || field.equals("seller_username") || field.equals("type")))
				str += "<span style=\"text-transform: capitalize\">" + field.replace('_', ' ') + "</span>" 
					 + "<input " + fieldType + " name=\"" + field + "\">";
			i++;
		}
		return str;

	}
}
