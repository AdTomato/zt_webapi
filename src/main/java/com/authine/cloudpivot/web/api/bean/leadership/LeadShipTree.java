package com.authine.cloudpivot.web.api.bean.leadership;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @author wangyong
 */
@Data
public class LeadShipTree {

    private String id;
    private String name;
    private List<Map<String, Object>> leadShipData;
    private List<LeadShipTree> child;

}


