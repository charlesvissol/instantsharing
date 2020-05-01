package org.openjdev.is.db;

public class DbHistory {

	
	
	/**
	 * Method to add history tracking for a dedicated user
	 * @param userId technical identifier of the user
	 * @param action Action which is a static attribute of DbHistory
	 * @param url HTML page name
	 * @param id Identifier of the object target of the action
	 */
	public static void addUserHistory(int userId, String action, String url, String id) {
		
		if (url == null)
			url = "NULL";//In case of forcing disconnection 
		if(id == null)
			id = "NULL";//If user is not consulting manual
		
		DbRequest query = new DbRequest("insert into isuserhistory (action, id_url, url, id_isuser) values (?, ?, ?, ?);");
		query.setParameter(1, action);
		query.setParameter(2, id);
		query.setParameter(3, url);
		query.setParameter(4, userId);
		
		query.execute();
		query.close();
		query = null;
		
	}
	
	
}
