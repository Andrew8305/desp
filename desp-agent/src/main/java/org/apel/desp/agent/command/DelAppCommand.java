package org.apel.desp.agent.command;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apel.desp.agent.monitor.MonitorService;
import org.apel.desp.commons.consist.ZKCommandCode;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.os.OperationSystemMananger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
@Component
public class DelAppCommand implements ZKCommander{

	private static Logger LOG = LoggerFactory.getLogger(DelAppCommand.class);
	
	@Autowired
	private ZKCommandPool poolMap;
	@Autowired
	private MonitorService moitorService;
	@Autowired
	private OperationSystemMananger operationSystemManager;
	
	@Override
	public ZKCommandCode commandCode() {
		return ZKCommandCode.DEL_APP;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void execute(ZKCommand zkCommand, ZKCommandCallback zkCommandCallback) {
		ExecutorService pool = poolMap.getPoolOrSet(zkCommand.getAppId());
		pool.execute(new Runnable() {
			@Override
			public void run() {
				Map param = JSON.parseObject((zkCommand.getParam()), Map.class);
				String appId = param.get("appId").toString();
				if (operationSystemManager.checkAppRuning(appId)){//如果app已经启动，则不做任何操作,否则进行启动
					LOG.info("停止应用：" + appId);
					operationSystemManager.killProcess(operationSystemManager.getPID(appId));
				}
				//删除本地文件
				try {
					TimeUnit.SECONDS.sleep(3);//休眠3s后再删除本地文件，避免进程未停止会占用文件(windows上)
					FileUtils.deleteDirectory(new File(MonitorService.LOCAL_APP_ROOT_DIR + "/" + appId));
				} catch (Exception e) {
					e.printStackTrace();
					Throwables.throwIfUnchecked(e);
				}
				zkCommandCallback.call();
			}
		});
	}

}
