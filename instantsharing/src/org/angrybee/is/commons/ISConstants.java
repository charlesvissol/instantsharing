package org.angrybee.is.commons;

import java.util.ResourceBundle;

/**
 * 
 * @author Charles Vissol - Angrybee
 *
 */
public class ISConstants {

	private static ResourceBundle resources = ResourceBundle.getBundle(ISConstants.class.getName());	
	
	
	/**
	 * DB context of ConfluenceViewer
	 */
	public static String DB_CONTEXT = resources.getString("db.context");

	
	/**
	 * Structure: Folder to import manuals
	 */
	public static String REPOSITORY_FOLDER = resources.getString("repository.folder");
	
	/**
	 * Event: User is connecting
	 */
	public static String RECONNECT = resources.getString("reconnect");
	
	/**
	 * Event: User is connecting
	 */
	public static String CONNECT = resources.getString("connect");
	
	/**
	 * Event: User is disconnecting
	 */
	public static String DISCONNECT = resources.getString("disconnect");

	
	/**
	 * Event: Create new list
	 */
	public static String CREATE_LIST = resources.getString("create.list");
	
	
	
	
	
	
}
