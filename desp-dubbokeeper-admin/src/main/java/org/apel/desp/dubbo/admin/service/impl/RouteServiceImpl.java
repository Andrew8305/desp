package org.apel.desp.dubbo.admin.service.impl;

import java.util.List;

import org.apel.desp.dubbo.admin.domain.Route;
import org.apel.desp.dubbo.admin.service.AbstractService;
import org.apel.desp.dubbo.admin.service.RouteService;
import org.apel.desp.dubbo.admin.sync.util.Pair;
import org.apel.desp.dubbo.admin.sync.util.SyncUtils;
import org.apel.desp.dubbo.admin.sync.util.Tool;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;

/**
 * Created by bieber on 2015/7/25.
 */
@Service
public class RouteServiceImpl extends AbstractService implements RouteService {

    @Override
    public void createRoute(Route route) {
        add(route.toUrl());
    }

    @Override
    public void deleteRoute(Long id) {
        delete(id, Constants.ROUTERS_CATEGORY);
    }

    @Override
    public void updateRoute(Route route) {
        URL oldUrl = getOneById(Constants.ROUTERS_CATEGORY,route.getId());
        update(oldUrl,route.toUrl());
    }

    @Override
    public List<Route> listByServiceKey(final String serviceKey) {
        return filterCategoryData(new ConvertURL2Entity<Route>() {

            @Override
            public Route convert(Pair<Long, URL> pair) {
                if(pair.getValue().getPath().equals(Tool.getInterface(serviceKey))){
                    return SyncUtils.url2Route(pair);
                }else{
                    return null;
                }
            }
        }, Constants.ROUTERS_CATEGORY, Constants.VERSION_KEY, Tool.getVersion(serviceKey),Constants.GROUP_KEY,Tool.getGroup(serviceKey));
    }

    @Override
    public Route getRoute(Long id) {
        return SyncUtils.url2Route(new Pair<Long, URL>(id,getOneById(Constants.ROUTERS_CATEGORY,id)));
    }

    @Override
    public void enable(Long id) {
        Route route = getRoute(id);
        if(route.isEnabled()){
            return ;
        }
        route.setEnabled(true);
        updateRoute(route);
    }

    @Override
    public void disable(Long id) {
        Route route = getRoute(id);
        if(!route.isEnabled()){
            return ;
        }
        route.setEnabled(false);
        updateRoute(route);
    }
}
