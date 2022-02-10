package com.cs336.pkg;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Auction - For storing tuples of auctions.
 */
@WebServlet("/Auction")
public class Auction extends HttpServlet{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Servlet constructor
	 */
	public Auction () {
		super();
	}
	
	/**
	 * Do get.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String address = "auction.jsp";
		if (request.getParameter("auction") == null)
			request.getRequestDispatcher("index.jsp").forward(request, response);
		else
			request.getRequestDispatcher(address).forward(request, response);
	}
	
	
	
	
	/** Auction type. */
	private String type;
	
	/** Auction id. */
	private int auction_id;
	
	/** Auction fields. */
	private ArrayList<String> fields;
	
	/** Auction template (labels). */
	private ArrayList<String> template;
	
	/**
	 * Instantiates a new auction.
	 *
	 * @param type auction type
	 */
	public Auction (String type) {
		this.type = type;
	}
	
	/**
	 * Instantiates a new auction.
	 *
	 * @param type auction type
	 * @param template auction template
	 * @param fields auction fields
	 * @param auction_id auction id
	 */
	public Auction (String type, ArrayList<String> template, ArrayList<String> fields, int auction_id) {
		this.fields = fields;
		this.template = template;
		this.auction_id = auction_id;
		this.type = type;
	}
	
	/**
	 * Type of item in auction.
	 *
	 * @return auction's item type
	 */
	public String type () {
		return type;
	}
	
	/**
	 * Template.
	 *
	 * @return template (labels) of the auction
	 */
	public ArrayList<String> template () {
		return template;
	}
	
	/**
	 * Fields.
	 *
	 * @return fields of the auction
	 */
	public ArrayList<String> fields () {
		return fields;
	}
	
	/**
	 * Auction id.
	 *
	 * @return auction id
	 */
	public int auction_id () {
		return auction_id;
	}
	
	/**
	 * Field.
	 *
	 * @param field index of field
	 * @return the field data
	 */
	public String field (int field) {
		return fields.get(field);
	}
	
	/**
	 * Field.
	 *
	 * @param label label of the field
	 * @return the field data
	 */
	public String field (String label) {
		return fields.get(template.indexOf(label));
	}
	
	
	/**
	 * To string.
	 *
	 * @return HTML encoded auction page
	 */
	@Override
	public String toString() {
		String str = new String();
		str += "<div class=\"auction-page\">";
		String buyer = null;
		int inc = 1;
		int price = Bid.currentPrice(auction_id);
		int i = 0;
		str += "<div class=\"title\"><h2>" + fields.get(template.indexOf("title")) + "</h2></div>";
		for (String label : template) {
			if (label.equals("bid_inc")) {
				inc = Integer.parseInt(fields.get(i));
				i++;
				continue;
			}
			if (label.equals("buyer_username")) {
				buyer = fields.get(i);
				i++;
				continue;
			}
			if (label.equals("title")) {
				i++;
				continue;
			}
			str +=    "<div><h3>" 
					+ label.replace('_', ' ') 
					+ ":</h3><span>" 
					+ fields.get(i) 
					+ "</span></div>";
			i++;
		}
		str += "</div>";
		if (buyer == null)
			str += "<div class=\"auction-price\">"
			 + "<div class=\"auction-price\">"
			 + "<h3>Current Price: $" + price + "</h3>"
			 + "<span>Bid Increment: $" + inc + "</span>"
			 + "</div>"
			 + "<form method=\"post\" action=\"Bid\">"
			 + "<label for=\"amount\">Bid Amount</label>"
			 + "<input id=\"amount\" type=\"number\" name=\"amount\" min=\"" + (price + inc) + "\" value=\"" + (price + inc) + "\" step=\"" + inc + "\">"
			 + "<label for=\"isReccuring\">Is Reccurring</label>"
			 + "<input id=\"isReccuring\" type=\"checkbox\" name=\"isReccuring\" value=\"true\">"
			 + "<div class=\"max-container\">"
			 + "<label for=\"max_bid\">Max Bid</label><br>"
			 + "<input id=\"max_bid\" type=\"number\" name=\"max_bid\" min=\"" + (price + inc) + "\" value=\"" + (price + inc) + "\" step=\"" + inc + "\">"
			 + "</div>"
			 + "<input type=\"hidden\" name=\"auction\" value=\"" + auction_id + "\">"
			 + "<input type=\"submit\" value=\"Bid\">"
			 + "</form>";
		else
			str += "<div class=\"auction-price\">"
				 + "<h3>This auction is closed<br>Final Price: $" + price + "</h3>"
				 + "</div>";
		if (i == 0)
			return "<h1>404 Error, Page Not Found</h1>";
		return str;
	}
	
}
