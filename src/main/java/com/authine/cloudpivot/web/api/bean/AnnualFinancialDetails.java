package com.authine.cloudpivot.web.api.bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnnualFinancialDetails {
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
     * 工作实绩(40分)
     */
    @JsonAlias("work_achievement")
    private BigDecimal work_achievement ;
    /**
     * 课题研究(20分)
     */
    private BigDecimal topic_research ;
    /**
     * 学习培训(20分)
     */
    private BigDecimal study_train ;
    /**
     * 交流授课(20分）
     */
    private BigDecimal echange_teach;
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
