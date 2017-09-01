package org.apel.desp.commons.exception;

import org.apel.gaia.commons.exception.PlatformException;

public class ApplicationStatusIllegleException extends PlatformException{

	private static final String DESC = "应用有部分状态不合法";
	
	private static final long serialVersionUID = 8503478597440033432L;

	public ApplicationStatusIllegleException() {
		super(DESC);
	}
	
	public ApplicationStatusIllegleException(Exception e) {
		 super(e);
    }

	public ApplicationStatusIllegleException(String msg) {
		super(DESC + ":" + msg);
	}

	public ApplicationStatusIllegleException(String msg, Throwable cause) {
		super(DESC + ":" + msg, cause);
	}
	
}
