package org.apel.desp.console.service.impl;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKNodePath;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.util.ZKConnector;
import org.apel.desp.console.dao.CommandRepository;
import org.apel.desp.console.domain.CommandEntity;
import org.apel.desp.console.service.CommandService;
import org.apel.gaia.infrastructure.impl.AbstractBizCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;

@Service
@Transactional
@Order(value = 2)
public class CommandServiceImpl extends AbstractBizCommonService<CommandEntity, String> implements CommandService, ApplicationListener<ContextRefreshedEvent>{

	@Autowired
	private ZKConnector zkConnector;
	
	@Override
	public void sendCommand(CommandEntity commandEntity) {
		//先保存命令到数据库
		getRepository().store(commandEntity);
		//再发送命令到zk
		sendZKCommand(commandEntity);
	}

	private void sendZKCommand(CommandEntity commandEntity) {
		try {
			ZKCommand zkCommand = new ZKCommand();
			zkCommand.setId(commandEntity.getId());
			zkCommand.setParam(commandEntity.getParam());
			zkCommand.setAppId(commandEntity.getAppId());
			zkCommand.setStatus(SystemConsist.COMMAND_EXE_STATUS_NONE);
			zkCommand.setCreateDate(commandEntity.getCreateDate());
			zkCommand.setZkCommandCode(commandEntity.getZkCommandCode());
			String[] agents = commandEntity.getAgents().split(",");
			for (String agent : agents) {
				String commandPath = ZKNodePath.ZK_COMMONDS_PATH + "/" 
						+ agent + "/" + zkCommand.getId();
				String data = JSON.toJSONString(zkCommand);
				zkConnector.getClient().create()
					.creatingParentsIfNeeded()
					.withMode(CreateMode.EPHEMERAL)
					.forPath(commandPath, data.getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
	}

	@Override
	public void retryUndoneCommand() {
		List<CommandEntity> commands = ((CommandRepository)getRepository())
				.findByStatusOrderByCreateDateAsc(SystemConsist.COMMAND_EXE_STATUS_NONE);
		for (CommandEntity commandEntity : commands) {
			sendZKCommand(commandEntity);
		}
	}

	@Override
	public void updateStatus(String id) {
		CommandEntity commandEntity = getRepository().findOne(id);
		commandEntity.setStatus(SystemConsist.COMMAND_EXE_STATUS_DONE);
		getRepository().update(commandEntity);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//系统启动的时候重试数据库中未发送的命令
		retryUndoneCommand();
	}
	
	
}
