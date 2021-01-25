package org.angrybee.is.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * <p>Utility class to provide tools for database connection and data management</p>
 * <p>This class is a <b>Singleton</b> pattern</p>
 * <p>Usage:</p>
 * <pre>
 * DbUtils ut = DbUtils.getInstance()
 * </pre>
 * 
 * @author Charles Vissol - ArianeGroup - ConfluenceViewer Project
 *
 */
public class DbUtils { 
	
	/**
	 * Unique instance pre-loaded
	 */
	private static DbUtils INSTANCE = new DbUtils();
	private static Connection connection = null;
	
	/**
	 * Singleton constructor
	 */
	private DbUtils() {
		
	}
	
	/**
	 * <p>Get the instance of the Singleton with concurrence management. Not a lazy-loading (instantiation when method called).</p>
	 * @return return a DbUtils instance. this instance is a Singleton
	 */
	public static synchronized DbUtils getInstance() {
		return INSTANCE;
	}

	/**
	 * Method to get the connection from the database in Tomcat context for Servlets 
	 * <p>Example of Tomcat configuration in $TOMCAT_HOME/conf/context.xml:</p>
	 * <pre>
	 *   &lt;Resource name="jdbc/ConfluenceViewer" auth="Container" type="javax.sql.DataSource"
     *   maxTotal="20" maxIdle="10" maxWaitMillis="-1" 
     *   username="confluenceviewer" password="###" driverClassName="org.postgresql.Driver"
     *   url="jdbc:postgresql://localhost:5432/confluenceviewer"/&gt;
	 * </pre>
	 * <p>Example of $TOMCAT_HOME/webapps/$PROJECT_HOME/WEB-INF/web.xml to complete the context.xml configuration:</p>
	 * <pre>
	 * &lt;resource-ref&gt;
  	 *   &lt;description&gt;Database connection&lt;/description&gt;
  	 *   &lt;res-ref-name&gt;jdbc/ConfluenceViewer&lt;/res-ref-name&gt;
  	 *   &lt;res-type&gt;javax.sql.DataSource&lt;/res-type&gt;
  	 *   &lt;res-auth&gt;Container&lt;/res-auth&gt;
     * &lt;/resource-ref&gt;

	 * 
	 * </pre>
	 * @param contextLookup Context of the database connection described in context.xml and web.xml. Ex: "java:/comp/env/jdbc/ConfluenceViewer"
	 * @return Instance of a <pre>java.sql.Connection</pre>
	 */
	public synchronized Connection getConnection(String contextLookup) {
		
		/**
		 * New connection only if doesn't exist
		 */
		if(connection == null) {
		
			InitialContext cxt = null;
			try {
				cxt = new InitialContext();
			} catch (NamingException e) {
				Logger.getLogger(DbUtils.class.getName()).log(Level.SEVERE, "<" + DbUtils.class.getName() + "> Echec de l'instantiation du contexte [javax.naming.InitialContext]");
				e.printStackTrace();
			}
			if ( cxt == null ) {
				Logger.getLogger(DbUtils.class.getName()).log(Level.SEVERE, "<" + DbUtils.class.getName() + "> Pas de contexte [javax.naming.InitialContext] pour cr�er la connexion");
			}
	
			DataSource ds = null;
			try {
				ds = (DataSource) cxt.lookup(contextLookup);
			} catch (NamingException e) {
				Logger.getLogger(DbUtils.class.getName()).log(Level.SEVERE, "<" + DbUtils.class.getName() + "> Impossible de cr�er [javax.sql.DataSource]");
				e.printStackTrace();
			}
	
			if ( ds == null ) {
				Logger.getLogger(DbUtils.class.getName()).log(Level.SEVERE, "<" + DbUtils.class.getName() + "> Impossible de cr�er [javax.sql.DataSource]. L'objet n'est pas instanci�");
			}
			
			try {
				connection = ds.getConnection();
				Logger.getLogger(DbUtils.class.getName()).log(Level.INFO, "<" + DbUtils.class.getName() + "> Connexion � la base de donn�es r�ussie");
			} catch (SQLException e) {
				Logger.getLogger(DbUtils.class.getName()).log(Level.SEVERE, "<" + DbUtils.class.getName() + "> La connexion a �chou�");
				e.printStackTrace();
			}
		}
		return connection;
	}
	
	
	
	
}

