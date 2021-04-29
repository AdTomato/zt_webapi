package com.authine.cloudpivot.web.api.bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class ExpertsAssessScore {
    /**
     * 主表id
     */
    private String id;
    /**
     * 考核类型
     */
    private String assessment_type;
    /**
     * 年度
     */
    private String annual;
    /**
     * 考核主体
     */
    private String assessment_content;
    /**
     * 工作单位
     */
    private String work_unit;
    /**
     * 财务审计专家年度考核评分表
     */
    @JsonAlias("financial_audit_annual")
    private List<AnnualFinancialDetails> financial_audit_annual;
    /**
     * 工程技术专家年度考核评分表
     */
    private List<AnnualEngineerTechDetails> engineer_technology_annual;
    /**
     * 工程经济专家年度考核评分表
     */
    private List<AnnualEngineerEconomicDetails> engineer_economy_annual;
}
