package com.authine.cloudpivot.web.api.bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * @ClassName HeadquarterAchievement
 * @author: lfh
 * @Date:2020/7/20 15:06
 * @Description: 总部部门业绩评价表实体类
 **/
@Data
public class HeadquarterAchievement extends BaseBean {
    /**
     * 被考核部门
     */
    private String assessmentDeptment;

    /**
     * 考核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd" ,timezone="GMT+8")
    private Instant assessmentDate;

    /**
     * 评委
     */
    private String judges;

    /**
     * 权重
     */
    private Double weight;

    /**
     * 考核明细
     */
    private List<AssessmentProject> assessmentProjects;
    /**
     * 关联发起表的id
     */
    @JsonAlias("send_form_id")
    private String sendFormId;
}
