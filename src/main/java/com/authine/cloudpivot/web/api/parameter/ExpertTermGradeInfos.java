package com.authine.cloudpivot.web.api.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

/**
 * @Author:lfh
 * @Date:2020/3/2 16:16
 * @Description: 专家任期考核评分信息 插入任期考核明细
 */
@Data
public class ExpertTermGradeInfos {


    /**
     *考核评分子表
     */
    @JsonAlias("term_office_assessment_score")
    private List<TermOfficeAssessmentScore> term_office_assessment_score;

    /**
     * id
     */
    private String id;
    /**
     * 考核类别
     */
    @JsonAlias("assessment_type")
    private  String  assessment_type;

    /**
     * 年度
     */
    @JsonAlias("annual")
    private String annual;

    /**
     * 考核主体
     */
    @JsonAlias("assessment_content")
    private String assessment_content;

    /**
     * 工作单位
     */
    @JsonAlias("work_unit")
    private String work_unit;

    private Integer num;

}
