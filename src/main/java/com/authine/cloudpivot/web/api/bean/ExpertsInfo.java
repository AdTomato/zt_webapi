package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

@Data
public class ExpertsInfo {
    /**
     * 单位名
     */
    private String unit;
    /**
     * 用户名
     */
    private String user_name;
    /**
     * 各专家自己年度考核表id,i6rlc_experts_annual_assessment
     */
    private String u_id;
    /**
     * 专家的水平
     */
    private String expert_level;
}
