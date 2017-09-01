package org.apel.desp.commons.os;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
	
	public static void main(String[] args) throws Exception {
		
	}

}
