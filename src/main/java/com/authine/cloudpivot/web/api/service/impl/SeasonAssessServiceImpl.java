package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.AvgScore;
import com.authine.cloudpivot.web.api.bean.TotalScore;
import com.authine.cloudpivot.web.api.bean.VoteInfo;
import com.authine.cloudpivot.web.api.bean.deptSeasonAssess.DeptSeasonAssChild;
import com.authine.cloudpivot.web.api.bean.deptSeasonAssess.SubmitDeptSeasonRequest;
import com.authine.cloudpivot.web.api.mapper.SeasonAssessMapper;
import com.authine.cloudpivot.web.api.service.SeasonAssessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SeasonAssessServiceImpl implements SeasonAssessService {

    @Resource
    private SeasonAssessMapper seasonAssessMapper;

    @Override
    @Transactional
    public void saveScore(List<VoteInfo> voteInfo) {
        seasonAssessMapper.saveScore(voteInfo);
    }

    @Override
    @Transactional
    public int resetScore(String depteffectIds) {
        return seasonAssessMapper.resetScore(depteffectIds);
    }

    @Override
    public List<AvgScore> countAvg(String id) {
        return seasonAssessMapper.countAvg(id);
    }

    @Override
    public int updateAvg(AvgScore avgScore) {
        return seasonAssessMapper.updateAvgScore(avgScore);
    }

    @Override
    public List<TotalScore> countTotal(String id) {
        return seasonAssessMapper.countTotal(id);
    }

    @Override
    public int updateTotal(TotalScore totalScore) {
        return seasonAssessMapper.updateTotalScore(totalScore);
    }

    @Override
    public int checkRepeat(String seasonassessmentId, String userId) {
        return seasonAssessMapper.checkRepeat(seasonassessmentId, userId);
    }

    @Override
    public void deleteWorkitemfinished(String instanceid) {
        seasonAssessMapper.deleteWorkitemfinished(instanceid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDeptSeasonAssTables(List<DeptSeasonAssChild> deptSeasonAssTables) {
        seasonAssessMapper.insertDeptSeasonAssTables(deptSeasonAssTables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDeptSeasonEvaInfo(SubmitDeptSeasonRequest submitDeptSeasonRequest) {
        seasonAssessMapper.insertDeptSeasonEvaInfo(submitDeptSeasonRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBasicAvg(SubmitDeptSeasonRequest submitDeptSeasonRequest) {
        seasonAssessMapper.updateBasicAvg(submitDeptSeasonRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHandleScore(SubmitDeptSeasonRequest submitDeptSeasonRequest) {
        seasonAssessMapper.updateHandleScore(submitDeptSeasonRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePartyHandleScore(SubmitDeptSeasonRequest submitDeptSeasonRequest) {
        seasonAssessMapper.updatePartyHandleScore(submitDeptSeasonRequest);

    }

    @Override
    public TotalScore countDeptTotal(SubmitDeptSeasonRequest submitDeptSeasonRequest) {
        return seasonAssessMapper.countDeptTotal(submitDeptSeasonRequest);
    }
}
