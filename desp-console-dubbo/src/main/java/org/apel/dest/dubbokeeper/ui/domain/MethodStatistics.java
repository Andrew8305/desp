package org.apel.dest.dubbokeeper.ui.domain;


import java.util.Collection;

import org.apel.desp.dubbokeeper.storage.domain.Statistics;


/**
 * Created by bieber on 2015/10/25.
 */
public class MethodStatistics {

    private Collection<Statistics> statisticsCollection;


    public Collection<Statistics> getStatisticsCollection() {
        return statisticsCollection;
    }

    public void setStatisticsCollection(Collection<Statistics> statisticsCollection) {
        this.statisticsCollection = statisticsCollection;
    }


}
