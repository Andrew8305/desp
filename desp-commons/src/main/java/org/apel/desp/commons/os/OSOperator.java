package org.apel.desp.commons.os;

/**
 * 
 * 操作系统命令接口
 * @author lijian
 *
 */
public interface OSOperator {

	boolean checkAppRuning(String appId);
	
	int getPID(String appId);
	
	String identifier();
	
}
