package org.openjdev.is.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;


/**
 * Utility class to manage files
 * @author Charles Vissol - Openjdev
 *
 */
public class FileUtils {

	/**
	 * List of strings
	 */
	ArrayList<String> listFiles;
	
	
	
	/**
	 * Method to get the String content of a file
	 * @param file File object representing the file 
	 * @return String content of the file
	 * @throws IOException Interrupts the I/O exception in case of problems when writting current text file
	 */
    public static String getContentAsString(File file) throws IOException {
        FileInputStream fis = null;
        try
        {
            StringBuffer result = new StringBuffer();
            fis = new FileInputStream(file);
            byte buff[] = new byte[1024];
            int cnt = -1;
            while ((cnt = fis.read(buff)) != -1) {
                result.append(new String(buff, 0, cnt));
            }
            return result.toString();
        }
        finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException e) {}
            }
        }
    }
	

    /**
     * Write a file from a direct stream content
     * 
     * @param sourceFile ImputStream of source file
     * @param pathFile File path to create.
     */
    public static void writeFromStream(InputStream sourceFile, String pathFile){
    	
    	try {
    	
	        FileOutputStream lFos = new FileOutputStream(pathFile);
	        byte buff[] = new byte[1024];
	        int cnt = -1;
	        while ((cnt = sourceFile.read(buff)) != -1) {
	            lFos.write(buff, 0, cnt);
	        }
	        lFos.close();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Write a text file directly from String content
     * 
     * @param p_File Chemin Full path of the file to write
     * @param p_FileContent String content to write in the file
     */
    public static void writeFromString(String p_File, String p_FileContent)
    {
    	try {
	    	File fileDir = new File(p_File);
	    	Writer out = new BufferedWriter(new OutputStreamWriter(	new FileOutputStream(fileDir),"UTF-8"));
	    	
	    	out.append(p_FileContent);
	    	out.flush();
	    	out.close();
	    	
		} catch (UnsupportedEncodingException e) {
			e.getMessage();
		} catch (IOException e) {
			e.getMessage();
		} catch (Exception e) {
			e.getMessage();
		}    	
    
    } 
    
    /**
     * Get recursive paths of files inside a directory
     * @param file Fichier de base pour le parcours.
     * @param <b>true</b> if return the full path of files, <b>false</b> if return only the name of file
     */
    private void listFiles(File file, boolean isPath)
    {
    	
        if (file.isDirectory()) 
        {
            File[] childs = file.listFiles();
            for (int i = 0; i < childs.length; i++)
            {
            	if(isPath)
            		listFiles(childs[i], true);//Full path
            	else
            		listFiles(childs[i], false);//only names
            }
        }
        if(isPath)
        	listFiles.add(file.getAbsolutePath());//Full path
        else
        	listFiles.add(file.getName());//Only names
    }
    
    /**
     * return a list of files names (format: $file.getName()) recursively obtained by 
     * analysis a root directory
     * @param file Root directory
     * @return List of String representing $file.getName()
     */
    public ArrayList<String> getFilesName(File file){
    	listFiles = new ArrayList<String>();
    	listFiles(file, false);
    	
    	return listFiles;
    }
    
    /**
     * return a list of files names (format: $file.getAbsolutePath()) recursively obtained by 
     * analysis a root directory
     * @param file Root directory
     * @return List of String representing $file.getAbsolutePath()
     */
    public ArrayList<String> getFilesPath(File file){
    	listFiles = new ArrayList<String>();
    	listFiles(file, true);
    	
    	return listFiles;
    }
    
    
    
    
    
}