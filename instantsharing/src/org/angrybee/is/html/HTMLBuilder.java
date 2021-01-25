package org.angrybee.is.html;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.angrybee.is.commons.HTMLConstants;
import org.angrybee.is.db.DbRequest;
import org.angrybee.is.http.HTTPUtils;

/**
 * <p>Class to build an HTML file from an HTML file + a properties file (with Locale adaptation).</p>
 * <p>The goal is to build HTML content adapted to the context (Language, etc.) and also to aggregate several HTML content.</p>
 * 
 * @author Charles Vissol - Openjdev
 *
 */
public class HTMLBuilder {

	/**
	 * <p>This method transforms the properties file content into an HashMap of Key and Value in order to:</p>
	 * <ul>
	 * <li> inject values in the HTML tags</li>
	 * <li>aggregate HTML fragment(s) in current HTML</li>
	 * <li>inject values in attributes</li>
	 * </ul>
	 * <p>The method assumes that the properties file has always the same structure like in the example bellow:</p>
	 * <pre>
	 * id=key1,key2,key3
	 * key1=text{$value}
	 * key2=attr{$attrName-&gt;$attrValue}
	 * key3=text{$value}
	 * </pre>
	 * <p>The values of <i>id</i> key are, in HTML, <b>id attributes of HTML tags</b>. It allows identifying HTML tags during HTML parsing.</p> 
	 * Each key is associated to text (value of tag of HTML fragment) or attr:
	 * <ul>
	 * <li><i>text</i> means that the value is a text content of a tag or an HTML fragment ex: <pre>identifier=text{Identifiant}</pre> or <pre>identifier=text{fragment.html}</pre></li>
	 * <li><i>attr</i> means that the value is composed by the name of the attribute and its value, Ex: <pre>identifierInput=attr(placeholder|Saisir identifiant)</pre></li>
	 * </ul>
	 * <ul>
	 * <li><i>$value</i>: value of the tag content: text or HTML</li>
	 * <li><i>$attrName</i>: name of the attribute</li>
	 * <li><i>$attrValue</i>: value of the attribute (text)</li>
	 * </ul>
	 * 
	 * @param resources properties file to analyze
	 * @return HashMap containing keys and value of the properties file
	 */
	public static HashMap<String, String> getProperties(ResourceBundle resources) {
		
		HashMap<String, String> htmlProperties = new HashMap<String, String>();
		
		/**
		 * The id key of the properties has the list of all other keys of the properties file
		 * We get first this list to get after, the values of each key
		 * properties keys are always separated by comma
		 */
		String identifiers = resources.getString("id");
		String[] idList = identifiers.split(",");
		
		String key = null;
		String value = null;
		
		for (int i = 0; i < idList.length; i++) {//search each value of each key 
			key = idList[i];
			value = resources.getString(key);
			htmlProperties.put(key, value.substring(value.indexOf("{")+1, value.indexOf("}")));//Get text and attr values
		}
		
		return htmlProperties;
	}
	
	/**
	 * <p>The method combines an html file with the HashMap resulting from the properties file adapted to the html file (from getProperties() method).</p>
	 * <p>This methodology allows ConfluenceViewer to build contextual GUI in HTML (local) and to aggregate different sources of HTML.</p> 
	 * 
	 * @param input File representing the html file
	 * @param htmlProperties HasMap representing all the values of the properties file
	 * @return Full html code of the page to display 
	 */
	public static String buildHtml(File input, HashMap<String, String> htmlProperties) {
		
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "");//parse the html document
		} catch (IOException e) {
			Logger.getLogger(HTMLBuilder.class.getName()).log(Level.SEVERE, "<" + HTMLBuilder.class.getName() + "> HTML File parsing impossible: " + input.getAbsolutePath());
			e.printStackTrace();
		}
		Element content = null;
		
		for (String i : htmlProperties.keySet()) {
			
			if(i.equalsIgnoreCase("script")) {//Add specific manual.js to the current HTML that display the manual
				Element head = doc.select("head").first();
				head.append("<script type=\"text/javascript\" src=\"" + htmlProperties.get(i) + "\"></script>"); 
			} else {
			    
				content = doc.getElementById(i);//select an html element
				
				//System.out.println(htmlProperties.get(i));
				//System.out.println(i);
				if(htmlProperties.get(i).contains("->")) {//Set the value in an HTML attribute 
					//System.out.println(htmlProperties.get(i));
					String[] attribs = htmlProperties.get(i).split("->");
					
					Logger.getLogger(HTMLBuilder.class.getName()).log(Level.INFO, "Set Attribute <" + attribs[0] + "> with value <" + attribs[1] + ">");
					//System.out.println(attribs[0] + "||" + attribs[1]);
					content.attr(attribs[0], attribs[1]);//set attribute value of the attribute Ex: placeholder="Set the identifier"
				} else {
					//Logger.getLogger(HTMLBuilder.class.getName()).log(Level.INFO, "i: " + i);
					if(htmlProperties.get(i).endsWith(".html")) {//HTML file content to inject into the HTML. IMPORTANT: Must be in the same directory !
						File include = new File(input.getParent() + File.separator + htmlProperties.get(i));
						
						Logger.getLogger(HTMLBuilder.class.getName()).log(Level.INFO, "<" + HTMLBuilder.class.getName() + "> HTML File to include is: " + input.getParent() + File.separator + htmlProperties.get(i));
						
						try {
							//System.out.println(Jsoup.parse(include, "UTF-8", "").toString());
							content.html(Jsoup.parse(include, "UTF-8", "").outerHtml());
							
						} catch (IOException e) {
							Logger.getLogger(HTMLBuilder.class.getName()).log(Level.SEVERE, "<" + HTMLBuilder.class.getName() + "> HTML File include parsing impossible: " + include.getAbsolutePath());
							e.printStackTrace();
						}
						
					} else {//Test to inject in HTML tag
						
						if(htmlProperties.get(i).startsWith("html:")) {//Case of html to inject inside the tag
							content.html(htmlProperties.get(i).substring(htmlProperties.get(i).indexOf("html:") + 5));// Set the text, ie the value Ex: <p>my value</p>
						} else {//Case of text to inject inside the tag
							
							content.text(htmlProperties.get(i));// Set the text, ie the value Ex: <p>my value</p>
						}
						
						//System.out.println("i=" + i);
						//System.out.println("tag=" + htmlProperties.get(i));
						//content.html(htmlProperties.get(i));// Set the text, ie the value Ex: <p>my value</p>
					}
				}
			}
		}
		

		return doc.toString();
	}
	
	/**
	 * <p>Same method than buildHtml(input, htmlProperties), but with user connected. So the method has additional Session infos (logon + role) to 
	 * store in the htmlProperties HashMap before calling buildHtml(input, htmlProperties) method.</p>
	 * @param input File representing the html file
	 * @param htmlProperties HasMap representing all the values of the properties file
	 * @param session Current user session
	 * @return Full html code of the page to display 
	 */
	public static String buildHtml(File input, HashMap<String, String> htmlProperties, HttpSession session) {
		
		String[] user = HTTPUtils.getSessionUser(session);//Common HTTP utility to get user information from Session & database
		
		htmlProperties.put("logon", user[0]);
		htmlProperties.put("role", user[1]);
		
		return buildHtml(input,htmlProperties);
	}

	/**
	 * Post-treatment of pages to modify favorites: when manual and/or page are in favorite for current user, set stars in white. If it is not the case, set 
	 * stars in black
	 * @param html Full HTML string document
	 * @param urlForPage Url of the page favorite image
	 * @param urlForManual Url of the manual favorite image
	 * @return Full HTML string document
	 */
	public static String updateFavorites(String html, String urlForPage, String urlForManual) {
		Document doc = null;
		doc = Jsoup.parse(html);//parse the html document
		
		//Icon of the manual
		Element imgManual = doc.getElementById("manualGeneralMenuFavorite");
		imgManual.attr("src", urlForManual);
		
		
		//Icon of the page
		Element imgPage = doc.getElementById("imgMenuFavoritePage");
		imgPage.attr("src", urlForPage);
		
		return doc.toString();
	}
	
	public static Document updateForRegistered(Document doc, String idRegisteredConsult, String idPage, String action) {
		

		
		/**
		 * Select & display applicability
		 */
		
		//System.out.println("Start select & display applicability " + System.currentTimeMillis());
		
		DbRequest query = new DbRequest("select hasDocApplicable from registeredconsult where id=?;");
		query.setParameter(1, Integer.parseInt(idRegisteredConsult));
		
		ResultSet result = query.execute();
		
		boolean applicability = false;
		
		try {
			while(result.next()) {
				applicability = result.getBoolean("hasDocApplicable");				
			}
			
			
			query.close();
			query = null;
			result = null;
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		if(applicability == true) {//The document has applicability

			//System.out.println("Start load applicability " + System.currentTimeMillis());
			
			/**
			 * Get applicability values list 
			 */
			query = new DbRequest("select applicability.htmlvalue as applicabilityvalue from registeredconsulttoapplicability,applicability where registeredconsulttoapplicability.id_applicability=applicability.id and registeredconsulttoapplicability.id_registeredconsult=? and registeredconsulttoapplicability.id_page=?;");
			query.setParameter(1, Integer.parseInt(idRegisteredConsult));
			query.setParameter(2, Integer.parseInt(idPage));
			
			result = query.execute();
			
			StringBuffer applicabilityList = new StringBuffer();
			
			
			try {
				while(result.next()) {
					applicabilityList.append(result.getString("applicabilityvalue"));	
				}
				query.close();
				query = null;
				result = null;
				
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			
			/**
			 * Display decoration for applicability
			 */
			
			//System.out.println("Start display decoration for applicability " + System.currentTimeMillis());
			
			Elements delelements = doc.select(".agapplicabilitydel");
			
			for (Element delelement : delelements) {
				
				Element delelementKey = delelement.select(".agapplicabilitydelkey").first();
				
				if(!applicabilityList.toString().contains(delelementKey.text())) {//DEL applicability 
					delelement.attr("class","w3-row w3-panel");
					Logger.getLogger(HTMLBuilder.class.getName()).log(Level.INFO, "<" + HTMLBuilder.class.getName() + "> DEL value didn't found: " + delelementKey.text() + " => Hide decoration");
					
				} else {
					Logger.getLogger(HTMLBuilder.class.getName()).log(Level.INFO, "<" + HTMLBuilder.class.getName() + "> Applicability DEL is found: " + delelementKey.text());
				}
				
			}
			
			
			Elements addelements = doc.select(".agapplicabilityadd");

			for (Element addelement : addelements) {
				
				Element addelementKey = addelement.select(".agapplicabilityaddkey").first();
				
				if(!applicabilityList.toString().contains(addelementKey.text())) {//ADD applicability 
					addelement.attr("class", "w3-hide");
					Logger.getLogger(HTMLBuilder.class.getName()).log(Level.INFO, "<" + HTMLBuilder.class.getName() + "> ADD value didn't found: " + addelementKey.text() + " => Hide the content");
					
				} else {
					Logger.getLogger(HTMLBuilder.class.getName()).log(Level.INFO, "<" + HTMLBuilder.class.getName() + "> Applicability ADD is found: " + addelementKey.text());
				}
				
			}
			
			
			Elements modifelements = doc.select(".agapplicabilitymodified");

			for (Element modifelement : modifelements) {
				
				Element modifelementKey = modifelement.select(".agapplicabilitymodifiedkey").first();
				
				if(!applicabilityList.toString().contains(modifelementKey.text())) {//ADD applicability 
					modifelement.attr("class","w3-row w3-panel");
					Logger.getLogger(HTMLBuilder.class.getName()).log(Level.INFO, "<" + HTMLBuilder.class.getName() + "> MODIF value didn't found: " + modifelementKey.text() + " => Hide decoration");
					
				} else {
					Logger.getLogger(HTMLBuilder.class.getName()).log(Level.INFO, "<" + HTMLBuilder.class.getName() + "> Applicability MODIF is found: " + modifelementKey.text());
				}
				
			}
			
			
			
			
			
			
			
		} else {//The document has no applicability selected
			
			/**
			 * Display No decoration for applicability DEL/MODIFY & Hide for applicability ADD
			 */

			//System.out.println("Start treatment where no applicability " + System.currentTimeMillis());
			
			Elements delelements = doc.select(".agapplicabilitydel");
			
			for (Element delelement : delelements) {
				
				delelement.attr("class","w3-row w3-panel");
				
			}

			Elements modifelements = doc.select(".agapplicabilitymodified");
			
			for (Element modifelement : modifelements) {
				
				modifelement.attr("class","w3-row w3-panel");
				
			}
			
			
			
			
			
			Elements addelements = doc.select(".agapplicabilityadd");

			for (Element addelement : addelements) {
				
				addelement.attr("class", "w3-hide");
				
			}
			
		}
		
		
		/**
		 * Get codmats => delete those not selected in HTML (page)
		 */
		
		//System.out.println("Start Load CodMats " + System.currentTimeMillis());
		
		query = new DbRequest("select content from hardwarecode,registeredconsulttohardwarecode where registeredconsulttohardwarecode.id_hardwarecode=hardwarecode.id and registeredconsulttohardwarecode.id_registeredconsult=? and registeredconsulttohardwarecode.id_page=?;");
		query.setParameter(1, Integer.parseInt(idRegisteredConsult));
		query.setParameter(2, Integer.parseInt(idPage));
		
		result = query.execute();
		
		StringBuffer hardwareCodeList = new StringBuffer();
		
		try {
			while(result.next()) {
				hardwareCodeList.append(result.getString("content") + ",");	
			}
			query.close();
			query = null;
			result = null;
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		Elements hardwareElements = doc.select(".agdtufmcodmat");
		
		for (Element hardwareElement : hardwareElements) {
			
			if(hardwareCodeList.toString().contains(hardwareElement.text()) == false){//If hardware code not selected, remove it
				
				hardwareElement.remove();
				
			}
			
			
		}
		
		
		/**
	     * Get filters => delete those not selected. Filter Header => unmodifiable
		 */
		
		//System.out.println("Load filters " + System.currentTimeMillis());
		
		query = new DbRequest("select content from filteredarea,registeredconsulttofilteredarea where registeredconsulttofilteredarea.id_filteredarea=filteredarea.id and registeredconsulttofilteredarea.id_registeredconsult=? and registeredconsulttofilteredarea.id_page=?;");
		query.setParameter(1, Integer.parseInt(idRegisteredConsult));
		query.setParameter(2, Integer.parseInt(idPage));
		
		result = query.execute();
		
		StringBuffer filtersList = new StringBuffer();
		
		try {
			while(result.next()) {
				filtersList.append(result.getString("content") + ",");	
			}
			query.close();
			query = null;
			result = null;
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		Elements filtersElements = doc.select(".agdtufilterarea");
		
		for (Element filtersElement : filtersElements) {
			
			
			Element filterAreaKey = filtersElement.select(".agdtufilterareakey").first();
			
			if(filtersList.toString().contains(filterAreaKey.text()) == false){//If filter code not selected, remove it
		
				filtersElement.remove();
				
			}
			
		}
		
		/**
		 * Hide the filter header
		 */
		
		//System.out.println("Start to hide header " + System.currentTimeMillis());
		
		Element filtersHeaderElement = doc.select(".agdtufilterheader").first();
		
		if(filtersHeaderElement != null) {
			filtersHeaderElement.remove();
		}
		
		
		
		
		
		
		/**
		 * PART 1: Background color of operations + enable controls
		 */
		
		//System.out.println("Start activate controls and colors" + System.currentTimeMillis());
		
		Elements elements = doc.select("*[disabled]");//Search for HTML controls disabled
		//Activate controls
		elements.removeAttr("disabled");
		

		/**
		 * Get stored information and modify it
		 */
		query = new DbRequest("select htmltag,htmlid,htmltype,storeddata from registeredconsultvalue where id_registeredconsult=? and id_page=? order by id asc;");
		query.setParameter(1, Integer.parseInt(idRegisteredConsult));
		query.setParameter(2, Integer.parseInt(idPage));
		
		
		result = query.execute();
		
		try {
				while(result.next()) {
				
				/**
				 * get data for:
				 * - agdtucomment.vm
				 * - agdtufmminutes.vm (textarea only)
				 * - agdtuingredients.vm
				 * - agdtuoperation.vm
				 * - agdtuoperators.vm
				 * - agdtutools.vm
				 * Insert information into the textarea 
				 */
				if(result.getString("htmltag").equalsIgnoreCase(HTMLConstants.TAG_TEXTAREA)) {
					doc.getElementById(result.getString("htmlid")).text(result.getString("storeddata"));
				}
				
				
				/**
				 * get data for:
				 * - agdtumultipleparameters.vm
				 * - agdtuparameter.vm
				 * 
				 */
				if(result.getString("htmltag").equalsIgnoreCase(HTMLConstants.TAG_INPUT) && result.getString("htmltype").equalsIgnoreCase(HTMLConstants.ATTR_TYPE_TEXT)) {
	
					if(result.getString("storeddata").equalsIgnoreCase("true")) {
						doc.getElementById(result.getString("htmlid")).attr(HTMLConstants.ATTR_CHECKED, "true");
					}					
				}
				
				/**
				 * get data for:
				 * - agdtuoperation.vm
				 * - agdtuequipementoption.vm
				 * - agdtufmminutes (checkboxes)
				 * 
				 */
				if(result.getString("htmltag").equalsIgnoreCase(HTMLConstants.TAG_INPUT) && result.getString("htmltype").equalsIgnoreCase(HTMLConstants.ATTR_TYPE_RADIO)) {
					
					if(result.getString("storeddata").equalsIgnoreCase("true")) {
						
						doc.getElementById(result.getString("htmlid")).attr(HTMLConstants.ATTR_CHECKED, "true");
					
						
						
						if(result.getString("htmlid").contains("agdtuoperationcontrol") && result.getString("htmlid").contains("radioconform")) {
							
							String[] tagIdentifier = result.getString("htmlid").split("::");
							
							System.out.println("agdtuoperationcontrol" + tagIdentifier[0].substring(21,22) + "::" + tagIdentifier[1]);
							
							Element tagRow = doc.getElementById("agdtuoperationcontrol" + tagIdentifier[0].substring(21,22) + "::" + tagIdentifier[1]);
							
							tagRow.attr("class", tagRow.attr("class") + " " + HTMLConstants.CLASS_COLOR_GREEN);
						}
						
						
					
						if(result.getString("htmlid").contains("agdtuoperationcontrol") && result.getString("htmlid").contains("radionotconform")) {
							String[] tagIdentifier = result.getString("htmlid").split("::");
							
							System.out.println("agdtuoperationcontrol" + tagIdentifier[0].substring(21,22) + "::" + tagIdentifier[1]);
							
							Element tagRow = doc.getElementById("agdtuoperationcontrol" + tagIdentifier[0].substring(21,22) + "::" + tagIdentifier[1]);
							
							tagRow.attr("class", tagRow.attr("class") + " " + HTMLConstants.CLASS_COLOR_RED);
							
						}
	
						
						
						
						if(result.getString("htmlid").contains("agdtuoperationcontrol") && result.getString("htmlid").contains("radionotapplicable")) {
							String[] tagIdentifier = result.getString("htmlid").split("::");
							
							System.out.println("agdtuoperationcontrol" + tagIdentifier[0].substring(21,22) + "::" + tagIdentifier[1]);
							
							Element tagRow = doc.getElementById("agdtuoperationcontrol" + tagIdentifier[0].substring(21,22) + "::" + tagIdentifier[1]);
							
							tagRow.attr("class", tagRow.attr("class") + " " + HTMLConstants.CLASS_COLOR_WHITE);
							
						}
					
					
						/**
						 * for equipment option 
						 */
						if(result.getString("htmlid").contains("agdtuequipmentoptionrowradioconform")) {
							String[] tagIdentifier = result.getString("htmlid").split("::");
							
							Element tagRow = doc.getElementById("agdtuequipmentoptionrow::" + tagIdentifier[1]);
							
							tagRow.attr("class", tagRow.attr("class") + " " + HTMLConstants.CLASS_COLOR_GREEN);
							
						}
						
						
						if(result.getString("htmlid").contains("agdtuequipmentoptionrowradionotconform")) {
							String[] tagIdentifier = result.getString("htmlid").split("::");
							
							Element tagRow = doc.getElementById("agdtuequipmentoptionrow::" + tagIdentifier[1]);
							
							tagRow.attr("class", tagRow.attr("class") + " " + HTMLConstants.CLASS_COLOR_RED);
							
						}
						
						
					}
	
					
				}
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println("End of updateForRegistered() " + System.currentTimeMillis());
		
		return doc;
	}
	
	
	/**
	 * Transforms a combination of HTML + variables in properties files or in String representation
	 * @param keyValue corresponding to the properties key variable containing a mix of HTML and ${variable-name} variable
	 * @param map HashMap containing 2 String values: The Key with the name of the variable (syntax -> ${variable-name}) and the value of the variable
	 * @return The HTML with variable values
	 */
	public static String html(String keyValue, HashMap<String,String> map) {
		
		for(String var : map.keySet()) {
			
			keyValue = keyValue.replace(var, map.get(var));

		}
		
		return keyValue;
	}
	
	/**
	 * Transforms a combination of HTML + ? characters representing variables to update with values
	 * @param keyValue String representation of HTML
	 * @param values Array of variable values
	 * @return String representation of HTML with variable values injected inside
	 */
	public static String html(String keyValue, String[] values) {
		
		String part1 = null;
		String part2 = null;
		
		for (int i = 0; i < values.length; i++) {
			part1 = keyValue.substring(0,keyValue.indexOf("?"));
			part2 = keyValue.substring(keyValue.indexOf("?") + 1);
			keyValue = part1 + values[i] + part2;
		}
		
		return keyValue;
	}	
	
	
	
	
}






