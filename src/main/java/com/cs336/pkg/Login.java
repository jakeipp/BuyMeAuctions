package com.cs336.pkg;

import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Login.
 */
@WebServlet("/Login")
public class Login extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Do get. Fetches messages when user is logged in an embeds them into the request.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String address = "login.jsp";
		HttpSession session = request.getSession();
		String user = (String)session.getAttribute("USERNAME");
        if(user != null)
        {
        	Messages msgs = new Messages(user);
        	request.setAttribute("MSGS", msgs.msgArray());
        }
        
		

        request.getRequestDispatcher(address).forward(request, response);
	}

	/**
	 * Do post. Logs in the user if they are in the DB.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Request the session
		HttpSession session = request.getSession();
		
		//Get the USERNAME variable from the session
		String USERNAME = (String)session.getAttribute("USERNAME");
		
		//Check if USERNAME is not null (User already logged in)
		if (USERNAME != null) {
			//If the user is already logged in, redirect to welcome page
			response.sendRedirect("Welcome");
		}
		
		else {

			try {
				//Get the database connection
				ApplicationDB db = new ApplicationDB();
				Connection con = db.getConnection();

				//Create a SQL statement
				Statement stmt = con.createStatement();

				//Get parameters from the HTML form at login.jsp
				String username = request.getParameter("username");
				String password = request.getParameter("password");

				//Check the Admin Table for the user's credentials
				String str = "SELECT * FROM administrators WHERE username = \'" + username + "\' AND password = \'" + password + "\'";
				ResultSet result = stmt.executeQuery(str);
				if (result.next()) {
					
					//User is an Admin, set the session variables accordingly
					session.setAttribute("USERNAME", username);
					session.setAttribute("NAME", result.getString("name"));
					session.setAttribute("ADMIN", true);
					response.sendRedirect("Welcome");
				}
				
				else {
					
					//Check the Customer Support table for the user's credentials
					str = "SELECT * FROM customer_support_reps WHERE username = \'" + username + "\' AND password = \'" + password + "\'";
					result = stmt.executeQuery(str);
					if (result.next()) {
						//User is a Customer Rep, set the session variables accordingly
						session.setAttribute("USERNAME", username);
						session.setAttribute("NAME", result.getString("name"));
						session.setAttribute("REP", true);
						response.sendRedirect("Welcome");
					}
					
					else {
						//Check the End Users table for the user's credentials
						str = "SELECT * FROM end_users WHERE username = \'" + username + "\' AND password = \'" + password + "\'";
						result = stmt.executeQuery(str);
						if (result.next()) {
							//User is a normal end user, set the session variables accordingly
							session.setAttribute("USERNAME", username);
							session.setAttribute("NAME", result.getString("name"));
							response.sendRedirect("Welcome");
						}
						else
							//Incorrect Login Info, redirect to login page with invalid message
							response.sendRedirect("Login?msg=invalid");
					}
				}
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}




	}

}
