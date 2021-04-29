package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnnualEngineerTechDetails {
    /**
     * 单位
     */
    private String unit ;
    /**
     * 姓名
     */
    private String user_name ;
    /**
     * 专家的等级
     */
    private String expert_level ;
    /**
     * 施组评审研讨(40分)
     */
    private BigDecimal review_discussion;
    /**
     * 解决技术难题与提供现场服务(20分)
     */
    private BigDecimal poser_field_service;
    /**
     * 技术骨干人才培养及培训授课(20分)
     */
    private BigDecimal train_teaching;
    /**
     * 科学技术创新(20分)
     */
    private BigDecimal technology_innovate;
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
