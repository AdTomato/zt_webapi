package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.ExpertTermAvgScore;
import com.authine.cloudpivot.web.api.bean.ExpertsInfoResult;
import com.authine.cloudpivot.web.api.parameter.ExpertBasicInfo;

import java.util.List;

/**
 * @Author:lfh
 * @Date: 2020/3/2 15:11
 * @Description：专家任期考核服务层
 */
public interface ExpertTermAssessmentService {

	/**
	 * 专家任期考核评分 获取专家信息
	 * @param expertBasicInfo 专辑任期考核的基础数据
	 * @return 返回需要填充的数据
	 */
	List<ExpertsInfoResult> findExpertsBasicInfo(ExpertBasicInfo expertBasicInfo);

	/**
	 * 清空打分项
	 * @param parentId 专家任期考核评分表id
	 */
	void clearAssessmentScore(String parentId);

	/**
	 * 通过评委数量判断是否打分结束
	 * @param id
	 */
	List<String> getAssessMentDetailNum(String id);

	/**
	 * 计算平均分
	 * @param id
	 * @return
	 */
	List<ExpertTermAvgScore> countFinanceAvg(String id);

	/**
	 * 将平均分更新考核评分表
	 * @param expertTermAvgScores
	 */
	void updateFinalAvg(List<ExpertTermAvgScore> expertTermAvgScores);

	/**
	 * 更新专家任期考核所在单位评分
	 * @param expertTermAvgScores
	 */
    void updateDeptJudgesScore(List<ExpertTermAvgScore> expertTermAvgScores);

	/**
	 * 更新专家任期考核局评委会评分
	 * @param expertTermAvgScores
	 */
	void updateBureauJudgesScore(List<ExpertTermAvgScore> expertTermAvgScores);

	/*List<ExpertTermAssessment> get(String majorCategories, String unit, String annual, String assessmentContent);

	String post(String json);
	
	String calc(String id);*/


}
