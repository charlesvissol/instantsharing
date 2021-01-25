package org.angrybee.is.crypt;

import java.io.File;

/**
 * Class to check the CryptoUtils class
 * @author Openjdev.org
 * <pre>
 * //Get length of String 
 *  String key = "$!&#�$#�&ee�e%&#$!&#�$#�&ee�e%&#";
 *  System.out.println(key.getBytes().length);
 *  
 * </pre>
 */
public class MainCrypt {

	public static void main(String[] args) {
		
        File inputFile = new File("C:\\Users\\A094614\\Downloads\\keys\\crypto-162.zip");
        File encryptedFile = new File("C:\\Users\\A094614\\Downloads\\keys\\crypto-162.encrypt.zip");
        File decryptedFile = new File("C:\\Users\\A094614\\Downloads\\keys\\crypto-162.decrypt.zip");
         
        try {
        	
        	String pswd = CryptoUtils.initPassword();
        	
            CryptoUtils.encrypt(pswd, inputFile, encryptedFile);
            CryptoUtils.decrypt(pswd, encryptedFile, decryptedFile);
            
            
            
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

	}

}
