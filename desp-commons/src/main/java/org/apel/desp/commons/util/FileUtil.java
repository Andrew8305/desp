package org.apel.desp.commons.util;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.curator.shaded.com.google.common.base.Throwables;

public class FileUtil {

	/**
	 * 获取本地文件md5 checksum
	 * @param file 文件路径
	 */
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
