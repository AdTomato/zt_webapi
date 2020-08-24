package com.authine.cloudpivot.web.api.bean.EmployeeEvalution;

import com.authine.cloudpivot.web.api.bean.DeptName;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchJudges;
import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/8/17 16:16
 * @Version 1.0
 */
@Data
@ApiModel
public class LaunchEvaluationRequest {
    @ApiModelProperty(value = "主表id")
    private String id ;
    @ApiModelProperty(value = "年度")
    private String annual;
    @ApiModelProperty(value = "季度")
    private String season;
    @ApiModelProperty(value = "部门")
    private DeptName dept;
    @ApiModelProperty(value = "考核类型")
    private String assess_name;
    @ApiModelProperty(value = "副职及以上表考核项")
    @JsonAlias(value ="launch_deputy_ass_ele")
    private List<LaunchDeputyAssChild> deputy_assesselement;

    @ApiModelProperty(value = "科长及以下表考核项")
    @JsonAlias(value ="launch_section_ass_ele")
    private List<LaunchDeputyAssChild>  section_assesselement;

    @ApiModelProperty(value = "评委表子表")
    @JsonAlias(value = "launch_emp_per_judges")
    private List<LaunchJudges>  deputy_judges;
}
