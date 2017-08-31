package org.apel.desp.agent.command;

import java.util.HashMap;
import java.util.Map;

import org.apel.desp.commons.ZKCommanderNotFoundException;
import org.apel.desp.commons.consist.ZKCommandCode;
import org.apel.desp.commons.domain.ZKCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ZKCommandManager implements ApplicationListener<ContextRefreshedEvent>{
	
	@Autowired
	private ApplicationContext applicationContext;
	private Map<ZKCommandCode, ZKCommander> zkCommandMap = new HashMap<>();
	
	public void execute(ZKCommand zkCommand, ZKCommandCallback zkCommandCallback){
		ZKCommander zkCommander = zkCommandMap.get(ZKCommandCode.getZKCommand(zkCommand.getZkCommandCode()));
		if (zkCommander == null){
			throw new ZKCommanderNotFoundException();
		}
		zkCommander.execute(zkCommand, zkCommandCallback);
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Map<String, ZKCommander> zkCommandBeans = applicationContext.getBeansOfType(ZKCommander.class);
		zkCommandBeans.values().stream().forEach(command -> {
			zkCommandMap.put(command.commandCode(), command);
		});
	}
	
	
	
}
