package com.authine.cloudpivot.web.api.bean.QualityAssessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 考核平均分
 *
 * @Author Ke LongHai
 * @Date 2021/4/16 16:35
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageScore extends BaseEntity implements Serializable {

    /**
     * 被打分人姓名
     */
    private String gradedName;

    /**
     * 平均总分
     */
    private Double averageScore;

}
