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
    @JsonAlias("assessment_year")
    private int assessmentYear;
    @JsonAlias("assessment_season")
    private String assessmentSeason;
    @JsonAlias("old_parent_id")
    private String oldParentId;
    @JsonAlias("dept")
    private List<DeptName> deptName;
    @JsonAlias("merge_score")
    private BigDecimal mergeScore;
    private String deptNameToString;

    private String activityCode;
    private String deptNameId;
    private String userId;
}
