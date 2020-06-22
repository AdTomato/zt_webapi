package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 机关部门考核打分明细
 */
@Data
public class LeaderEvaluationTable {

    /**
     * id值
     */
    private String id;

    /**
     * 父id
     */
    private String parentId;

    /**
     * 考核内容
     */
    private String assess_content;

    /**
     * 考核指标
     */
    private String assess_index;

    /**
     * 考核要素及标准
     */
    private String assess_criteria;


    private String score;

    private BigDecimal sortKey;
}
