package org.apel.desp.dubbokeeper.storage.domain;

/**
 * Created by bieber on 2015/11/4.
 */
public class ElapsedItem extends  BaseItem{

	private static final long serialVersionUID = 9037720367197162405L;
	private Long elapsed;


    public Long getElapsed() {
        return elapsed;
    }

    public void setElapsed(Long elapsed) {
        this.elapsed = elapsed;
    }
}
