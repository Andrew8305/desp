package org.apel.desp.commons.os;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 
 * 操作系统代理类，动态判断操作系统
 * @author lijian
 *
 */
@Component
public class OperationSystemMananger implements OSOperator, ApplicationListener<ContextRefreshedEvent>{

	@Autowired
	private ApplicationContext applicationContext;
	
	private OSOperator osOperator;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Map<String, OSOperator> operators = applicationContext.getBeansOfType(OSOperator.class);
		String osName = System.getProperty("os.name");
		boolean flag = false;
		for (OSOperator operator : operators.values()) {
			if (null == operator.identifier())
				continue;
			if (osName.toLowerCase().contains(operator.identifier())){
				osOperator = operator;
				flag = true;
				break;
			}
		}
		if (!flag){
			throw new RuntimeException("没有找到操作系统处理类");
		}
	}

	@Override
	public boolean checkAppRuning(String appId) {
		return osOperator.checkAppRuning(appId);
	}
	

	public static void main(String[] args) {
		System.out.println("linux".contains(""));
	}

	@Override
	public String identifier() {
		return null;
	}

	@Override
	public int getPID(String appId) {
		return osOperator.getPID(appId);
	}

	@Override
	public void killProcess(int pid) {
		osOperator.killProcess(pid);
	}

	@Override
	public void startApp(String appId) {
		osOperator.startApp(appId);
	}
	
}
