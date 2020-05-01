package org.openjdev.is.commons;

import java.util.ResourceBundle;

/**
 * HTML constants to help to store information about tag and also to 
 * process
 * 
 * @author Charles Vissol - ArianeGroup - ConfluenceViewer Project
 *
 */
public class HTMLConstants {
	
	private static ResourceBundle resources = ResourceBundle.getBundle(HTMLConstants.class.getName());
	
	
	/**
	 * Constant representing an input tag
	 */
	public static String TAG_INPUT = resources.getString("tag.input");
	
	/**
	 * Constant representing a textarea tag
	 */
	public static String TAG_TEXTAREA = resources.getString("tag.textarea");
	
	/**
	 * Constant representing a type attribute equals to text
	 */
	public static String ATTR_TYPE_TEXT = resources.getString("attr.type.text");
	
	/**
	 * Constant representing a type attribute equals to checkbox
	 */
	public static String ATTR_TYPE_CHECKBOX = resources.getString("attr.type.checkbox");
	
	/**
	 * Constant representing a type attribute equals to radio 
	 */
	public static String ATTR_TYPE_RADIO = resources.getString("attr.type.radio");
	
	/**
	 * Constant for tags wihtout attribute 
	 */
	public static String ATTR_NOTHING = resources.getString("attr.nothing");
	
	
	public static String ATTR_CHECKED = resources.getString("tag.attr.checked");
	
	public static String CLASS_COLOR_RED = resources.getString("w3.color.red");
	
	public static String CLASS_COLOR_GREEN = resources.getString("w3.color.green");
	
	public static String CLASS_COLOR_WHITE = resources.getString("w3.color.white");
	
}
