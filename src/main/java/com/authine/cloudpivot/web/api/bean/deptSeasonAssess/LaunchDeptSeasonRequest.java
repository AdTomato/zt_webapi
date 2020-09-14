package com.authine.cloudpivot.web.api.bean.deptSeasonAssess;

import com.authine.cloudpivot.web.api.bean.DeptEffect;
import com.authine.cloudpivot.web.api.bean.User;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/9/8 11:14
 * @Version 1.0
 */
@Data
public class LaunchDeptSeasonRequest {
    @JsonAlias("dept_effect")
    private List<DeptEffect> deptEffectList;
    @JsonAlias("assessment_year")
    private int assessmentYear;
    @JsonAlias("assessment_season")
    private String assessmentSeason;
    @JsonAlias("id")
    private String oldParentId;
    @JsonAlias("basic_selector")
    private List<User>  basicSelector;
    @JsonAlias("handle_selector")
    private List<User>  handleSelector;
    @JsonAlias("partyhandle_selector")
    private List<User>  partyhandleSelector;


}
