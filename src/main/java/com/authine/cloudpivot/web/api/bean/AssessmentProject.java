package com.authine.cloudpivot.web.api.bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * @ClassName AssessmentProject
 * @author: lfh
 * @Date:2020/7/20 11:43
 * @Description: 考核项目
 **/
@Data
public class AssessmentProject {
    /**
     * id
     */
    private String id;
    /**
     * 考核项目
     */
    @JsonAlias("assessment_project")
    private String assessmentProject;

    /**
     * 考核内容
     */
    @JsonAlias("assessment_content")
    private String assessmentContent;
    /**
     * 得分
     */
    private String scores;

    /**
     * parentId
     */
    private String parentId;

}
