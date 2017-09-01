package org.apel.desp.commons.exception;

import org.apel.gaia.commons.exception.PlatformException;

public class AgentNotRunningException extends PlatformException{
	
	private final static String DESC = "在物理机上有agent没有启动";
	
	private static final long serialVersionUID = -3962169683091938139L;

	public AgentNotRunningException() {
		super(DESC);
	}
	
	public AgentNotRunningException(Exception e) {
		 super(e);
    }

	public AgentNotRunningException(String msg) {
		super(DESC + ":" + msg);
	}

	public AgentNotRunningException(String msg, Throwable cause) {
		super(DESC + ":" + msg, cause);
	}
	
}
