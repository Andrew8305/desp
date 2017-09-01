package org.apel.desp.commons.os;

/**
 * 
 * 操作系统命令接口
 * @author lijian
 *
 */
public interface OSOperator {

	void startApp(String appId);
	
	boolean checkAppRuning(String appId);
	
	int getPID(String appId);
	
	void killProcess(int pid);
	
	String identifier();
	
}
