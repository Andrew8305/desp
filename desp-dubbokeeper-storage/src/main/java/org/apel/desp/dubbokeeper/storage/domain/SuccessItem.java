package org.apel.desp.dubbokeeper.storage.domain;

/**
 * Created by bieber on 2015/11/4.
 */
public class SuccessItem extends BaseItem {

	private static final long serialVersionUID = -6588051598608443440L;
	private Integer success;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }
}
