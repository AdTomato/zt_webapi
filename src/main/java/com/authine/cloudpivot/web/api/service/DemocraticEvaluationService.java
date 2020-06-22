package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.DemocraticEvaluation;
import com.authine.cloudpivot.web.api.bean.GDemocraticEvaluation;
import com.authine.cloudpivot.web.api.bean.SDemocraticEvaluation;
import com.authine.cloudpivot.web.api.parameter.DemocraticEvaluationUpdate;

import java.util.List;
import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-01-05 02:13
 * @Description: 民主评议表服务接口
 */
public interface DemocraticEvaluationService {

    /**
     * 获取发起工作民主评议表所有信息
     *
     * @param id 发起工作发起工作民主评议表id
     * @return 发起工作民主评议表信息
     */
    public SDemocraticEvaluation getSDemocraticEvaluationDataById(String id);

    /**
     * 根据传递过去的参数,获取符合规则的工作民主评议表
     *
     * @param map 参数
     * @return 符合规则的所有的工作民主评议表
     */
    public List<DemocraticEvaluation> getAllDemocraticEvaluationData(Map map);

    /**
     * 更新发起工作民主评议表所有信息
     *
     * @param sd 发起工作民主评议表
     */
    public void updateSDemocraticEvaluation(SDemocraticEvaluation sd);

    /**
     * 插入工作民主评议表数据
     *
     * @param democraticEvaluations 工作民主评议表数据
     */
    void insertDemocraticEvaluation(List<GDemocraticEvaluation> democraticEvaluations);

    /**
     * 更新发起民主评议表数据
     *
     * @param democraticEvaluationUpdate
     */
    void updateDemocratic(DemocraticEvaluationUpdate democraticEvaluationUpdate);

    void updateDemocratic(String id);
}
