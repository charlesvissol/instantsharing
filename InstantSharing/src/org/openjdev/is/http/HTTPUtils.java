package org.openjdev.is.http;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openjdev.is.commons.ISConstants;
import org.openjdev.is.db.DbHistory;
import org.openjdev.is.db.DbRequest;

/**
 * <p>Utility class to manage users Sessions
 * Use this class by calling static methods: HTTPUtils.$method($args).</p> 
 * <p>Take into account that this HTTPUtils class uses Session parameters such as cvuser_lang, cvuser_id, 
 * So use it if the user is connected and inside a Servlet which control first if the Session is active.</p>
 * <p>HTTPUtils requires also an access to the ConfluenceViewer database.</p>
 * 
 * @author Charles Vissol - Openjdev
 *
 */
public class HTTPUtils {


	/**
	 * <p>Generic method to verify if Session is active and to get the language of the user. Method is called from a Servlet.</p>
	 * <p>This method implies that user is connected and the Session is active. Session parameter "cvuser_lang" must be set.</p>
	 * @param session Current active Session
	 * @param response Response of the Calling Servlet
	 * @return Locale depending on the language declared and stored in the Session
	 */
	public static Locale getSessionLocale(HttpSession session, HttpServletResponse response) {
		Locale locale = null; 
		
		if (session != null) {
			Logger.getLogger(HTTPUtils.class.getName()).log(Level.INFO, "<" + HTTPUtils.class.getName() + "> Current Session OK");
			
			String lang = (String) session.getAttribute("isuser_lang");
			
			//Adapt the locale & the language for the HTML page
			if(lang.contains("fr")) {
				locale = new Locale("fr", "FR");
			} else {
				locale = new Locale("en", "GB");
			}
			
		} else {
			Logger.getLogger(HTTPUtils.class.getName()).log(Level.SEVERE, "<" + HTTPUtils.class.getName() + "> Current Session has ended unfortunatly! Perhap's user has closed the Windows browser or Session timeout. Please reconnect.");
		}
		return locale;
	}
	
	/**
	 * <p>Method to get user information from current active Session.</p>
	 * <p>This method implies that the Session is active (parameter "cvuser_id" available) and that 
	 * the access to the database is available</p>
	 * @param session Current active Session
	 * @return String table: index 0 for logon, index 1 for designation
	 */
	public static String[] getSessionUser(HttpSession session) {

		String userId = (String) session.getAttribute("isuser_id");
		
		String logon = null, roleDesignation = null;

		//Get user infos from registered Session
		DbRequest query = new DbRequest("select logon, designation from isuser, role where isuser.id=? and role.id=isuser.id_role;");
		query.setParameter(1, Integer.parseInt(userId));
		
		ResultSet resultSet = query.execute();
		try {
			//just 1 row because logon is unique column in the database
			while (resultSet.next()) {
				logon = resultSet.getString("logon");
				roleDesignation = resultSet.getString("designation");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String[] user = {logon, roleDesignation};
		
		return user;
	}
	
	/**
	 * <p>Generic method to delete an active Session.</p>
	 * <p>Access to the database is necessary.</p>
	 * @param session Current active session
	 * @param fromUrl Name of the HTML page from where the active session is deleted
	 */
	public static void deleteSession(HttpSession session, String fromUrl) {
		
			String userId = (String) session.getAttribute("isuser_id");//Get user id from current Session
			
			Logger.getLogger(HTTPUtils.class.getName()).log(Level.INFO, "<" + HTTPUtils.class.getName() + "> Before reconnecting, user (Id <" + userId + ">) is disconnecting by Instant File Sharing because session remains...");
			
			if(userId != null)//Only if Session has not ended
				DbHistory.addUserHistory(Integer.parseInt(userId), ISConstants.DISCONNECT, fromUrl, null);//Track disconnection
			
			
		    session.invalidate();//Destroy Session
		
	}
}
