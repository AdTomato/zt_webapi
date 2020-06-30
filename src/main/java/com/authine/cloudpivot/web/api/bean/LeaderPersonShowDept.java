package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

/**
 * 领导人员部门显示
 *
 * @author wangyong
 * @time 2020-06-30-10-43
 */
@Data
public class LeaderPersonShowDept extends BaseBean {

    private String showDept;
    private Double sortKey;

}
