package org.apel.dest.dubbokeeper.ui.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apel.desp.dubbo.admin.domain.Provider;
import org.apel.desp.dubbo.admin.domain.Route;
import org.apel.desp.dubbo.admin.service.ProviderService;
import org.apel.desp.dubbo.admin.service.RouteService;
import org.apel.dest.dubbokeeper.ui.domain.BasicResponse;
import org.apel.dest.dubbokeeper.ui.domain.RouteAbstractInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.Constants;

/**
 * Created by bieber on 2015/7/25.
 */
@Controller
@RequestMapping("/route")
public class RouterController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private ProviderService providerService;

    @RequestMapping("provider/{serviceKey}/list")
    public @ResponseBody List<Route> queryRoutesByServiceKey(@PathVariable("serviceKey")String serviceKey) throws UnsupportedEncodingException {
        serviceKey = URLDecoder.decode(serviceKey, "UTF-8");
        return routeService.listByServiceKey(serviceKey);
    }

    @RequestMapping("create")
    public @ResponseBody
    BasicResponse createRoute(@RequestBody Route route){
        BasicResponse response = new BasicResponse();
        response.setResult(BasicResponse.SUCCESS);
        routeService.createRoute(route);
        return response;
    }

    @RequestMapping("batch-{type}")
    public @ResponseBody BasicResponse batchDelete(@RequestParam("ids")String ids,@PathVariable("type") String type){
        BasicResponse response = new BasicResponse();
        String[] idArray = Constants.COMMA_SPLIT_PATTERN.split(ids);
        if("delete".equals(type)){
            for(String id:idArray){
                routeService.deleteRoute(Long.parseLong(id));
            }
        }else if("enable".equals(type)){
            for(String id:idArray){
                routeService.enable(Long.parseLong(id));
            }
        }else if("disable".equals(type)){
            for(String id:idArray){
                routeService.disable(Long.parseLong(id));
            }
        }
        response.setResult(BasicResponse.SUCCESS);
        return response;
    }


    @RequestMapping("{type}_{id}")
    public @ResponseBody BasicResponse delete(@PathVariable("type")String type,@PathVariable("id")Long id){
        BasicResponse response = new BasicResponse();
        response.setResult(BasicResponse.SUCCESS);
        if("delete".equals(type)){
            routeService.deleteRoute(id);
        }else if("enable".equals(type)){
            routeService.enable(id);
        }else if("disable".equals(type)){
            routeService.disable(id);
        }else{
            response.setResult(BasicResponse.FAILED);
        }
        return response;
    }

    @RequestMapping("update")
    public @ResponseBody BasicResponse updateRoute(@RequestBody Route route){
        BasicResponse response = new BasicResponse();
        response.setResult(BasicResponse.SUCCESS);
        route.setRule(null);
        routeService.updateRoute(route);
        return response;
    }


    @ResponseBody
    @RequestMapping("list")
    public List<RouteAbstractInfo> list(){
        List<Provider> providers = providerService.listAllProvider();
        List<RouteAbstractInfo> routeAbstractInfos = new ArrayList<RouteAbstractInfo>();
        for(Provider provider :providers){
            RouteAbstractInfo routeAbstractInfo = new RouteAbstractInfo();
            routeAbstractInfo.setServiceKey(provider.getServiceKey());
            routeAbstractInfo.setApplicationName(provider.getApplication());
            routeAbstractInfo.setRouteCount(routeService.listByServiceKey(provider.getServiceKey()).size());
            if(routeAbstractInfo.getRouteCount()>0){
                routeAbstractInfos.add(routeAbstractInfo);
            }
        }
        return routeAbstractInfos;
    }

    @RequestMapping("get_{id}")
    public @ResponseBody Route getRoute(@PathVariable("id")Long id){
        return routeService.getRoute(id);
    }


}
