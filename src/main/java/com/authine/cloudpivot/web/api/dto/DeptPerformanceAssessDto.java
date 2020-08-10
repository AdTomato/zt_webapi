package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.bean.User;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * @ClassName DeptPerformanceAssessDto
 * @author: lfh
 * @Date:2020/7/21 11:40
 * @Description:
 **/
@Data
public class DeptPerformanceAssessDto {
    /**
     * 部门
     */
    private String dept;
    /**
     * 考核时间
     */
    private Instant date;
    /**
     * 评委
     */
    private User judges;
    /**
     * 权重
     */
    private Double weight;
    ///**
    // * 考核项目
    // */
    //private String project;
    ///**
    // * 分数
    // */
    //private Double score;
    /**
     * 考核 项目分数
     */
    //private Map<String,Double> projectScoreMap;
    List<TestProject> testProjects;
}
