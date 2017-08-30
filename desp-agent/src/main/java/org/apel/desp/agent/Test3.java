package org.apel.desp.agent;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.curator.shaded.com.google.common.base.Throwables;

import com.google.common.base.Strings;

public class Test3 {

	public static void main(String[] args) throws Exception {
		// 0E73BC845884D6D0D927C22134F24C9F
//		System.out.println("0E73BC845884D6D0D927C22134F24C9F");
		System.out.println(Strings.padStart(checkSum("D:/1.txt"), 32, '0'));
		System.out.println(checkSumApacheCommons("D:/1.txt"));
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
		try(FileInputStream fis = new FileInputStream(file)) {
			checksum = DigestUtils.md5Hex(fis);
		} catch (IOException e) {
			Throwables.throwIfUnchecked(e);
		}
		return checksum;
	}

}
