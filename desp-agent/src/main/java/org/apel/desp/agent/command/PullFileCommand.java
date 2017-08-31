package org.apel.desp.agent.command;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apel.desp.commons.consist.ZKCommandCode;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/**
 * 拉取文件命令
 * @author lijian
 *
 */
@Component
public class PullFileCommand implements ZKCommander{

	private static Logger LOG = LoggerFactory.getLogger(PullFileCommand.class);
	
	@Autowired
	private ZKCommandPool poolMap;
	@Autowired
	private FTPUtil ftpUtil;
	
	@Override
	public ZKCommandCode commandCode() {
		return ZKCommandCode.PULL_FILE;
	}

	@Override
	public void execute(ZKCommand zkCommand, ZKCommandCallback zkCommandCallback) {
		ExecutorService pool = poolMap.getPoolOrSet(zkCommand.getAppId());
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				Map param = JSON.parseObject((zkCommand.getParam()), Map.class);
				String appId = param.get("appId").toString();
				String jarRealName = param.get("jarRealName").toString();
				try (OutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/" + jarRealName);){
					ftpUtil.retriveFile(appId + "/" + jarRealName, fos);
					LOG.info("拉取文件成功");
				} catch (Exception e) {
					e.printStackTrace();
				}
				zkCommandCallback.call();
			}
		});
	}
	

}
