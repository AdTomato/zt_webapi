package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

/**
 * @ClassName JudgesWeight
 * @author: lfh
 * @Date:2020/7/20 13:47
 * @Description: 发起总部部门业绩评价表 -字表 评委权重表
 **/
@Data
public class JudgesWeight {
    /**
     * id
     */
    //private String hid;
    /**
     * 评委
     */
    private String judges;
    /**
     * 权重
     */
    private Double weight;
}
