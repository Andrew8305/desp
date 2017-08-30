package org.apel.desp.commons.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

@Component
public class FTPUtil{

	private static final Logger LOG = LoggerFactory.getLogger(FTPUtil.class);

	@Value("${desp.ftp.host:127.0.0.1}")
	private String host;
	@Value("${desp.ftp.port:2121}")
	private int port;
	@Value("${desp.ftp.username:admin}")
	private String userName;
	@Value("${desp.ftp.password:admin}")
	private String password;

	/**
	 * 初始化ftp客户端
	 */
	public FTPClient init(){
		FTPClient ftp = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();
		ftp.configure(config);
		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
		ftp.setControlEncoding("UTF-8");
		boolean verified = true;
		try {
			int reply;
			if (port > 0) {
				ftp.connect(host, port);
			} else {
				ftp.connect(host);
			}
			LOG.info("连接ftp服务器 to {} on {}", host, port > 0 ? port : ftp.getDefaultPort());
			reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                LOG.error("ftp服务器 拒绝连接");
            }else{
            	 LOG.info("已经连接ftp服务器");
            }
            
            //ftp服务器认证
            if (!ftp.login(userName, password)) {
				verified = false;
				LOG.error("ftp认证失败，账号和密码错误");
				ftp.logout();
			}else{
				LOG.info("ftp认证成功");
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
			}
		} catch (IOException e) {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException f) {
					// do nothing
				}
			}
			LOG.error("不能够连接ftp服务器: " + e.getMessage());
			e.printStackTrace();
		}
		if (ftp.isAvailable() && ftp.isConnected() && verified){
			return ftp;
		}else{
			throw new RuntimeException("ftp连接初始化错误，请检查连接");
		}
	}
	
	
	/**
	 * 向ftp上传文件
	 * @param localFile 本地文件路径
	 * @param remoteDir ftp目录
	 * @param remoteFileName ftp文件名
	 */
	public void storeFile(String localFile, String remoteDir, String remoteFileName){
		try {
			storeFile(new FileInputStream(localFile), remoteDir, remoteFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
	}
	
	/**
	 * 向ftp上传文件--根目录
	 * @param localFile 本地文件路径
	 * @param remoteFileName ftp文件名
	 */
	public void storeFile(String localFile, String remoteFileName){
		storeFile(localFile, "", remoteFileName);
	}
	
	/**
	 * 向ftp上传文件--根目录
	 * @param input 本地文件输入流
	 * @param remoteFileName ftp文件名
	 */
	public void storeFile(InputStream input, String remoteFileName){
		storeFile(input, "", remoteFileName);
	}
	
	/**
	 * 向ftp上传文件
	 * @param input 本地文件输入流
	 * @param remoteDir ftp目录
	 * @param remoteFileName ftp文件名
	 */
	public void storeFile(InputStream input, String remoteDir, String remoteFileName){
		FTPClient ftp = init();
		try {
			if (StringUtils.isNotEmpty(remoteDir)){
				boolean dirExist = ftp.changeWorkingDirectory(remoteDir);
				if (!dirExist){
					mkdir(remoteDir, ftp);
					ftp.cwd(remoteDir);
				}
			}
			ftp.storeFile(remoteFileName, input);
			
			int replyCode = ftp.getReplyCode();
			if (FTPReply.isNegativePermanent(replyCode)){
				throw new RuntimeException("存储文件失败");
			}
		}catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}finally{
			closeFTPClient(ftp);
			if (input != null){
				try {
					input.close();
				} catch (IOException e) {
					
				}
			}
		}
	}
	
	/**
	 * 获取ftp文件流
	 * @param remoteFilePath ftp文件路径
	 * @param os 输出流
	 */
	public void retriveFile(String remoteFilePath, OutputStream os){
		FTPClient ftp = init();
		try {
			ftp.retrieveFile(pureDirFormat(remoteFilePath), os);
			int replyCode = ftp.getReplyCode();
			if (FTPReply.isNegativePermanent(replyCode)){
				throw new RuntimeException("读取文件失败");
			}
		} catch (IOException e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}finally{
			closeFTPClient(ftp);
			if (os != null){
				try {
					os.close();
				} catch (IOException e) {
					
				}
			}
		}
	}
	
	
	/**
	 * 删除ftp指定文件
	 * @param remoteFile ftp文件路径
	 */
	public void deleteFile(String remoteFilePath){
		FTPClient ftp = init();
		try {
			ftp.deleteFile(pureDirFormat(remoteFilePath));
		} catch (IOException e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}finally{
			closeFTPClient(ftp);
		}
	}
	
	/**
	 * 获取ftp文件MD5 checksum
	 * @param remoteFilePath ftp文件路径
	 */
	public String getChecksum(String remoteFilePath){
		String checksum = "";
		try {
			FTPClient ftp = init();
			if (FTPReply.isPositiveCompletion(ftp.sendCommand("md5", remoteFilePath))){
				String[] reply = ftp.getReplyStrings();
				checksum = reply[1];
			}
		} catch (IOException e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
		return checksum;
	}
	
	//关闭ftp客户端连接
	private void closeFTPClient(FTPClient ftp){
		if (ftp.isConnected()) {
			try {
				ftp.noop(); 
	            ftp.logout();
				ftp.disconnect();
			} catch (IOException f) {
				// do nothing
			}
		}
	}
	
	
	//递归创建ftp文件夹
	private void mkdir(String dir, FTPClient ftp) throws IOException{
		if (StringUtils.isEmpty(dir)){
			return;
		}
		int count = 0;
		dir = pureDirFormat(dir);
		mkdirRecursion(dir, dir.split("/"), ftp, count);
	}
	
	//将路径格式转为纯净格式(前后没有/)
	private String pureDirFormat(String path){
		path = path.startsWith("/") ? path.substring(1, path.length()) : path;
		path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
		return path;
	}
	
	private void mkdirRecursion(String rootDir, String[] dirs, FTPClient ftp, int count) throws IOException{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <= count; i++) {
			sb.append(dirs[i]);
			if (i != count){
				sb.append("/");
			}
		}
		ftp.mkd(sb.toString());
		if (rootDir.equals(sb.toString())){
			return;
		}
		count++;
		mkdirRecursion(rootDir, dirs, ftp, count);
	}
	

}
