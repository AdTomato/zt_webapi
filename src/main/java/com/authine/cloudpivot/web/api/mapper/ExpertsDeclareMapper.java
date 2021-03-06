package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.*;

import java.util.List;
import java.util.Map;

/**
 * @Author:lfh
 * @Date: 2020/1/14 10:10
 * @Description： 专家申报mapper
 */

public interface ExpertsDeclareMapper {

    /**
     *
     * @param expertsInfo
     * @return
     */
    List<ExpertsSelectResult> getExpertsDeclareInfo(ExpertsInf expertsInfo);

    /**
     * 清空每个专家的表决结果
     * @param parentId
     */
    void clearExpertsReult(String parentId);

    /**
     * 查询全部专家明细信息
     * @param id
     * @return
     */
    List<ExpertsResultDetail> findExpertsInfo(String id);

    String getExpertsDeclareStatus(String id);

    /**
     * 更新每个专家的投票结果
     * @param expertsDeclareList
     */
    void updateAllExpertsDeclare(List<ExpertsDeclare> expertsDeclareList);

    /**
     * 通过投票结果获取全部通过的专家信息
     * @param map expertDeclareName,expertsDeclareOrganization
     * @return
     */
    ExpertsInfoList findExpertsFromInfoList(Map map);
    /**
     * 通过专家id获取专家的主要业绩
     * @param parentId
     * @return
     */
    List<ConditionsParticipations> getConditionsParticipations(String parentId);


    //插入参评条件子表信息
    void insertConditions(List<ConditionsParticipations> conditionsParticipations);

    List<String> getExpertsDeclareDetailsNum(String id);
}
