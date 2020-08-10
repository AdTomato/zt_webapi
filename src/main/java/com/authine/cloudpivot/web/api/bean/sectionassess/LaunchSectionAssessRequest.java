package com.authine.cloudpivot.web.api.bean.sectionassess;

import com.authine.cloudpivot.web.api.bean.DeptName;
import com.authine.cloudpivot.web.api.bean.User;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchJudges;
import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/7/20 11:32
 * @Version 1.0
 */

@Data
@ApiModel
public class LaunchSectionAssessRequest {
        @ApiModelProperty(value = "主表id")
        private String id ;
        @ApiModelProperty(value = "年度")
        private String annual;
        @ApiModelProperty(value = "部门")
        private DeptName dept;
        @ApiModelProperty(value = "被考核人列表")
        private List<User> people;
        @ApiModelProperty(value = "考核项子表")
        @JsonAlias(value ="launch_section_asselement")
        private List<LaunchDeputyAssChild>  deputy_assesselement;
        @ApiModelProperty(value = "评委表子表")
        @JsonAlias(value = "launch_section_judges")
        private List<LaunchJudges>  deputy_judges;
}
