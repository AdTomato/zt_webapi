package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.AvgScore;
import com.authine.cloudpivot.web.api.bean.TotalScore;
import com.authine.cloudpivot.web.api.bean.VoteInfo;
import com.authine.cloudpivot.web.api.bean.deptSeasonAssess.DeptSeasonAssChild;
import com.authine.cloudpivot.web.api.bean.deptSeasonAssess.SubmitDeptSeasonRequest;

import java.util.List;

/**
 * @author zsh
 * @since 2019-12-03
 */
public interface SeasonAssessService {
    void saveScore(List<VoteInfo> voteInfo);

    int resetScore(String depteffectIds);

    List<AvgScore> countAvg(String id);

    int updateAvg(AvgScore avgScore);

    List<TotalScore> countTotal(String id);

    int updateTotal(TotalScore totalScore);

    int checkRepeat(String seasonassessmentId, String userId);

    void deleteWorkitemfinished(String instanceid);

    void insertDeptSeasonAssTables(List<DeptSeasonAssChild> deptSeasonAssTables);

    void insertDeptSeasonEvaInfo(SubmitDeptSeasonRequest submitDeptSeasonRequest);

    void updateBasicAvg(SubmitDeptSeasonRequest submitDeptSeasonRequest);

    void updateHandleScore(SubmitDeptSeasonRequest submitDeptSeasonRequest);

    void updatePartyHandleScore(SubmitDeptSeasonRequest submitDeptSeasonRequest);

    TotalScore countDeptTotal(SubmitDeptSeasonRequest submitDeptSeasonRequest);
}
