package org.apel.desp.dubbokeeper.storage.domain;

/**
 * 应用信息
 * Created by bieber on 2015/11/16.
 */
public class ApplicationInfo extends  BaseInfo{

	private static final long serialVersionUID = 3018838649197212303L;

	private String applicationName;

    //0 消费者，1 提供者 2 即是消费者也是提供者
    private Integer applicationType;


    public Integer getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(int applicationType) {
        this.applicationType = applicationType;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

}
