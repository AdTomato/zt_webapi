package com.authine.cloudpivot.web.api.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * @ClassName BeginAchievementAssess
 * @author: lfh
 * @Date:2020/7/20 11:38
 * @Description: 发起总部部门业绩评价表
 **/
@Data
public class BeginAchievementAssess {
    /**
     * id
     */
    private String id;
    /**
     * 被考核部门
     */
    private Department assessmentDepartment;

    /**
     * 考核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone="GMT+8")
    private Instant assessmentDate;

    /**
     * 考核项目
     */
    private List<AssessmentProject> assessmentProjects;

    /**
     * 评委权重
     */
    private List <JudgesWeight> judgesWeights;

}
