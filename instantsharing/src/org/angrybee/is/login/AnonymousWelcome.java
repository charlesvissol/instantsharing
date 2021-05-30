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

import org.angrybee.is.html.HTMLBuilder;

/**
 * Servlet implementation class AnonymousWelcome
 */
@WebServlet("/AnonymousWelcome")
public class AnonymousWelcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ResourceBundle resources;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AnonymousWelcome() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		
		
		//lang = request.getParameter("lang");//Get locale from the Browser
		
		//Logger.getLogger(Welcome.class.getName()).log(Level.INFO, "<" + Welcome.class.getName() + "> Browser language: " + lang);

		//Locale locale = null; 
		
		//Adapt the locale & the language for the HTML page
		/*
		if(lang.contains("fr")) {
			locale = new Locale("fr", "FR");
		} else {
			locale = new Locale("en", "GB");
		}*/
		
		String userSharingPath = Long.toString(System.currentTimeMillis());
		
		//Create user Session
		session = request.getSession(true);
		
		//Set Token to build upload path
		session.setAttribute("token", userSharingPath);
		
		
		resources = ResourceBundle.getBundle(AnonymousWelcome.class.getName());
		
		HashMap<String, String> propertiesKeys = HTMLBuilder.getProperties(resources);//Get properties adapted to Locale
		
		
		
		Logger.getLogger(AnonymousWelcome.class.getName()).log(Level.INFO, "<" + AnonymousWelcome.class.getName() + "> Properties value: " + propertiesKeys.get("link"));

		String uri = request.getScheme() + "://" + request.getServerName() 
				+ ":" 
				+ request.getServerPort() 
				+ request.getContextPath();
		
				
		Logger.getLogger(AnonymousWelcome.class.getName()).log(Level.INFO, "<" + AnonymousWelcome.class.getName() + "> URL: " + uri);
		
		propertiesKeys.put("link", "value->" + uri + "/Download?token=" + userSharingPath);
		
		File input = new File(this.getServletContext().getRealPath("AnonymousSharing.html"));// HTML file associated to the current servlet
		
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
