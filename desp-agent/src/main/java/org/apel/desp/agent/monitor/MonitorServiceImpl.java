package org.apel.desp.agent.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKNodePath;
import org.apel.desp.commons.monitor.AgentMonitorInfo;
import org.apel.desp.commons.monitor.ApplicationMonitorInfo;
import org.apel.desp.commons.os.OperationSystemMananger;
import org.apel.desp.commons.util.NetUtil;
import org.apel.desp.commons.util.ZKConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;

@Component
public class MonitorServiceImpl implements MonitorService{
	
	private static Logger LOG = LoggerFactory.getLogger(MonitorServiceImpl.class);
	private static final String LOCAL_AGENT_PATH = ZKNodePath.ZK_ACTIVE_AGENTS_PATH + "/" + NetUtil.getLocalPureMac();
	
	static{
		if (!LOCAL_APP_ROOT_DIR.exists()){
			LOCAL_APP_ROOT_DIR.mkdir();
		}
	}

	@Value("${agentVersion:none}")
	private String agentVersion;
	@Autowired
	private ZKConnector zkConnector;
	@Autowired
	private OperationSystemMananger operationSystemManager;
	
	private AgentMonitorInfo getZkAgentMonitorInfo(){
		AgentMonitorInfo agentMonitorInfo = null;
		try {
			byte[] dataBytes = zkConnector.getClient().getData().forPath(LOCAL_AGENT_PATH);
			agentMonitorInfo = JSON.parseObject(new String(dataBytes), AgentMonitorInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
		return agentMonitorInfo;
	}
	
	@Override
	public void updateZkAgentMonitorInfo(AgentMonitorInfo agentMonitorInfo){
		try {
			String monitorInfo = JSON.toJSONString(agentMonitorInfo);
			zkConnector.getClient().setData().forPath(LOCAL_AGENT_PATH, monitorInfo.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
	}
	
	@Override
	public AgentMonitorInfo changeAppStatus(String appId, int status){
		AgentMonitorInfo agentMonitorInfo = getZkAgentMonitorInfo();
		changeAppStatus(agentMonitorInfo, appId, status);
		return agentMonitorInfo;
	}
	
	protected void changeAppStatus(AgentMonitorInfo agentMonitorInfo, String appId, int status){
		try {
			if (agentMonitorInfo.getApps() == null){
				agentMonitorInfo.setApps(new ArrayList<>());
			}
			boolean flag = false;
			for (ApplicationMonitorInfo applicationMonitorInfo : agentMonitorInfo.getApps()) {
				if (applicationMonitorInfo.getAppId().equals(appId)){
					applicationMonitorInfo.setStatus(status);
					flag = true;
					break;
				}
			}
			if (!flag){
				ApplicationMonitorInfo applicationMonitorInfo = new ApplicationMonitorInfo();
				applicationMonitorInfo.setAppId(appId);
				applicationMonitorInfo.setStatus(status);
				agentMonitorInfo.getApps().add(applicationMonitorInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
	}

	@Scheduled(cron = "0 */1 * * * ?")
	@Override
	public void scheduleSendMonitorInfo() {
		//更新物理机基础信息
		AgentMonitorInfo zkAgentMonitorInfo = getZkAgentMonitorInfo();
		zkAgentMonitorInfo.setAgentVersion(agentVersion);
		zkAgentMonitorInfo.setStatus(SystemConsist.AGENT_STATUS_RUNNING);
		
		//检查app状态，如果有宕机现象，则通知，检查的前提是在zk上没有执行命令
		//如果有执行的命令，不会更新app状态，由命令执行过程之后进行更新，避免定时程序与命令执行冲突
		for (File appDir : LOCAL_APP_ROOT_DIR.listFiles()) {
			String appId = appDir.getName();
			String commandsPath = ZKNodePath.ZK_COMMONDS_PATH + "/" + NetUtil.getLocalPureMac();
			try {
				List<String> children = zkConnector.getClient().getChildren().forPath(commandsPath);
				if(children.size() != 0){//有命令不执行app更新
					continue;
				}
				//如果没有命令，则判断app是否已启动，如果没有启动将状态设停止，如果启动了将状态更新为运行中
				//此步骤用于对app异常宕机的状态进行通知
				boolean runing = operationSystemManager.checkAppRuning(appId);
				if (runing){
					changeAppStatus(zkAgentMonitorInfo, appId, SystemConsist.APPINSTANCE_STATUS_RUNNING);
				}else{
					changeAppStatus(zkAgentMonitorInfo, appId, SystemConsist.APPINSTANCE_STATUS_STOPED);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		updateZkAgentMonitorInfo(zkAgentMonitorInfo);
	}

	@Override
	public void changeAppStatusAndUpdate(String appId, int status) {
		AgentMonitorInfo agentMonitorInfo = changeAppStatus(appId, status);
		updateZkAgentMonitorInfo(agentMonitorInfo);
	}

}
