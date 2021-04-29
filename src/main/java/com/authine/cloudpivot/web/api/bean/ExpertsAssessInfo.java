package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

@Data
public class ExpertsAssessInfo {
    /**
     * 年度
     */
    private String annual;
    /**
     * 考核类型
     */
    private String assessment_type;
    /**
     * 工作单位
     */
    private String work_unit;
    /**
     * 考核主体
     */
    private String assessment_content;
}
