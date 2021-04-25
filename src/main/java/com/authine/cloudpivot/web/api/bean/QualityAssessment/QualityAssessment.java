package com.authine.cloudpivot.web.api.bean.QualityAssessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 主要领导能力素质评价主表
 * @Author Ke LongHai
 * @Date 2021/4/16 16:26
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualityAssessment {

    /**
     * 评分人
     */
    private String scorer;

    /**
     * 被打分的人
     */
    private String byPeopleWho;

    /**
     * 总分数
     */
    private int grossScore;


}
