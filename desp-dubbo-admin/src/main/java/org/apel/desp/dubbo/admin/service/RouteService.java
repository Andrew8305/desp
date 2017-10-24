package org.apel.desp.dubbo.admin.service;

import java.util.List;

import org.apel.desp.dubbo.admin.domain.Route;

/**
 * Created by bieber on 2015/6/3.
 */
public interface RouteService {

    public void createRoute(Route route);

    public void deleteRoute(Long id);

    public void updateRoute(Route route);

    public List<Route> listByServiceKey(String serviceKey);

    public Route getRoute(Long id);

    public void enable(Long id);

    public void disable(Long id);


}
