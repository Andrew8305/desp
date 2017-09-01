package org.apel.desp.agent.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apel.desp.agent.monitor.MonitorService;
import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKCommandCode;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.util.FTPUtil;
import org.apel.desp.commons.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;

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
	@Autowired
	private MonitorService moitorService;
	
	@Override
	public ZKCommandCode commandCode() {
		return ZKCommandCode.PULL_FILE;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(ZKCommand zkCommand, ZKCommandCallback zkCommandCallback) {
		ExecutorService pool = poolMap.getPoolOrSet(zkCommand.getAppId());
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map param = JSON.parseObject((zkCommand.getParam()), Map.class);
					String appId = param.get("appId").toString();
					String jarRealName = param.get("jarRealName").toString();
					String remoteJarPath = appId + "/" + jarRealName;
					File appDir = new File(MonitorService.LOCAL_APP_ROOT_DIR.getPath() + "/" + appId);
					String localJarFilePath = appDir.getPath() + "/" + appId + ".jar";
					boolean flag = false;
					if (!appDir.exists()){
						appDir.mkdirs();
						flag = true;
					}else{//如果本地存在jar，则校验checksum,如果checksum一致则直接更新状态，不需要再进行拉取
						File appJarFile = new File(localJarFilePath);
						if (appJarFile.exists()){
							String localChecksum = FileUtil.checkSum(appJarFile.getPath());
							String remoteChecksum = ftpUtil.getChecksum(remoteJarPath);
							if (!localChecksum.toUpperCase().equals(remoteChecksum)){
								flag = true;
							}
						}
					}
					//本地不存在jar或者是本地与远程的文件checksum不一致，则重新进行拉取
					try (OutputStream fos = new FileOutputStream(localJarFilePath);){
						if (flag){
							ftpUtil.retriveFile(remoteJarPath, fos);
							LOG.info("拉取文件成功");
						}
						//回调
						zkCommandCallback.call();
						//更新app状态
						moitorService.changeAppStatusAndUpdate(appId, SystemConsist.APPINSTANCE_STATUS_STOPED);
					} catch (Exception e) {
						Throwables.throwIfUnchecked(e);
					}
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error("拉取文件出错" + e.getMessage());
				}
			}
		});
	}
	
}
