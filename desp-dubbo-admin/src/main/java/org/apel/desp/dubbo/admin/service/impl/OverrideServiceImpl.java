package org.apel.desp.dubbo.admin.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.apel.desp.dubbo.admin.domain.Provider;
import org.apel.desp.dubbo.admin.service.AbstractService;
import org.apel.desp.dubbo.admin.service.OverrideService;
import org.apel.desp.dubbo.admin.sync.util.Pair;
import org.apel.desp.dubbo.admin.sync.util.SyncUtils;
import org.apel.desp.dubbo.admin.sync.util.Tool;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.cluster.Configurator;

/**
 * Created by bieber on 2015/6/13.
 */
@Service
public class OverrideServiceImpl extends AbstractService implements OverrideService {


    public List<org.apel.desp.dubbo.admin.domain.Override> listByProvider(Provider provider) {
        List<org.apel.desp.dubbo.admin.domain.Override> overrides = new ArrayList<>();
        ConcurrentMap<String, Map<Long, URL>> serviceUrls = getServiceByCategory(Constants.CONFIGURATORS_CATEGORY);
        if (serviceUrls == null || serviceUrls.size() <= 0) {
            return overrides;
        }
        Collection<Map<Long, URL>> urlMaps = serviceUrls.values();
        URL providerUrl = SyncUtils.provider2URL(provider);
        for (Map<Long, URL> urlMap : urlMaps) {
            for (Map.Entry<Long, URL> urlEntry : urlMap.entrySet()) {
                URL url = urlEntry.getValue();
                if (Constants.ANYHOST_VALUE.equals(url.getHost())
                        || providerUrl.getHost().equals(url.getHost())) {
                    String configApplication = url.getParameter(Constants.APPLICATION_KEY, url.getUsername());
                    String currentApplication = StringUtils.isEmpty(provider.getApplication()) ? provider.getUsername() : provider.getApplication();
                    if (configApplication == null || Constants.ANY_VALUE.equals(configApplication)
                            || configApplication.equals(currentApplication)) {
                        if ((url.getPort() == 0 || URL.valueOf(provider.getUrl()).getPort() == url.getPort())) {
                            if (url.getPath().equals(Tool.getInterface(provider.getServiceKey())) && StringUtils.isEquals(url.getParameter(Constants.GROUP_KEY), providerUrl.getParameter(Constants.GROUP_KEY))&& StringUtils.isEquals(url.getParameter(Constants.VERSION_KEY), providerUrl.getParameter(Constants.VERSION_KEY))) {
                                overrides.add(SyncUtils.url2Override(new Pair<Long, URL>(urlEntry.getKey(), url)));
                            }
                        }
                    }
                }
            }
        }
        return overrides;
    }

    public List<org.apel.desp.dubbo.admin.domain.Override> listByServiceKey(String serviceKey) {
        List<org.apel.desp.dubbo.admin.domain.Override> overrides = new ArrayList<>();
        ConcurrentMap<String, Map<Long, URL>> serviceUrls = getServiceByCategory(Constants.CONFIGURATORS_CATEGORY);
        if (serviceUrls == null || serviceUrls.size() <= 0) {
            return overrides;
        }
        Collection<Map<Long, URL>> urlMaps = serviceUrls.values();
        String group = Tool.getGroup(serviceKey);
        String interfaceName = Tool.getInterface(serviceKey);
        String version = Tool.getVersion(serviceKey);
        for (Map<Long, URL> urlMap : urlMaps) {
            for (Map.Entry<Long, URL> urlEntry : urlMap.entrySet()) {
                URL url = urlEntry.getValue();
                if(url.getPath().equals(interfaceName)&&StringUtils.isEquals(group,url.getParameter(Constants.GROUP_KEY))&&StringUtils.isEquals(version,url.getParameter(Constants.VERSION_KEY))){
                    overrides.add(SyncUtils.url2Override(new Pair<Long, URL>(urlEntry.getKey(), url)));
                }
            }
        }

        return overrides;
    }


    public void update(org.apel.desp.dubbo.admin.domain.Override override) {
        delete(override.getId());
        add(override);
    }

    @java.lang.Override
    public org.apel.desp.dubbo.admin.domain.Override getById(Long id) {
        URL url = getOneById(Constants.CONFIGURATORS_CATEGORY, id);
        if (url == null) {
            throw new IllegalStateException("data had changed!");
        }
        return SyncUtils.url2Override(new Pair<Long, URL>(id, url));
    }

    @java.lang.Override
    public void delete(org.apel.desp.dubbo.admin.domain.Override override) {
        delete(override.getId());
    }

    @java.lang.Override
    public void delete(Long id) {
        URL url = getOneById(Constants.CONFIGURATORS_CATEGORY, id);
        if (url == null) {
            throw new IllegalStateException("data had changed!");
        }
        delete(url);
    }

    @java.lang.Override
    public void add(org.apel.desp.dubbo.admin.domain.Override override) {
        add(override.toUrl());
    }

    @java.lang.Override
    public Provider configProvider(Provider provider) {
        List<org.apel.desp.dubbo.admin.domain.Override> overrides = listByProvider(provider);
        URL url = URL.valueOf(provider.getUrl()).addParameterString(provider.getParameters());
        for (org.apel.desp.dubbo.admin.domain.Override override : overrides) {
            URL overrideUrl = override.toUrl();
            Configurator configurator = SyncUtils.toConfigurators(overrideUrl);
            if (configurator != null) {
                url = configurator.configure(url);
            }
            if(overrideUrl.getParameters().containsKey(Constants.DISABLED_KEY)){
                url = url.addParameter(Constants.ENABLED_KEY,!Boolean.parseBoolean(overrideUrl.getParameter(Constants.DISABLED_KEY)));
            }
        }
        return SyncUtils.url2Provider(new Pair<Long, URL>(provider.getId(), url));
    }

    @java.lang.Override
    public URL configProviderURL(Provider provider) {
        List<org.apel.desp.dubbo.admin.domain.Override> overrides = listByProvider(provider);
        URL url = URL.valueOf(provider.getUrl()).addParameterString(provider.getParameters());
        for (org.apel.desp.dubbo.admin.domain.Override override : overrides) {
            URL overrideUrl = override.toUrl();
            Configurator configurator = SyncUtils.toConfigurators(overrideUrl);
            if (configurator != null) {
                url = configurator.configure(url);
            }
            if(overrideUrl.getParameters().containsKey(Constants.DISABLED_KEY)){
                url = url.addParameter(Constants.ENABLED_KEY,!Boolean.parseBoolean(overrideUrl.getParameter(Constants.DISABLED_KEY)));
            }
        }
        return url;
    }


}
