package org.apel.dest.dubbokeeper.ui.domain;

import java.util.Map;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.StringUtils;

/**
 * Created by bieber on 2015/6/21.
 */
public class WeightOverrideInfo extends  OverrideInfo{

    private int weight;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    public static WeightOverrideInfo valueOf(org.apel.desp.dubbo.admin.domain.Override override){
        String weight = null;
        if(!StringUtils.isEmpty(override.getParams())){
            Map<String,String> parameters = StringUtils.parseQueryString(override.getParams());
            weight=parameters.get(Constants.WEIGHT_KEY);
            if(StringUtils.isEmpty(weight)||"null".equals(weight)){
                return null;
            }
        }
        WeightOverrideInfo overrideInfo = new WeightOverrideInfo();
        overrideInfo.setAddress(override.getAddress());
        overrideInfo.setApplication(override.getApplication()==null?Constants.ANY_VALUE:override.getApplication());
        overrideInfo.setEnable(override.isEnabled());
        overrideInfo.setId(override.getId());
        overrideInfo.setParameters(override.getParams());
        if(!StringUtils.isEmpty(weight)){
            overrideInfo.setWeight(Integer.parseInt(weight));
        }
        return overrideInfo;
    }
}
