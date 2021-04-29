package com.authine.cloudpivot.web.api.bean.deptSeasonAssess;

import com.authine.cloudpivot.web.api.bean.DeptName;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/9/8 14:26
 * @Version 1.0
 */
@Data
public class SubmitDeptSeasonRequest {
    /**
     * 年度
     */
    @JsonAlias("assessment_year")
    private int assessmentYear;
    /**
     * 季度
     */
    @JsonAlias("assessment_season")
    private String assessmentSeason;
    /**
     * 发起表id
     */
    @JsonAlias("old_parent_id")
    private String oldParentId;
    /**
     * 部门
     */
    @JsonAlias("dept")
    private List<DeptName> deptName;
    /**
     * 总分
     */
    @JsonAlias("merge_score")
    private BigDecimal mergeScore;
    /**
     * 部门名称字符串
     */
    private String deptNameToString;

    /**
     * 活动节点
     */
    private String activityCode;
    /**
     * 部门id
     */
    private String deptNameId;
    /**
     * 用户id
     */
    private String userId;
}
