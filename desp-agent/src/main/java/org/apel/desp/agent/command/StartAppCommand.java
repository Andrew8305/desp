package org.apel.desp.agent.command;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apel.desp.agent.monitor.MonitorService;
import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKCommandCode;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.os.OperationSystemMananger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;

public class StartAppCommand implements ZKCommander{

	private static Logger LOG = LoggerFactory.getLogger(StartAppCommand.class);
	
	@Autowired
	private ZKCommandPool poolMap;
	@Autowired
	private MonitorService moitorService;
	@Autowired
	private OperationSystemMananger operationSystemManager;
	
	@Override
	public ZKCommandCode commandCode() {
		return ZKCommandCode.START_APP;
	}

	@Override
	public void execute(ZKCommand zkCommand, ZKCommandCallback zkCommandCallback) {
		ExecutorService pool = poolMap.getPoolOrSet(zkCommand.getAppId());
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				Map param = JSON.parseObject((zkCommand.getParam()), Map.class);
				String appId = param.get("appId").toString();
				if (!operationSystemManager.checkAppRuning(appId)){//如果app已经启动，则不做任何操作,否则进行启动
					operationSystemManager.startApp(appId);
					zkCommandCallback.call();
					moitorService.changeAppStatus(appId, SystemConsist.APPINSTANCE_STATUS_RUNNING);
				}
			}
		});
	}

}
