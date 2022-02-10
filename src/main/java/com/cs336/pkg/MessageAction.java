package com.cs336.pkg;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class MessageAction.
 */
@WebServlet("/MessageAction")
public class MessageAction extends HttpServlet {
	
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
		HttpSession session = request.getSession();
		String user = (String)session.getAttribute("USERNAME");
		String dismiss = request.getParameter("dismiss");
		String go = request.getParameter("goto");
		if(user != null && dismiss != null) {
			if (dismiss.equals("all"))
				Messages.dismiss(user);
			else
				Messages.dismiss(user, dismiss);
		}
		if(go != null)
			response.sendRedirect("Auction?auction=" + go);
		else
			response.sendRedirect(request.getHeader("referer"));
	}

}
