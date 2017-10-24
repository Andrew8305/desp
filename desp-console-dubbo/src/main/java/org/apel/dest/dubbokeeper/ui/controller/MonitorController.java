package org.apel.dest.dubbokeeper.ui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apel.desp.dubbo.admin.domain.Provider;
import org.apel.desp.dubbo.admin.service.ProviderService;
import org.apel.desp.dubbokeeper.storage.StatisticsStorage;
import org.apel.desp.dubbokeeper.storage.domain.ApplicationInfo;
import org.apel.desp.dubbokeeper.storage.domain.MethodMonitorOverview;
import org.apel.desp.dubbokeeper.storage.domain.ServiceInfo;
import org.apel.desp.dubbokeeper.storage.domain.StatisticsOverview;
import org.apel.dest.dubbokeeper.ui.domain.MethodStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;

@Controller
@RequestMapping("/monitor")
public class MonitorController {

    @Reference
    private StatisticsStorage statisticsStorage ;

    @Autowired
    private ProviderService providerService;
    @Value("${monitor.collect.interval:6000}")
    private Integer collectInterval;

    private static final long ONE_DAY=24*60*60*1000;

    private static final long ONE_HOUR=60*60*1000;

    @RequestMapping("/load-interval")
    public @ResponseBody Integer queryInterval(){
        return collectInterval;
    }

    @RequestMapping("/{application}/{service}/{method}/now")
    public @ResponseBody
    MethodStatistics queryCurrentMethodStatistics(@PathVariable("application")String application,@PathVariable("service")String service,@PathVariable("method")String method){
        MethodStatistics methodStatistics = new MethodStatistics();
        long currentTime = System.currentTimeMillis();
        methodStatistics.setStatisticsCollection(statisticsStorage.queryStatisticsForMethod(application,service,method,currentTime-ONE_HOUR,currentTime));
        return methodStatistics;
    }

    @RequestMapping("/{application}/{service}/{method}/{startTime}-{endTime}/monitors")
    public @ResponseBody
    MethodStatistics queryMethodStatistics(@PathVariable("application")String application,@PathVariable("service")String service,@PathVariable("method")String method,@PathVariable("startTime")long startTime,@PathVariable("endTime") long endTime){
        MethodStatistics methodStatistics = new MethodStatistics();
        methodStatistics.setStatisticsCollection(statisticsStorage.queryStatisticsForMethod(application,service,method,startTime,endTime));
        return methodStatistics;
    }
    @RequestMapping("/{application}/{service}/now")
    public @ResponseBody
    Collection<MethodMonitorOverview> overviewServiceRealTime(@PathVariable("application")String application,@PathVariable("service")String service){
        List<Provider> providers = providerService.listProviderByServiceKey(service);
        List<String> methods = new ArrayList<String>();
        if(providers.size()>0){
            Provider provider = providers.get(0);
            Map<String,String> params = StringUtils.parseQueryString(provider.getParameters());
            String methodStr = params.get(Constants.METHODS_KEY);
            if(!StringUtils.isEmpty(methodStr)){
                String[] methodArray = Constants.COMMA_SPLIT_PATTERN.split(methodStr);
                for(String method:methodArray){
                    methods.add(method);
                }
            }
        }
        long currentTime = System.currentTimeMillis();
        return statisticsStorage.queryMethodMonitorOverview(application,service,methods.size(),currentTime-ONE_HOUR,currentTime);
    }
    @RequestMapping("/{application}/{service}/{startTime}-{endTime}/monitors")
    public @ResponseBody
    Collection<MethodMonitorOverview> overviewService(@PathVariable("application")String application,@PathVariable("service")String service,@PathVariable("startTime")long startTime,@PathVariable("endTime") long endTime){
        List<Provider> providers = providerService.listProviderByServiceKey(service);
        List<String> methods = new ArrayList<String>();
        if(providers.size()>0){
            Provider provider = providers.get(0);
            Map<String,String> params = StringUtils.parseQueryString(provider.getParameters());
            String methodStr = params.get(Constants.METHODS_KEY);
            if(!StringUtils.isEmpty(methodStr)){
                String[] methodArray = Constants.COMMA_SPLIT_PATTERN.split(methodStr);
                for(String method:methodArray){
                    methods.add(method);
                }
            }
        }
        return statisticsStorage.queryMethodMonitorOverview(application,service,methods.size(),startTime,endTime);
    }

    @RequestMapping("/index")
    public @ResponseBody Collection<ApplicationInfo> monitorIndex(){
        return statisticsStorage.queryApplications();
    }

    @RequestMapping("/{application}/{dayRange}/info")
    public @ResponseBody
    ApplicationInfo queryApplicationInfo(@PathVariable("application")String application,@PathVariable("dayRange")int dayRange){
        long currentTime = System.currentTimeMillis();
        return statisticsStorage.queryApplicationInfo(application,currentTime-ONE_DAY*dayRange,currentTime);
    }

    @RequestMapping("/{application}/{dayRange}/overview")
    public @ResponseBody
    StatisticsOverview queryApplicationOverview(@PathVariable("application")String application,@PathVariable("dayRange")int dayRange){
        long currentTime = System.currentTimeMillis();
        return statisticsStorage.queryApplicationOverview(application,currentTime-(dayRange*ONE_DAY),currentTime);
    }
    @RequestMapping("/{application}/{service}/{dayRange}/overview")
    public @ResponseBody
    StatisticsOverview queryServiceOverview(@PathVariable("application")String application,@PathVariable("service")String service,@PathVariable("dayRange")int dayRange){
        long currentTime = System.currentTimeMillis();
        return statisticsStorage.queryServiceOverview(application, service, currentTime - (dayRange * ONE_DAY), currentTime);
    }



    @RequestMapping("/{application}/{dayRange}/services")
    public @ResponseBody
    Collection<ServiceInfo> queryServiceByApp(@PathVariable("application")String application,@PathVariable("dayRange")int dayRange){
        long end = System.currentTimeMillis();
        long start = end-ONE_DAY*dayRange;
        return statisticsStorage.queryServiceByApp(application,start,end);
    }

}
