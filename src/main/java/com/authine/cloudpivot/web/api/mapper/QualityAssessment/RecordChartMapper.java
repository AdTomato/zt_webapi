package com.authine.cloudpivot.web.api.mapper.QualityAssessment;


import com.authine.cloudpivot.web.api.bean.QualityAssessment.RecordChart;

import java.util.List;

/**
 * 被打分人次数表单映射器
 *
 * @Author Ke LongHai
 * @Date 2021/3/26 11:55
 * @Version 1.0
 */
public interface RecordChartMapper {

    /**
     * 查询被打分人次数记录表有哪些人被打分了
     *
     * @return {@link RecordChart}
     */
    List<RecordChart> getChartName();
    /**
     * 被打分人次数
     * @param gradedName 名字
     * @return {@link RecordChart}
     */
    RecordChart getChart(String gradedName);

    /**
     * 插入被打分人次数信息
     * @param recordChart 打分人次数信息
     */
    void insertChart(RecordChart recordChart);

    /**
     * 更新被打分人次数信息
     *
     * @param gradedName 名字
     * @param gradedNumber  次数
     */
    void updateChart(String gradedName, int gradedNumber);




}
