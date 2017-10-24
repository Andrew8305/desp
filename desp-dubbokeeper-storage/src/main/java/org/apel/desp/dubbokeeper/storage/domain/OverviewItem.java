package org.apel.desp.dubbokeeper.storage.domain;

import java.io.Serializable;

/**
 * Created by bieber on 2015/11/4.
 */
public class OverviewItem implements Serializable {

	private static final long serialVersionUID = -1439037416801926555L;

	private Long concurrent;

    private Long elapsed;

    private Long fault;

    private Long success;

    public Long getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(Long concurrent) {
        this.concurrent = concurrent;
    }

    public Long getElapsed() {
        return elapsed;
    }

    public void setElapsed(Long elapsed) {
        this.elapsed = elapsed;
    }

    public Long getFault() {
        return fault;
    }

    public void setFault(Long fault) {
        this.fault = fault;
    }

    public Long getSuccess() {
        return success;
    }

    public void setSuccess(Long success) {
        this.success = success;
    }
}
