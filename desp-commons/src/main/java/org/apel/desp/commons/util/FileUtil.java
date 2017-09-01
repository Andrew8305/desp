package org.apel.desp.commons.util;

import java.io.File;
import java.io.IOException;

import org.apache.curator.shaded.com.google.common.base.Throwables;

import com.twmacinta.util.MD5;

public class FileUtil {

	/**
	 * 获取本地文件md5 checksum
	 * @param filePath 文件路径
	 */
	public static String checkSum(String filePath) {
		String checksum = null;
		try {
			checksum = MD5.asHex(MD5.getHash(new File(filePath)));
		} catch (IOException e) {
			Throwables.throwIfUnchecked(e);
		}
		return checksum;
	}
	
}
