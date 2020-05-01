package org.openjdev.is.main;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openjdev.is.db.DbRequest;
import org.openjdev.is.html.HTMLBuilder;
import org.openjdev.is.http.HTTPUtils;
import org.openjdev.is.io.FileUtils;
import org.openjdev.is.login.VerifyConnection;

/**
 * Servlet implementation class SearchUsers
 */
@WebServlet("/SearchUsers")
public class SearchUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchUsers() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		Locale locale = HTTPUtils.getSessionLocale(session, response);
		
		String userQuery = request.getParameter("query");
		Logger.getLogger(SearchUsers.class.getName()).log(Level.INFO, "<" + SearchUsers.class.getName() + "> User query: " + userQuery);
		
		
		if(locale != null) {

			StringBuffer userList = new StringBuffer();
			
			
			DbRequest query = new DbRequest("select id,firstname,lastname,uid from isuser where lastname ilike ? or firstname ilike ? or uid ilike ?;");
			query.setParameter(1, "%" + userQuery + "%");
			query.setParameter(2, "%" + userQuery + "%");
			query.setParameter(3, "%" + userQuery + "%");
			
			ResultSet resultSet = query.execute();
			
			try {
				//just 1 row because logon is unique column in the database
				while (resultSet.next()) {
					
					userList.append("<li style=\"cursor: pointer;\" id=\"" + resultSet.getString("id") + "\" class=\"w3-display-container\">" + resultSet.getString("firstname") + " " + resultSet.getString("lastname") + " (" + resultSet.getString("uid") + ")</li>");
					
					//userList.append("<li class=\"selected-user w3-display-container\" id=\"" + resultSet.getString("id") + "\" onclick=\"selectItem(this)\">" + resultSet.getString("firstname") + "&nbsp;" + resultSet.getString("lastname") + "&nbsp;(" + resultSet.getString("uid") + ")<span onclick=\"this.parentElement.style.display='none'\" class=\"w3-button w3-display-right\">&time;</span></li>\n");
					
				}
			} catch (SQLException e) {
				Logger.getLogger(SearchUsers.class.getName()).log(Level.SEVERE, "<" + SearchUsers.class.getName() + "> Unable to access the database ");
				response.getWriter().append("Unable to access the database.");
				e.printStackTrace();
			}
			query.close();
			query = null;
			resultSet = null;
			
			Logger.getLogger(SearchUsers.class.getName()).log(Level.INFO, "<" + SearchUsers.class.getName() + "> User list: " + userList.toString());
			
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().append(userList.toString());
			response.getWriter().close();	
			
			
			
			
		} else {
			Logger.getLogger(SearchUsers.class.getName()).log(Level.SEVERE, "<" + SearchUsers.class.getName() + "> Locale is null. The Session has ended. Please reconnect.");
			File disconnect = new File(this.getServletContext().getRealPath("Disconnect.html"));
			response.getWriter().write(FileUtils.getContentAsString(disconnect));		
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
