package org.apel.desp.dubbo.admin.service.impl;

import java.util.List;

import org.apel.desp.dubbo.admin.domain.Consumer;
import org.apel.desp.dubbo.admin.service.AbstractService;
import org.apel.desp.dubbo.admin.service.ConsumerService;
import org.apel.desp.dubbo.admin.sync.util.Pair;
import org.apel.desp.dubbo.admin.sync.util.SyncUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;

/**
 * Created by bieber on 2015/6/6.
 */
@Service
public class ConsumerServiceImpl extends AbstractService implements ConsumerService {

    @Override
    public List<Consumer> listConsumerByApplication(String appName) {
        return filterCategoryData(new ConvertURL2Entity<Consumer>() {
            @Override
            public Consumer convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Consumer(pair);
            }
        },Constants.CONSUMERS_CATEGORY,Constants.APPLICATION_KEY,appName);
    }

    @Override
    public List<Consumer> listConsumerByService(String service) {
        return filterCategoryData(new ConvertURL2Entity<Consumer>() {
            @Override
            public Consumer convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Consumer(pair);
            }
        },Constants.CONSUMERS_CATEGORY,Constants.INTERFACE_KEY,service);
    }

    @Override
    public List<Consumer> listConsumerByConditions(String... conditions) {
        return filterCategoryData(new ConvertURL2Entity<Consumer>() {
            @Override
            public Consumer convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Consumer(pair);
            }
        },Constants.CONSUMERS_CATEGORY,conditions);
    }
}
