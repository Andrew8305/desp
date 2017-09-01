package org.apel.desp.commons.os;

import org.springframework.stereotype.Component;

/**
 * linux系统操作类
 * @author lijian
 *
 */
@Component
public class LinuxOperator implements OSOperator{

	@Override
	public boolean checkAppRuning(String appId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String identifier() {
		return "linux";
	}

	@Override
	public int getPID(String appId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void killProcess(int pid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startApp(String appId) {
		// TODO Auto-generated method stub
		
	}

}
