package com.authine.cloudpivot.web.api.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author:lfh
 * @Date:2020/3/2 14:38
 * @Description: 专家任期基本信息 用于查询信息填充到评分表
 */
@Data
public class ExpertBasicInfo {

    /**
     * 考核类别
     */
    @JsonAlias("assessment_type")
    @NotNull
    private  String  assessment_type;

    /**
     * 年度
     */
    @JsonAlias("annual")
    @NotNull
    private String annual;

    /**
     * 考核主体
     */
    @JsonAlias("assessment_content")
    @NotNull
    private String assessment_content;

    /**
     * 工作单位
     */
    @JsonAlias("work_unit")
    private String work_unit;
}
