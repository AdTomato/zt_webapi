package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.ExpertTermAvgScore;
import com.authine.cloudpivot.web.api.bean.ExpertsInfoResult;
import com.authine.cloudpivot.web.api.mapper.ExpertTermAssessmentMapper;
import com.authine.cloudpivot.web.api.parameter.ExpertBasicInfo;
import com.authine.cloudpivot.web.api.service.ExpertTermAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpertTermAssessmentServiceImpl implements ExpertTermAssessmentService {

	@Autowired
	private ExpertTermAssessmentMapper expertTermAssessmentMapper;
//	@Autowired
//	private ExpertAssessmentDetailMapper expertAssessmentDetailMapper;

/*
	@Override
	public List<ExpertTermAssessment> get(String majorCategories, String unit, String annual,
                                          String assessmentContent) {

		Example ex = new Example(ExpertTermAssessment.class);
		Criteria criteria = ex.createCriteria();
		criteria.andEqualTo("majorCategories", majorCategories);
		criteria.andEqualTo("unit", unit);
		criteria.andEqualTo("annual", annual);
		criteria.andEqualTo("assessmentContent", assessmentContent);
		List<ExpertTermAssessment> list = expertTermAssessmentMapper.selectByExample(ex);
		return list;
	}

	@Override
	public String post(String json) {

		JSONArray jsonArray = JSONArray.parseArray(json);
		for (Object j : jsonArray) {
			ExpertAssessmentDetail e = (ExpertAssessmentDetail) j;
			e.setId(UUID.randomUUID().toString().replace("-", ""));
			expertAssessmentDetailMapper.insertSelective(e);
		}
		return "success";
	}

	@Override
	public String calc(String pId) {

		Example ex = new Example(ExpertAssessmentDetail.class);
		Criteria criteria = ex.createCriteria();
		criteria.andEqualTo("pId", pId);
		List<ExpertAssessmentDetail> list = expertAssessmentDetailMapper.selectByExample(ex);
		int size = list.size();
		if (size == 0) {
			return "success";
		}
		BigDecimal performDuties = new BigDecimal(0);
		BigDecimal innovationWork = new BigDecimal(0);
		BigDecimal establishNewSystem = new BigDecimal(0);
		BigDecimal talentCultivate = new BigDecimal(0);
		for (ExpertAssessmentDetail e : list) {
			performDuties = performDuties.add(e.getPerformDuties());
			innovationWork = innovationWork.add(e.getInnovationWork());
			establishNewSystem = establishNewSystem.add(e.getEstablishNewSystem());
			talentCultivate = talentCultivate.add(e.getTalentCultivate());
		}
		// 回写
		ExpertTermAssessment term = new ExpertTermAssessment();
		term.setId(pId);
		if (list.get(0).getAssessmentContent().startsWith("所在单位")) {
			term.setDEstablishNewSystem(establishNewSystem.divide(new BigDecimal(size), 2));
			term.setDInnovationWork(innovationWork.divide(new BigDecimal(size), 2));
			term.setDPerformDuties(performDuties.divide(new BigDecimal(size), 2));
			term.setDTalentCultivate(talentCultivate.divide(new BigDecimal(size), 2));
		} else {
			term.setBEstablishNewSystem(establishNewSystem.divide(new BigDecimal(size), 2));
			term.setBInnovationWork(innovationWork.divide(new BigDecimal(size), 2));
			term.setBPerformDuties(performDuties.divide(new BigDecimal(size), 2));
			term.setBTalentCultivate(talentCultivate.divide(new BigDecimal(size), 2));
		}
		expertTermAssessmentMapper.updateByPrimaryKeySelective(term);
		return "success";
	}
*/

	/**
	 * 专家任期考核评分 获取专家信息
	 * @param expertBasicInfo 专辑任期考核的基础数据
	 * @return
	 */
	@Override
	public List<ExpertsInfoResult> findExpertsBasicInfo(ExpertBasicInfo expertBasicInfo) {
		return expertTermAssessmentMapper.findExpertsBasicInfo(expertBasicInfo);
	}

	/**
	 * 清空打分项
	 * @param parentId 专家任期考核评分表id
	 */
	@Override
	public void clearAssessmentScore(String parentId) {
		expertTermAssessmentMapper.clearAssessmentScore(parentId);
	}

	/**
	 * 通过评委数量判断是否打分结束
	 * @param id
	 * @return
	 */
	@Override
	@Transactional
	public List<String> getAssessMentDetailNum(String id) {
		return expertTermAssessmentMapper.getAssessMentDetailNum(id);
	}

	/**
	 * 计算平均分
	 * @param id
	 * @return
	 */
	@Override
	public List<ExpertTermAvgScore> countFinanceAvg(String id) {
		return expertTermAssessmentMapper.countFinanceAvg(id);
	}

	/**
	 * 将平均分更新考核评分表
	 * @param expertTermAvgScores
	 */
	@Override
	public void updateFinalAvg(List<ExpertTermAvgScore> expertTermAvgScores) {
		expertTermAssessmentMapper.updateFinalAvg(expertTermAvgScores);
	}

	/**
	 * 更新专家任期考核所在单位评分
	 * @param expertTermAvgScores
	 */
	@Override
	public void updateDeptJudgesScore(List<ExpertTermAvgScore> expertTermAvgScores) {
		expertTermAssessmentMapper.updateDeptJudgesScore(expertTermAvgScores);
	}

	/**
	 * 更新专家任期考核局评委会评分
	 * @param expertTermAvgScores
	 */
	@Override
	public void updateBureauJudgesScore(List<ExpertTermAvgScore> expertTermAvgScores) {
		expertTermAssessmentMapper.updateBureauJudgesScore(expertTermAvgScores);
	}
}
