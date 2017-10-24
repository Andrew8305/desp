package org.apel.desp.dubbokeeper.storage.domain;

/**
 * Created by bieber on 2015/11/4.
 */
public class ConcurrentItem extends  BaseItem{

	private static final long serialVersionUID = -2962573743188535260L;
	private Long concurrent;

    public Long getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(Long concurrent) {
        this.concurrent = concurrent;
    }
}
