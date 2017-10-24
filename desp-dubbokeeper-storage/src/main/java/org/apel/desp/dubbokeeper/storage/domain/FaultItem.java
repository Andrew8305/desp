package org.apel.desp.dubbokeeper.storage.domain;

/**
 * Created by bieber on 2015/11/4.
 */
public class FaultItem extends  BaseItem{

	private static final long serialVersionUID = 435577563875911158L;
	private Integer fault;

    public Integer getFault() {
        return fault;
    }

    public void setFault(Integer fault) {
        this.fault = fault;
    }
}
