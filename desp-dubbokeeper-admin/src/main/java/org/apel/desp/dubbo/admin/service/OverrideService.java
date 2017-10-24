package org.apel.desp.dubbo.admin.service;

import java.util.List;

import org.apel.desp.dubbo.admin.domain.Provider;

import com.alibaba.dubbo.common.URL;

/**
 * Created by bieber on 2015/6/3.
 */
public interface OverrideService {

    public List<org.apel.desp.dubbo.admin.domain.Override> listByProvider(Provider provider);

    public List<org.apel.desp.dubbo.admin.domain.Override> listByServiceKey(String serviceKey);

    public void update(org.apel.desp.dubbo.admin.domain.Override override);

    public org.apel.desp.dubbo.admin.domain.Override getById(Long id);

    public void delete(org.apel.desp.dubbo.admin.domain.Override override);

    public void delete(Long id);

    public void add(org.apel.desp.dubbo.admin.domain.Override override);


    public Provider configProvider(Provider provider);

    public URL configProviderURL(Provider provider);
}
