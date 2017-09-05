package org.apel.desp.commons.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;

/**
 * linux系统操作类
 * 
 * @author lijian
 *
 */
@Component
public class LinuxOperator implements OSOperator {

	private final static Logger LOG = LoggerFactory
			.getLogger(LinuxOperator.class);

	@Override
	public String identifier() {
		return "linux";
	}

	@Override
	public boolean checkAppRuning(String appId) {
		if (getPID(appId) == -1) {
			return false;
		}
		return true;
	}

	@Override
	public int getPID(String appId) {
		int pid = -1;
		try {
			String[] cmd = { "/bin/sh", "-c",
					"jps | grep " + appId + ".jar | awk '{print $1}'" };
			Process p = Runtime.getRuntime().exec(cmd);
			String line;
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			while (null != (line = br.readLine())) {
				if (StringUtils.isEmpty(line)) {
					continue;
				}
				try {
					Integer.valueOf(line.trim());
				} catch (Exception e) {
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
			String[] cmd = { "/bin/sh", "-c", "kill -9 " + pid };
			Process p = Runtime.getRuntime().exec(cmd);
			String line;
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			while (null != (line = br.readLine())) {
				if ("".equals(line)) {
					continue;
				}
				try {
					LOG.info(line);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
	}

	@Override
	public void startApp(String appId) {
		new Thread() {

			@Override
			public void run() {
				try {
					String bootFilePath = "";
					String appDirPath = System.getProperty("user.dir") + "/apps/" + appId;
					File appDir = new File(appDirPath);
					File[] bootFiles = appDir.listFiles(new FileFilter() {
						@Override
						public boolean accept(File file) {
							if (file.getName().endsWith(".sh")){
								return true;
							}
							return false;
						}
					});
					if (bootFiles.length == 0){//创建启动文件
						File bootFile = new File(appDirPath, "start.sh");
						try (FileOutputStream fw = new FileOutputStream(bootFile)){
							StringBuffer sb = new StringBuffer();
							sb.append("#!/bin/bash\n");
							sb.append("cd " + appDir.getPath() + "\n");
							sb.append("nohup java -Xms128M -Xmx1024M -jar " + appDirPath + "/" + appId + ".jar &\n");
							IOUtils.write(sb.toString(), fw, Charsets.UTF_8);
							bootFilePath = bootFile.getPath();
							bootFile.setExecutable(true);
							bootFile.setReadable(true);
							bootFile.setWritable(true);
						}
					}else{//调用启动文件
						bootFilePath = bootFiles[0].getPath();
					}
					
					String[] cmd = { "sh", bootFilePath};
					Process p = Runtime.getRuntime().exec(cmd);
					String line;
					InputStream is = p.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
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

}
