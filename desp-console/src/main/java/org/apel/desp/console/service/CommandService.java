package org.apel.desp.console.service;

import org.apel.desp.console.domain.CommandEntity;
import org.apel.gaia.infrastructure.BizCommonService;

public interface CommandService extends BizCommonService<CommandEntity,String>{

	/**
	 * 把命令保存到数据库然后发送zk命令
	 * @param commandEntity
	 */
	void sendCommand(CommandEntity commandEntity);
	
	/**
	 * 重试数据库中未发送的zk命令
	 */
	void retryUndoneCommand();

	/**
	 * 修改状态
	 * @param id
	 */
	void updateStatus(String id);

}
