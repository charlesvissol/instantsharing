package org.openjdev.is.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.RandomStringUtils;




/**
 * Utility class to encrypt and decrypt data transfered by a user
 * @author Openjdev.org
 *
 * <p>Usage:</p>
 * <pre>
 *    public static void main(String[] args) {
 *       File inputFile = new File("C:\\Users\\.\\Downloads\\keys\\doc.pdf");
 *       File encryptedFile = new File("C:\\Users\\.\\Downloads\\keys\\doc.encrypt.pdf");
 *       File decryptedFile = new File("C:\\Users\\.\\Downloads\\keys\\doc.decrypt.pdf");
 *        
 *       try {
 *       
 *           String key = CryptoUtils.initPassword();//Generate pasword
 *       
 *           CryptoUtils.encrypt(key, inputFile, encryptedFile);//Encrypt
 *           CryptoUtils.decrypt(key, encryptedFile, decryptedFile);//Decrypt
 *       } catch (CryptoException ex) {
 *           System.out.println(ex.getMessage());
 *           ex.printStackTrace();
 *       }
 *  }
 *
 *
 * </pre>
 *
 */

public class CryptoUtils {
	
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
 
    /**
     * Password generator in 32 bytes
     * @return String password
     */
    public static String initPassword() {
    	
    	String key = RandomStringUtils.randomAlphanumeric(32);
    	
    	return key;
    }
    
    
    public static void encrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(key, Cipher.ENCRYPT_MODE, inputFile, outputFile);
    }
 
    public static void decrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(key, Cipher.DECRYPT_MODE, inputFile, outputFile);
    }
 
    private static void doCrypto(String key, int cipherMode, File inputFile,
            File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
             
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }	
	
}
