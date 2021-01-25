package org.angrybee.is.main;

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

import org.angrybee.is.http.HTTPUtils;

import org.angrybee.is.io.FileUtils;
import org.angrybee.is.html.HTMLBuilder;

/**
 * Servlet implementation class MainPage
 * Display MainPage.html
 */
@WebServlet("/MainPage")
public class MainPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static ResourceBundle resources;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainPage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		Locale locale = HTTPUtils.getSessionLocale(session, response);
		
		
		if(locale != null) {

			/**
			 * Create UID for user to create unique identifier for each upload
			 */
			String userUid = Long.toString(System.currentTimeMillis());
			session.setAttribute("isuser_uid", userUid);
			
			
			
			resources = ResourceBundle.getBundle(MainPage.class.getName(), locale);
			
			HashMap<String, String> propertiesKeys = HTMLBuilder.getProperties(resources);//Get properties adapted to Locale
			
			/*
			 * Add unique ID to current user sharing.
			 * Unique Id is attached to user each time he load the page
			 */
			propertiesKeys.put("sharing-uid", userUid);
			
			File input = new File(this.getServletContext().getRealPath("MainPage.html"));// HTML file associated to the current servlet
			
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().append(HTMLBuilder.buildHtml(input, propertiesKeys));//Write the html file in Session context to display user + role
			response.getWriter().close();	
		} else {
			Logger.getLogger(MainPage.class.getName()).log(Level.SEVERE, "<" + MainPage.class.getName() + "> Locale is null. The Session has ended. Please reconnect.");
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
