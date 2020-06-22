package com.authine.cloudpivot.web.api.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:lfh
 * @Date:2020/3/2 17:09
 * @Description: 专家任期考核评分表 考核评分
 */
@Data
public class TermOfficeAssessmentScore {

    /**
     * id
     */
    private String id;
    /**
     * 单位
     */
    private String unit;

    /**
     * 姓名
     */
    private String user_name;

    /**
     * 专家等级
     */
    private String expert_level;

    /**
     * 履行职责(满分30分)
     */
    @JsonAlias("perform_duties")
    private BigDecimal perform_duties;

    /**
     * 创新工作(满分20分)
     */
    @JsonAlias("innovation_work")
    private BigDecimal innovation_work;

    /**
     * 建章立制(满分20分)
     */
    @JsonAlias("establishment")
    private BigDecimal establishment;

    /**
     * 人才培养(满分20分)
     */
    @JsonAlias("personnel_training")
    private BigDecimal personnel_training;

    /**
     * 总分
     */
    private BigDecimal total_score;

    /**
     * pid
     */
    private String pId;

    /**
     * parentId
     */
    private String parentId;
}
