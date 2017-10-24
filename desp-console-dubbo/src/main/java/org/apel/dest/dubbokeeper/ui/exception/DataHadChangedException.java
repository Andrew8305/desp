package org.apel.dest.dubbokeeper.ui.exception;

/**
 * Created by bieber on 2015/6/15.
 */
public class DataHadChangedException extends IllegalStateException {

	private static final long serialVersionUID = -900893490609843174L;

	public DataHadChangedException() {
    }

    public DataHadChangedException(String s) {
        super(s);
    }

    public DataHadChangedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataHadChangedException(Throwable cause) {
        super(cause);
    }
}
