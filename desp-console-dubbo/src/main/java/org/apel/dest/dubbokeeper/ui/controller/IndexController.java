package org.apel.dest.dubbokeeper.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apel.desp.dubbo.admin.domain.Application;
import org.apel.desp.dubbo.admin.domain.Consumer;
import org.apel.desp.dubbo.admin.domain.Provider;
import org.apel.desp.dubbo.admin.service.ConsumerService;
import org.apel.desp.dubbo.admin.service.DubboApplicationService;
import org.apel.desp.dubbo.admin.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.URL;

/**
 * Created by bieber on 2015/6/4.
 */
@Controller
public class IndexController {

    @Autowired
    private DubboApplicationService applicationService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConsumerService consumerService;
    
    @RequestMapping("/admin_index")
    public String index(){
        return "admin_index";
    }

    @RequestMapping(value = "/loadAppsType", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody List<Integer> loadAppsTypeDoughnutChat(){
        List<Application> applications = applicationService.getApplications();
        int providerCount=0;
        int consumerCount=0;
        int providerAndConsumerCount=-0;
        for(Application application:applications){
            if(application.getType()==Application.PROVIDER){
                providerCount++;
            }else if(application.getType()==Application.CONSUMER){
                consumerCount++;
            }else{
                providerAndConsumerCount++;
            }
        }
        List<Integer> statistics = new ArrayList<Integer>();
        statistics.add(providerCount);
        statistics.add(consumerCount);
        statistics.add(providerAndConsumerCount);
        return statistics;
    }


    @RequestMapping(value = "/loadServiceProtocols", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody Map<String,Integer> loadServiceProtocolDoughnutChat(){
        List<Provider> providers = providerService.listAllProvider();
        Map<String,Integer> statistics = new HashMap<String, Integer>();
        for(Provider provider:providers){
            URL providerUrl = URL.valueOf(provider.getUrl());
            if(statistics.containsKey(providerUrl.getProtocol())){
                statistics.put(providerUrl.getProtocol(),statistics.get(providerUrl.getProtocol())+1);
            }else{
                statistics.put(providerUrl.getProtocol(),1);
            }
        }
        return statistics;
    }

    @RequestMapping(value = "/loadAppServices", method = {RequestMethod.POST, RequestMethod.GET})
    public  @ResponseBody Map<String,List<Integer>> loadApplicationServiceStatus(){
        List<Application> applications =  applicationService.getApplications();
        Map< String,List<Integer>> statistics =  new HashMap<String, List<Integer>>();
        for(Application application:applications){
            List<Integer> data = new ArrayList<Integer>();
            List<String> containsConsumes = new ArrayList<String>();
            List<String> containsProvides = new ArrayList<String>();
            List<Consumer> consumers =  consumerService.listConsumerByApplication(application.getApplication());
            for(Consumer consumer:consumers){
                if(containsConsumes.contains(consumer.getServiceKey())){
                    continue;
                }
                containsConsumes.add(consumer.getServiceKey());
            }
            List<Provider> providers = providerService.listProviderByApplication(application.getApplication());
            for(Provider provider :providers){
                if(containsConsumes.contains(provider.getServiceKey())){
                    continue;
                }
                containsProvides.add(provider.getServiceKey());
            }
            data.add(containsProvides.size());
            data.add(containsConsumes.size());
            statistics.put(application.getApplication(),data);
        }
        return statistics;
    }
    @RequestMapping(value = "/loadAppNodes", method = {RequestMethod.POST, RequestMethod.GET})
    public  @ResponseBody Map<String,Integer> loadApplicationNodes(){
        List<Application> applications =  applicationService.getApplications();
        Map< String,Integer> statistics =  new HashMap<String, Integer>();
        for(Application application:applications){
            statistics.put(application.getApplication(),applicationService.getNodesByApplicationName(application.getApplication()).size());
        }
        return statistics;
    }
    @RequestMapping(value = "/loadAppsDependencies", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody Map<String,List<Map<String,Object>>> loadAppsDependencies(){
        Map<String,List<Map<String,Object>>> statistics=new HashMap<String, List<Map<String, Object>>>();
        List<Application> applications =  applicationService.getApplications();
        List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> links = new ArrayList<Map<String, Object>>();
        List<String> containedNodes = new ArrayList<String>();
        for(Application application:applications){
            if(containedNodes.contains(application.getApplication())){
                continue;
            }
            containedNodes.add(application.getApplication());
            Map<String,Object> node = new HashMap<String, Object>();
            node.put("category",application.getType()-1);
            node.put("name",application.getApplication());
            node.put("value",application.getApplication());
            node.put("symbolSize",20);
            node.put("draggable",true);
            nodes.add(node);
            List<Consumer> consumers = consumerService.listConsumerByApplication(application.getApplication());
            List<String> containedLinks = new ArrayList<String>();
            for(Consumer consumer:consumers){
                Map<String,Object> link = new HashMap<String, Object>();
                link.put("source",application.getApplication());
                List<Provider> providers = providerService.listProviderByServiceKey(consumer.getServiceKey());
                if(providers.size()>0){
                    if(containedLinks.contains(providers.get(0).getApplication())){
                        continue;
                    }
                    containedLinks.add(providers.get(0).getApplication());
                    link.put("target",providers.get(0).getApplication());
                    link.put("weight",1);
                    link.put("name",application.getApplication()+"依赖"+providers.get(0).getApplication());
                    links.add(link);
                }
            }
            containedLinks.clear();
        }
        statistics.put("nodes",nodes);
        statistics.put("links",links);
        containedNodes.clear();
        return statistics;
    }
    
}
