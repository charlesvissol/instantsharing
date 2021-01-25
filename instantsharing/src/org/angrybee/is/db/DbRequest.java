package org.angrybee.is.db;

import org.angrybee.is.commons.ISConstants;
import org.angrybee.is.db.DbUtils;

import java.util.ResourceBundle;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * <p>Object to create SQL queries</p>
 * <p>Usage:</p>
 * <pre>
 *      DbRequest query = new DbRequest("select table_name from all_tables where table_name like ?");
 *      query.setParameter(1, "SIL%MASTER");
 *      
 *      ResultSet resultSet = query.execute();
 *      while (resultSet.next()) {
 *          System.out.println(resultSet.getString("table_name"));
 *      }
 *      query.close();
 *      query = null;
 *      resultSet = null;
 * </pre>
 * 
 * @author Charles Vissol - Openjdev
 *
 */
public class DbRequest {
	//private static ResourceBundle resources;
	/**
	 * Database context ("db.context" string in *.properties)
	 */
	private static String dbContext;//Context of the database connection

	private PreparedStatement pStatement = null;//For queries with parameters
	private Statement statement = null;//For queries without parameters
    private String query;//String representing the SQL query
	
	public DbRequest(String query) {
		
		
		dbContext = ISConstants.DB_CONTEXT;
		
		this.query = query;
		
        try {
            if (this.query.indexOf("?") != -1) {//insensitive scrollable result set is one where the values captured in the result set never change, even if changes are made to the table from which the data was retrieved
            	pStatement = DbUtils.getInstance().getConnection(dbContext).prepareStatement(this.query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            } else {//insensitive scrollable result set is one where the values captured in the result set never change, even if changes are made to the table from which the data was retrieved
    			statement = DbUtils.getInstance().getConnection(dbContext).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    			//A sensitive scrollable result set is one where the current values in the table are reflected in the result set. So if a change is made to a row in the table, the result set will show the new data when the cursor is moved to that row
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }				
	}	
	
	/**
	 * <p>Method to execute SQL queries. Insert in parameter the full SQL query with or without parameters (?).</p>
	 * <p>If the SQL query has parameters, before you must user setParameter() methods to add parameters to query.</p>
	 * <p>Example:</p>
	 * <pre>
	 *      query.setParameter(1, "SIL%MASTER");
	 *      query.initQuery("select table_name from all_tables where table_name like ?");
	 *      
	 *      ResultSet resultSet = query.execute();
	 *      while (resultSet.next()) {
	 *          System.out.println(resultSet.getString("table_name"));
	 * </pre>
	 * @return resultSet object, it means the result of the query execution
	 */
	public ResultSet execute() {
		ResultSet resultSet = null;
		

        if (pStatement == null) {
            try {
                if (this.query.indexOf("SELECT") != -1 || this.query.indexOf("select") != -1) {
                    resultSet = statement.executeQuery(this.query);
                } else {
                    statement.executeUpdate(this.query);
                }
                
                //statement.close();
                //statement = null;
                //System.gc();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                //if (this.query.indexOf("SELECT ") != -1 || this.query.indexOf("select ") != -1) {
            	if ((this.query.contains("SELECT ") || this.query.contains("select ")) && (this.query.contains(" from ") || this.query.contains(" FROM "))) {
                    resultSet = pStatement.executeQuery();
                } else {
                	pStatement.executeUpdate();
                }
                
                //pStatement.close();
                //pStatement = null;
                //System.gc();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return resultSet;
	}

	
	public static int getMaxId(String table) {
		DbRequest query = new DbRequest("select max(id) as id from " + table + ";");
		ResultSet resultSet = query.execute();
		
		try {
			while (resultSet.next()) {
				return resultSet.getInt("id");
			}
		} catch (SQLException e) {
			query.close();
			e.printStackTrace();
		}
		
		query.close();
		return 0;
	}
	
	/**
	 * Method to release the resources
	 */
	public void close() {
		
		try {
		
			if(pStatement != null) {
				pStatement.close();
				pStatement = null;
				System.gc();
			}
			if(statement != null) {
				statement.close();
				statement = null;
				System.gc();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
    /**
     * <p>Add parameter to current SQL script.</p>
     * @param index index of parameter in the query
     * @param parameter BigDecimal value of parameter
     */
    public void setParameter(int index, BigDecimal parameter) {
        try {
            this.pStatement.setBigDecimal(index, parameter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * <p>Add parameter to current SQL script.</p>
     * @param index index of parameter in the query
     * @param parameter Integer value of parameter
     */
    public void setParameter(int index, int parameter) {
        try {
            this.pStatement.setInt(index, parameter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * <p>Add parameter to current SQL script.</p>
     * @param index index of parameter in the query
     * @param parameter Long value of parameter
     */
    public void setParameter(int index, long parameter) {
        try {
            this.pStatement.setLong(index, parameter);
            //this.pStatement.setLong(index, parameter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * <p>Add parameter to current SQL script.</p>
     * @param index index of parameter in the query
     * @param parameter Date value of parameter
     */
    public void setParameter(int index, Date parameter) {
        try {
            this.pStatement.setDate(index, parameter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * <p>Add parameter to current SQL script.</p>
     * @param index index of parameter in the query
     * @param parameter Time value of parameter
     */
    public void setParameter(int index, Time parameter) {
        try {
            this.pStatement.setTime(index, parameter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	
    /**
     * <p>Add parameter to current SQL script.</p>
     * @param index index of parameter in the query
     * @param parameter String value of parameter
     */
     public void setParameter(int index, String parameter) {
        try {
            this.pStatement.setString(index, parameter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     /**
      * <p>Add parameter to current SQL script.</p>
      * @param index index of parameter in the query
      * @param parameter Object value of parameter
      */
      public void setParameter(int index, Object parameter) {
         try {
             this.pStatement.setObject(index, parameter);
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
     
     
     
     /**
      * <p>Add parameter to current SQL script.</p>
      * @param index index of parameter in the query
      * @param parameter boolean value of parameter
      */
      public void setParameter(int index, boolean parameter) {
         try {
             this.pStatement.setBoolean(index, parameter);
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }

      
      /**
       * <p>Add parameter to current SQL script.</p>
       * @param index index of parameter in the query
       * @param parameter Timestamp value of parameter
       */
       public void setParameter(int index, Timestamp parameter) {
          try {
              this.pStatement.setTimestamp(index, parameter);
              
          } catch (SQLException e) {
              e.printStackTrace();
          }
      }
      
      
      
}
