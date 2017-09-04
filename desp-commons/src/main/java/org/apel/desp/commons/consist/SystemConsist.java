package org.apel.desp.commons.consist;

public interface SystemConsist {

	final static String AGENT_STATUS_STOPED = "已停止";
	final static String AGENT_STATUS_RUNNING = "运行中";
	
	final static int APPINSTANCE_STATUS_UNKOWN = -1;
	final static int APPINSTANCE_STATUS_STOPED = 0;
	final static int APPINSTANCE_STATUS_RUNNING = 1;
	final static int APPINSTANCE_STATUS_STOPPING = 2;
	final static int APPINSTANCE_STATUS_DEPLOYING = 3;
	final static int APPINSTANCE_STATUS_DELETING = 4;
	final static int APPINSTANCE_STATUS_STARTING = 5;
	
	final static int COMMAND_EXE_STATUS_DONE = 1;
	final static int COMMAND_EXE_STATUS_NONE = 0;
	
}
