package org.apel.desp.dubbo.admin.service;

import java.util.List;

import org.apel.desp.dubbo.admin.domain.Application;
import org.apel.desp.dubbo.admin.domain.Node;

/**
 * Created by bieber on 2015/6/3.
 */
public interface DubboApplicationService {

    //获取当前注册中心所有应用列表
    public List<Application> getApplications();
    //获取某个应用部署节点信息
    public List<Node> getNodesByApplicationName(String appName);
}
