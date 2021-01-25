package org.angrybee.is.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.angrybee.is.commons.ISConstants;
import org.angrybee.is.http.HTTPUtils;
import org.angrybee.is.io.FileUtils;

/**
 * Servlet implementation class Uploader
 */
@WebServlet("/Uploader")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 2000, maxRequestSize = 1024 * 1024 * 20 * 1000)
public class Uploader extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private static ResourceBundle resources;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Uploader() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		
		/**
		 * Upload the Confluence publication 
		 */
		Locale locale = HTTPUtils.getSessionLocale(session, response);
		
		//resources = ResourceBundle.getBundle(Uploader.class.getName(), locale);
		
		String filename = null;//File to upload and import
		String importPath = null;//Root path to import documentation

		
		if(locale != null) {
			
	        String userSharingPath = (String) session.getAttribute("isuser_uid");
			
			Part file = request.getPart("file");
	        filename = getFilename(file);
	        
	        InputStream filecontent = file.getInputStream();
	        
	        importPath = this.getServletContext().getRealPath("/") + ISConstants.REPOSITORY_FOLDER + File.separator + userSharingPath + File.separator;
	        new File(importPath).mkdir();
	        
	        FileUtils.writeFromStream(filecontent, importPath + filename);
	        
	        Logger.getLogger(Uploader.class.getName()).log(Level.INFO, "<" + Uploader.class.getName() + "> Uploaded file path: " + importPath + filename);
	
	        session.setAttribute("import_filename", filename);//Set the filename in the current Session to get it for Integrator Servlet
	        session.setAttribute("import_path", importPath);//Set the import path to the current Session to get it for Integrator Servlet
	        
	        
	        response.setContentType("text/plain");
	        response.setCharacterEncoding("UTF-8");
	        //response.getWriter().write(filename + " " + resources.getString("msg.uploadsuccess") + "<br>");
	        
	        //response.sendRedirect("Integrator");
	        
		} else {
			Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, "<" + Uploader.class.getName() + "> Locale is null. The Session has ended. Please reconnect.");
			File disconnect = new File(this.getServletContext().getRealPath("Disconnect.html"));
			response.getWriter().write(FileUtils.getContentAsString(disconnect));
		}
	}

	/**
	 * Specific method dedicated to the upload. Copied from internet
	 * @param part
	 * @return
	 */
    private static String getFilename(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
            }
        }
        return null;
    }
	
	
	
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
