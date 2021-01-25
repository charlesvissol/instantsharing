package org.angrybee.is.main;

import java.io.File;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.angrybee.is.commons.ISConstants;
import org.angrybee.is.db.DbRequest;
import org.angrybee.is.http.HTTPUtils;
import org.angrybee.is.io.FileUtils;
import org.angrybee.is.db.DbHistory;

/**
 * Servlet implementation class CreateList
 */
@WebServlet("/CreateList")
public class CreateList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static ResourceBundle resources;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		Locale locale = HTTPUtils.getSessionLocale(session, response);
		
		String team = request.getParameter("team");
		String listName = request.getParameter("listname");
		int lastListId = 0;
		Document doc = null;
		
		System.out.println(request.getRemoteAddr());
		System.out.println(request.getRemoteUser());
		
		
		
		Logger.getLogger(CreateList.class.getName()).log(Level.INFO, "<" + CreateList.class.getName() + "> Listname: " + listName);
		
		
		if(locale != null) {

			
			resources = ResourceBundle.getBundle(CreateList.class.getName(), locale);

			System.out.println("#"+team+"#");
			
			
			if(listName.length() == 0) {
				
				response.setContentType("text/html");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().append(resources.getString("list.name.error"));
				response.getWriter().close();	
				
			} else if(team.trim().length() == 0) {
				response.setContentType("text/html");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().append(resources.getString("list.name.nobody"));
				response.getWriter().close();	
				
			}	else {
			
				String currentUserId = (String) session.getAttribute("isuser_id");
				
				
				StringBuffer userList = new StringBuffer();
	
				DbRequest query = new DbRequest("insert into islist (listname,id_isuser) values (?,?)");
				query.setParameter(1, listName);
				query.setParameter(2, Integer.parseInt(currentUserId));
				
				try {
					query.execute();
					
				} catch (Exception e) {
					e.printStackTrace();
					response.setContentType("text/html");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().append(resources.getString("list.created.successfully"));
					response.getWriter().close();	
					
				}
	
				
				query.close();
				query = null;
				
				
				//Add historical tracking 
				DbHistory.addUserHistory(Integer.parseInt(currentUserId), ISConstants.CREATE_LIST, "CreateList", String.valueOf(DbRequest.getMaxId("islist")));
	
				lastListId = DbRequest.getMaxId("islist");
				
				Logger.getLogger(CreateList.class.getName()).log(Level.INFO, "<" + CreateList.class.getName() + "> Max Id of list: " + lastListId);
				
				
				doc = Jsoup.parse(team);
				
				Elements listTeam = doc.getElementsByTag("li");
				for (Element element : listTeam) {
					
					element.attr("id");
					
					//@TODO
					
				}
				
				//islisttoisuser
				//id_islist integer NOT NULL,
			    //id_isuser integer NOT NULL,
			    
			    
				/*query.setParameter(1, userQuery + "%");
				query.setParameter(2, userQuery + "%");
				query.setParameter(3, userQuery + "%");
				
				ResultSet resultSet = query.execute();
				
				try {
					//just 1 row because logon is unique column in the database
					while (resultSet.next()) {
						
						userList.append("<li style=\"cursor: pointer;\" id=\"" + resultSet.getString("id") + "\" class=\"w3-display-container\">" + resultSet.getString("firstname") + "&nbsp;" + resultSet.getString("lastname") + "&nbsp;(" + resultSet.getString("uid") + ")</li>");
						
						//userList.append("<li class=\"selected-user w3-display-container\" id=\"" + resultSet.getString("id") + "\" onclick=\"selectItem(this)\">" + resultSet.getString("firstname") + "&nbsp;" + resultSet.getString("lastname") + "&nbsp;(" + resultSet.getString("uid") + ")<span onclick=\"this.parentElement.style.display='none'\" class=\"w3-button w3-display-right\">&time;</span></li>\n");
						
					}
				} catch (SQLException e) {
					Logger.getLogger(CreateList.class.getName()).log(Level.SEVERE, "<" + CreateList.class.getName() + "> Unable to access the database ");
					response.getWriter().append("Unable to access the database.");
					e.printStackTrace();
				}
				query.close();
				query = null;
				resultSet = null;
				
				Logger.getLogger(CreateList.class.getName()).log(Level.INFO, "<" + CreateList.class.getName() + "> User list: " + userList.toString());
				*/
				
				response.setContentType("text/html");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().append(resources.getString("list.created.successfully"));
				response.getWriter().close();	
			
		}
			
			
		} else {
			Logger.getLogger(CreateList.class.getName()).log(Level.SEVERE, "<" + CreateList.class.getName() + "> Locale is null. The Session has ended. Please reconnect.");
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
