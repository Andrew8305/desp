package org.apel.desp.commons.exception;

import org.apel.gaia.commons.exception.PlatformException;

public class ZKCommanderNotFoundException extends PlatformException{

	private static final long serialVersionUID = 1497660481771691497L;

	public ZKCommanderNotFoundException() {}
	
	public ZKCommanderNotFoundException(Exception e) {
		 super(e);
    }

	public ZKCommanderNotFoundException(String msg) {
		super(msg);
	}

	public ZKCommanderNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
