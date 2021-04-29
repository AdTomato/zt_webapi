package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnnualEngineerEconomicDetails {
    /**
     * 单位
     */
    private String unit ;
    /**
     * 姓名
     */
    private String user_name ;
    /**
     * 专家等级
     */
    private String expert_level ;
    /**
     * 变更索赔方案策划评审研讨及难题解决(30分)
     */
    private BigDecimal solve_problems;
    /**
     * 成本管理及信息化建设与推广(30分)
     */
    private BigDecimal construction_promotion;
    /**
     * 项目精细化管理等黄总大专项活动(20分)
     */
    private BigDecimal special_events;
    /**
     * 人才培养及授课(20分)
     */
    private BigDecimal culture_teaching;
    /**
     * 总分
     */
    private BigDecimal total_score ;
    /**
     * 各专家自己年度考核表id,i6rlc_experts_annual_assessment
     */
    private String u_id ;
    /**
     * 主表id
     */
    private String id ;
}
