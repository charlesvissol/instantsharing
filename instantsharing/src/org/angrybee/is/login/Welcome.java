package org.angrybee.is.login;

import java.io.File;
import java.io.IOException;
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

import org.angrybee.is.commons.ISConstants;

import org.angrybee.is.db.DbHistory;
import org.angrybee.is.html.HTMLBuilder;
import org.angrybee.is.http.HTTPUtils;


/**
 * <p>Servlet (method doGet()) that configure the login form to access to ConfluenceViewer.</p>
 * <p>We get the Language of the Browser (French or others : English) to display the page.</p> 
 * <p>If the session exists, it is destroyed</p>
 * 
 * @author Charles Vissol - ArianeGroup - ConfluenceViewer Project
 */
@WebServlet("/Welcome")
public class Welcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ResourceBundle resources;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Welcome() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		
		if (session != null) {
			Logger.getLogger(Welcome.class.getName()).log(Level.INFO, "<" + Welcome.class.getName() + "> Session remains but user" + (String) session.getAttribute("isuser_id") + " is reconnecting");
			DbHistory.addUserHistory(Integer.parseInt((String) session.getAttribute("isuser_id")), ISConstants.RECONNECT, "Welcome.html", null);//Track Reconnection
			HTTPUtils.deleteSession(session, request.getParameter("fromurl"));
		}
		
		String lang = null;
		
		lang = request.getParameter("lang");//Get locale from the Browser
		
		Logger.getLogger(Welcome.class.getName()).log(Level.INFO, "<" + Welcome.class.getName() + "> Browser language: " + lang);

		Locale locale = null; 
		
		//Adapt the locale & the language for the HTML page
		if(lang.contains("fr")) {
			locale = new Locale("fr", "FR");
		} else {
			locale = new Locale("en", "GB");
		}
		resources = ResourceBundle.getBundle(Welcome.class.getName(), locale);
		
		HashMap<String, String> propertiesKeys = HTMLBuilder.getProperties(resources);//Get properties adapted to Locale
		
		File input = new File(this.getServletContext().getRealPath("Welcome.html"));// HTML file associated to the current servlet
		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().append(HTMLBuilder.buildHtml(input, propertiesKeys));//Write the html file
		response.getWriter().close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
