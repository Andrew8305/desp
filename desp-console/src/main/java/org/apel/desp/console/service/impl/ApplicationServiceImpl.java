package org.apel.desp.console.service.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKCommandCode;
import org.apel.desp.commons.exception.AgentNotRunningException;
import org.apel.desp.commons.exception.ApplicationStatusIllegleException;
import org.apel.desp.commons.util.FTPUtil;
import org.apel.desp.console.dao.AppInstanceRepository;
import org.apel.desp.console.dao.CommandRepository;
import org.apel.desp.console.dao.DeploySerialRepository;
import org.apel.desp.console.dao.MachineInstanceRepository;
import org.apel.desp.console.domain.AppInstance;
import org.apel.desp.console.domain.Application;
import org.apel.desp.console.domain.CommandEntity;
import org.apel.desp.console.domain.DeploySerial;
import org.apel.desp.console.domain.MachineInstance;
import org.apel.desp.console.service.ApplicationService;
import org.apel.desp.console.service.CommandService;
import org.apel.gaia.infrastructure.impl.AbstractBizCommonService;
import org.apel.gaia.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;

@Service
@Transactional
public class ApplicationServiceImpl extends AbstractBizCommonService<Application, String> implements ApplicationService{

	@Autowired
	private FTPUtil ftpUtil;
	@Autowired
	private DeploySerialRepository deploySerialRepository;
	@Autowired
	private AppInstanceRepository appInstanceRepository;
	@Autowired
	private MachineInstanceRepository machineInstanceRepository;
	@Autowired
	private CommandRepository commandRepository;
	@Autowired
	private CommandService commandService;
	
	@Override
	public void uploadJar(InputStream is, String fileName, String id) {
		//先检查之前的jar包是否有发布流水引用，如果没有则需要删除jar
		Application application = findById(id);
		List<String> deploySerials = deploySerialRepository.findByJarRealName(application.getJarRealName());
		if (CollectionUtils.isEmpty(deploySerials) && StringUtils.isNotEmpty(application.getRemoteJarPath())){
			ftpUtil.deleteFile(application.getRemoteJarPath());
		}
		//先保存本地数据库，然后将流嫁接给ftp
		application.setJarName(fileName);
		String remoteJarPath = application.getAppId();
		String jarRealName = UUIDUtil.uuid() + ".jar";
		application.setJarRealName(jarRealName);
		application.setRemoteJarPath(remoteJarPath + "/" + jarRealName);
		update(application);
		ftpUtil.storeFile(is, remoteJarPath, jarRealName);
	}

	@Override
	public void deploy(String[] mids, String appPrimary) {
		Application application = null;
		StringBuffer agents = new StringBuffer();
		for (String mid : mids) {
			//创建应用和物理机器实例的关联
			Object[] createApplicationAndMachineRelation = createApplicationAndMachineRelation(appPrimary, mid);
			application = (Application)createApplicationAndMachineRelation[0];
			MachineInstance mi = (MachineInstance)createApplicationAndMachineRelation[1];
			
			//写入发布流水
			createDeploySerial(application, mi);
			
			agents.append(mi.getMacAddress().replaceAll("-", "") + ",");
		}
		if (application != null){
			//写入命令，操作zk
			CommandEntity commandEntity = new CommandEntity();
			commandEntity.setStatus(SystemConsist.COMMAND_EXE_STATUS_NONE);
			commandEntity.setZkCommandCode(Integer.valueOf(ZKCommandCode.PULL_FILE.toString()));
			commandEntity.setAppId(application.getAppId());
			commandEntity.setCreateDate(new Date());
			Map<String, String> param = new HashMap<>();
			param.put("appId", application.getAppId());
			param.put("jarRealName", application.getJarRealName());
			commandEntity.setParam(JSON.toJSONString(param));
			commandEntity.setAgents(agents.subSequence(0, agents.length() - 1).toString());
			commandService.sendCommand(commandEntity);
		}
	}
	
	private Object[] createApplicationAndMachineRelation(String appPrimary, String mid){
		Application application = null;
		MachineInstance mi = null;
		List<AppInstance> appInstances = appInstanceRepository.findByApplicationIdAndMachineInstanceId(appPrimary, mid);
		if (CollectionUtils.isEmpty(appInstances)){//如果数据库中没有关联，则创建关联
			AppInstance ai = new AppInstance();
			application = findById(appPrimary);
			mi = machineInstanceRepository.findOne(mid);
			if (!SystemConsist.AGENT_STATUS_RUNNING.equals(mi.getAgentStatus())){
				throw new AgentNotRunningException();
			}
			ai.setJarName(application.getJarName());
			ai.setCreateDate(new Date());
			ai.setStatus(SystemConsist.APPINSTANCE_STATUS_DEPLOYING);
			ai.setApplication(application);
			ai.setMachineInstance(mi);
			appInstanceRepository.store(ai);
		}else{//如果数据库中有关联，直接更新关联状态为部署中
			AppInstance ai = appInstances.get(0);
			if (ai.getStatus() != SystemConsist.APPINSTANCE_STATUS_STOPED){
				throw new ApplicationStatusIllegleException("不能部署");
			}
			mi = ai.getMachineInstance();
			if (!SystemConsist.AGENT_STATUS_RUNNING.equals(mi.getAgentStatus())){
				throw new AgentNotRunningException();
			}
			ai.setJarName(ai.getApplication().getJarName());
			ai.setStatus(SystemConsist.APPINSTANCE_STATUS_DEPLOYING);
			application = ai.getApplication();
			appInstanceRepository.update(ai);
		}
		return new Object[]{application, mi};
	}

	private void createDeploySerial(Application application, MachineInstance mi) {
		//产生发布流水
		List<DeploySerial> deploySerials = 
				deploySerialRepository.findByApplicationIdAndMiIdAndJarRealName(
					application.getId(), 
					mi.getId(), 
					application.getJarRealName()
				);
		if (CollectionUtils.isEmpty(deploySerials)){//如果没有发布流水则产生发布流水
			DeploySerial deploySerial = new DeploySerial();
			deploySerial.setApplication(application);
			deploySerial.setMi(mi);
			deploySerial.setJarName(application.getJarName());
			deploySerial.setJarRealName(application.getJarRealName());
			deploySerial.setDeployDate(new Date());
			deploySerialRepository.store(deploySerial);
		}else{//如果有相同的发布流水，则更新其发布时间
			deploySerials.get(0).setDeployDate(new Date());
			deploySerialRepository.update(deploySerials.get(0));
		}
	}

	@Override
	public void deployAll(String appPrimary) {
		List<MachineInstance> mList = machineInstanceRepository.findByAgentStatus(SystemConsist.AGENT_STATUS_RUNNING);
		if (mList.size() == 0){
			throw new RuntimeException("没有可部署的机器");
		}
		deploy(mList.stream().map(m -> m.getId()).collect(Collectors.toList()).toArray(new String[]{}), appPrimary);
	}

	

}
