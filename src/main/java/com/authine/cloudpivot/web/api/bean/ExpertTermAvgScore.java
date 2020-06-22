package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

/**
 * @Author:lfh
 * @Date:2020/3/3 11:10
 * @Description: 专家任期考核平均分
 */
@Data
public class ExpertTermAvgScore {
    /**
     *关联专人任期考核评分表
     */
    private String expert_office_assessment_id;

    /**
     * 关联专家任期考核
     */
    private String expert_term_assessment_id;

    /**
     * id
     */
    private String id;
    /**
     * 履行职责(满分30分)
     */
    private Double avgPerformDuties;

    /**
     * 创新工作(满分20分)
     */
    private Double avgInnovationWork;

    /**
     *建章立制(满分20分)
     */
    private Double avgEstablishment;

    /**
     * 人才培养(满分20分)
     */
    private Double avhPersonnelTraining;

    /**
     * 总分
     */
    private Double avgTotalScore;


}
