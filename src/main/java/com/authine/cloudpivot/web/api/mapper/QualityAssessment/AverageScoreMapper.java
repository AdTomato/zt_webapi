package com.authine.cloudpivot.web.api.mapper.QualityAssessment;


import com.authine.cloudpivot.web.api.bean.QualityAssessment.AverageScore;

/**
 * 中铁考核人员平均分映射器
 *
 * @Author Ke LongHai
 * @Date 2021/3/26 11:55
 * @Version 1.0
 */
public interface AverageScoreMapper {



    /**
     * 保存平均分数
     *
     * @param averageScore 平均分数
     */
    void saveAverageScore(AverageScore averageScore);

    /**
     * 更新平均分数
     *
     * @param gradedName       名字
     * @param averageScore averageScore
     */
    void updateAverageScore(String gradedName, Double averageScore);


    /**
     * 查询平均分数表中是否已有数据
     *
     * @param name 的名字
     * @return {@link AverageScore}
     */
    AverageScore selectAverageScore(String name);
}
