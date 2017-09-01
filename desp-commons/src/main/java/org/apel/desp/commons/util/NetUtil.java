package org.apel.desp.commons.util;

import java.net.InetAddress;
import java.net.NetworkInterface;

import org.apache.curator.shaded.com.google.common.base.Throwables;

public class NetUtil {

	/**
	 * 获取本地mac地址(有破折号)
	 */
	public static String getLocalMac()  {
		//获取网卡，获取地址
		StringBuffer sb = new StringBuffer("");
		try {
			byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
			
			for(int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append("-");
				}
				//字节转换为整数
				int temp = mac[i]&0xff;
				String str = Integer.toHexString(temp);
				if (str.length() == 1) {
					sb.append("0"+str);
				}else {
					sb.append(str);
				}
			}
		} catch (Exception e) {
			Throwables.throwIfUnchecked(e);
		}
		return sb.toString().toUpperCase();
	}
	
	/**
	 * 获取本地mac地址(无破折号)
	 */
	public static String getLocalPureMac(){
		return getLocalMac().replaceAll("-", "");
	}
	
	/**
	 * 获取本地mac地址(无破折号)
	 */
	public static String macPureToRaw(String pureMacAddress){
		char[] charArray = pureMacAddress.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < charArray.length; i++) {
			if (i != 0 && i % 2 == 0){
				sb.append('-');
			}
			sb.append(charArray[i]);
		}
		return sb.toString();
	}
	
}
