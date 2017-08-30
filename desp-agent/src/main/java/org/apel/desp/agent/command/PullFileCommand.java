package org.apel.desp.agent.command;

import java.util.concurrent.ExecutorService;

import org.apel.desp.commons.consist.ZKCommandCode;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.util.FTPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 拉取文件命令
 * @author lijian
 *
 */
@Component
public class PullFileCommand implements ZKCommander{

	@Autowired
	private ZKCommandPool poolMap;
	@Autowired
	private FTPUtil ftpUtil;
	
	@Override
	public ZKCommandCode commandCode() {
		return ZKCommandCode.PULL_FILE;
	}

	@Override
	public void execute(ZKCommand zkCommand) {
		ExecutorService pool = poolMap.getPoolOrSet(zkCommand.getAppId());
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("123");
			}
		});
	}
	

}
