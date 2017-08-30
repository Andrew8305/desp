package org.apel.desp.agent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Strings;

public class Test3 {

	public static void main(String[] args) throws Exception {
		// 0E73BC845884D6D0D927C22134F24C9F
		System.out.println("0E73BC845884D6D0D927C22134F24C9F");
		System.out.println(Strings.padStart(checkSum("D:/1.zip"), 32, '0'));
	}

	public static String checkSum(String path){
        String checksum = null;
        try {
            FileInputStream fis = new FileInputStream(path);
            MessageDigest md = MessageDigest.getInstance("MD5");
          
            //Using MessageDigest update() method to provide input
            byte[] buffer = new byte[8192];
            int numOfBytesRead;
            while( (numOfBytesRead = fis.read(buffer)) > 0){
                md.update(buffer, 0, numOfBytesRead);
            }
            byte[] hash = md.digest();
            checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
        } catch (IOException ex) {
           
        } catch (NoSuchAlgorithmException ex) {
            
        }
          
       return checksum;
    }

	public static String checkSumApacheCommons(String file) {
		String checksum = null;
		try {
			checksum = DigestUtils.md5Hex(new FileInputStream(file));
		} catch (IOException ex) {
			
		}
		return checksum;
	}

}
