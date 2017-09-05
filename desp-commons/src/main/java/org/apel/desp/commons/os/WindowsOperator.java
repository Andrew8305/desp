package org.apel.desp.commons.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

/**
 * 
 * windows系统操作类
 * @author lijian
 *
 */
@Component
public class WindowsOperator implements OSOperator{
	
	private final static Logger LOG = LoggerFactory.getLogger(WindowsOperator.class);

	@Override
	public boolean checkAppRuning(String appId) {
		if (getPID(appId) == -1){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public String identifier() {
		return "windows";
	}
	
	@Override
	public int getPID(String appId) {
		int pid = -1;
		try {
			ProcessBuilder builder = new ProcessBuilder(
					"wmic", "process", "where", "name like '%java%' and CommandLine Like '%-jar " + appId + ".jar%'",
					"get", "processid");
			builder.redirectErrorStream(true);
			Process p = builder.start();
			String line;
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			while(null != (line = br.readLine())){
				if (StringUtils.isEmpty(line)){
					continue;
				}
				try{
					Integer.valueOf(line.trim());
				}catch(Exception e){
					continue;
				}
				pid = Integer.valueOf(line.trim());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return pid;
	}
	
	@Override
	public void killProcess(int pid) {
		try {
			ProcessBuilder builder = new ProcessBuilder(
					"taskkill", "/F", "/PID", String.valueOf(pid), "/T");
			builder.redirectErrorStream(true);
			Process p = builder.start();
			String line;
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"GBK"));
			while(null != (line = br.readLine())){
				LOG.debug(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
	}
	
	@Override
	public void startApp(String appId) {
		new Thread(){
			@Override
			public void run() {
				try {
					String bootFilePath = "";
					String appDirPath = System.getProperty("user.dir") + "/apps/" + appId;
					File appDir = new File(appDirPath);
					File[] bootFiles = appDir.listFiles(new FileFilter() {
						@Override
						public boolean accept(File file) {
							if (file.getName().endsWith(".bat")){
								return true;
							}
							return false;
						}
					});
					if (bootFiles.length == 0){//创建启动文件
						File bootFile = new File(appDirPath, "start.bat");
						try (FileOutputStream fw = new FileOutputStream(bootFile)){
							StringBuffer sb = new StringBuffer();
							sb.append("cd " + appDir.getPath() + "\r\n");
							sb.append("java -Xms128M -Xmx1024M -jar " + appId + ".jar\r\n");
							IOUtils.write(sb.toString(), fw, Charsets.UTF_8);
							bootFilePath = bootFile.getPath();
						}
					}else{//调用启动文件
						bootFilePath = bootFiles[0].getPath();
					}
					
					ProcessBuilder builder = new ProcessBuilder(
							"cmd", "/c", bootFilePath);
					builder.redirectErrorStream(true);
					Process p = builder.start();
					String line;
					InputStream is = p.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is,"GBK"));
					while (null != (line = br.readLine())){
						LOG.info(line);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Throwables.throwIfUnchecked(e);
				}
			}
			
		}.start();
	}
	
	public static void main(String[] args) {
		WindowsOperator operator = new WindowsOperator();
		operator.killProcess(operator.getPID("log"));
	}
	

}
