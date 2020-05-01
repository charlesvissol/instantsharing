package org.openjdev.is.login;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import org.openjdev.is.commons.ISConstants;
import org.openjdev.is.db.DbHistory;
import org.openjdev.is.db.DbRequest;
import org.openjdev.is.http.HTTPUtils;


/**
 * <p>Servlet (method doGet()) where InstantSharing verifies the user's connection status.</p>
 * <p>Verification is made into database, but the first action is to control the Session: if it exists, it is destroyed and recreated.</p>
 * <p>Session is always created.</p>
 * 
 * @author Charles Vissol - Openjdev
 */
@WebServlet("/VerifyConnection")
public class VerifyConnection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ResourceBundle resources;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerifyConnection() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session != null) {
			HTTPUtils.deleteSession(session, request.getParameter("fromurl"));
		}
		

		String lang = request.getParameter("lang");
		String logon = request.getParameter("logon");
		String pswd = request.getParameter("pswd");
		
		Locale locale = null;
		
		//Adapt the locale & the language for the HTML page
		if(lang.contains("fr")) {
			locale = new Locale("fr", "FR");
		} else {
			locale = new Locale("en", "GB");
		}
		resources = ResourceBundle.getBundle(VerifyConnection.class.getName(), locale);

		//Create user Session
		session = request.getSession(true);
		
		
		String dbLang = null;
		String dbLogon = null;
		String dbPswd = null;
		String dbId = null;
		boolean dbActive = false;
		
		
		Logger.getLogger(VerifyConnection.class.getName()).log(Level.INFO, "<" + VerifyConnection.class.getName() + "> Logon <" + logon + "> is trying to connect");
		
		//Verify into database if login & pswd are ok
		
		response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
	    
		
		if(logon.length()>0 && pswd.length() > 0) {//Verify the logon/pswd
			
			DbRequest query = new DbRequest("select * from isuser where logon like ? and password like ?;");
			query.setParameter(1, logon);
			query.setParameter(2, pswd);
			
			ResultSet resultSet = query.execute();
			
			try {
				//just 1 row because logon is unique column in the database
				while (resultSet.next()) {
					dbLogon = resultSet.getString("logon");
					dbPswd = resultSet.getString("password");
					dbLang = resultSet.getString("lang");
					dbId = resultSet.getString("id");
					dbActive = resultSet.getBoolean("active"); 
					
					Logger.getLogger(VerifyConnection.class.getName()).log(Level.INFO, "Logon <"
							+ dbLogon + "> | password <" 
							+ dbPswd + "> | Lang <"
							+ dbLang + "> | Id <"
							+ dbId +">");
				}
				
				query.close();
				resultSet.close();
				query = null;
				resultSet = null;
				
				if(dbLogon != null) {
					
					if(dbActive == true) {
						session.setAttribute("isuser_id", dbId);
						session.setAttribute("isuser_lang", dbLang);					
						
						//Add historical tracking 
						DbHistory.addUserHistory(Integer.parseInt(dbId), ISConstants.CONNECT, "Welcome.html", null);
						
						response.getWriter().append(resources.getString("main.page"));//Response: [$status]$Target_Html_Page
						
						Logger.getLogger(VerifyConnection.class.getName()).log(Level.INFO, "<" + VerifyConnection.class.getName() + "> Connection ok ");
						
					} else {
						
						response.getWriter().append(resources.getString("msg.user.disabled"));
												
					}
					
					
				} else {
					response.getWriter().append(resources.getString("msg.user.errorconnect"));
					
					
				}
				
			} catch (SQLException e) {
				Logger.getLogger(VerifyConnection.class.getName()).log(Level.SEVERE, "<" + VerifyConnection.class.getName() + "> Unable to access the database ");
				response.getWriter().append("Unable to access the database.");
				e.printStackTrace();
			}
			
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
