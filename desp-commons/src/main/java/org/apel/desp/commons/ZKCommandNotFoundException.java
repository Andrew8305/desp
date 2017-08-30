package org.apel.desp.commons;

import org.apel.gaia.commons.exception.PlatformException;

public class ZKCommandNotFoundException extends PlatformException{

	private static final long serialVersionUID = 1497660481771691497L;

	public ZKCommandNotFoundException() {}
	
	public ZKCommandNotFoundException(Exception e) {
		 super(e);
    }

	public ZKCommandNotFoundException(String msg) {
		super(msg);
	}

	public ZKCommandNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
