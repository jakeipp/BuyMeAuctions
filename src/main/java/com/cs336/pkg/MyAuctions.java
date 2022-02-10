package com.cs336.pkg;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class MyAuctions.
 */
@WebServlet("/MyAuctions")
public class MyAuctions extends HttpServlet {
	
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
		String address = "myauctions.jsp";
		HttpSession session = request.getSession();
		String user = (String)session.getAttribute("USERNAME");
        if(user != null)
        {
        	Messages msgs = new Messages(user);
        	request.setAttribute("MSGS", msgs.msgArray());
        	request.getRequestDispatcher(address).forward(request, response);
        }
        else
        	request.getRequestDispatcher("index.jsp").forward(request, response);;
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

	}

}
