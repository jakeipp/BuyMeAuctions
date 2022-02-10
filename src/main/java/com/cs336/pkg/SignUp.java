package com.cs336.pkg;

import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// TODO: Auto-generated Javadoc
/**
 * Servlet implementation class SignUp.
 */
@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
	
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
		String address = "signup.jsp";
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
	 * Do post.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			
			//Request the session
			HttpSession session = request.getSession();
			//If the user is already logged in, sign them out
			session.invalidate();
			
	        //Get the database connection
	        ApplicationDB db = new ApplicationDB();
	        Connection con = db.getConnection();

	        //Create a SQL statement
	        Statement stmt = con.createStatement();

	        //Get the user's parameters from SignUp.jsp
	        String username = request.getParameter("username");
	        String password = request.getParameter("password");
	        String name = request.getParameter("name");
	        String email = request.getParameter("email");
	        String dob = request.getParameter("dob");

	        // -- Check if the user name is in use, if it is redirect back to login page with userTaken message --
	        String str = "SELECT * FROM administrators WHERE username = \'" + username + "\'";
	        ResultSet result = stmt.executeQuery(str);
	        if (result.next())
	        	response.sendRedirect("SignUp?msg=userTaken");
	        else {
	            str = "SELECT * FROM customer_support_reps WHERE username = \'" + username + "\'";
	            result = stmt.executeQuery(str);
	            if(result.next())
	                response.sendRedirect("SignUp?msg=userTaken");
	            else {
	                str = "SELECT * FROM end_users WHERE username = \'" + username + "\'";
	                result = stmt.executeQuery(str);
	                if(result.next())
	                	response.sendRedirect("SignUp?msg=userTaken");
	                
	        // -- End check, user name is not in use --
	                
	                else {
	                	
	                	//Insert values into the DB
	                    str = "INSERT INTO end_users VALUE(?, ?, ?, ?, {d?})";
	                    PreparedStatement ps = con.prepareStatement(str);
	                    ps.setString(1, username);
	                    ps.setString(2, email);
	                    ps.setString(3, password);
	                    ps.setString(4, name);
	                    ps.setString(5, dob);
	                    ps.executeUpdate();
	                    //Redirect to login page with success message
	                    response.sendRedirect("Login?msg=success");
	                }
	            }
	        }
	        
	        con.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
