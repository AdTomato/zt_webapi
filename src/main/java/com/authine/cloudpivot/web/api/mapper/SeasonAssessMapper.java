package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.AvgScore;
import com.authine.cloudpivot.web.api.bean.TotalScore;
import com.authine.cloudpivot.web.api.bean.VoteInfo;
import com.authine.cloudpivot.web.api.bean.deptSeasonAssess.DeptSeasonAssChild;
import com.authine.cloudpivot.web.api.bean.deptSeasonAssess.SubmitDeptSeasonRequest;

import java.util.List;

public interface SeasonAssessMapper {
    void saveScore(List<VoteInfo> voteInfo);

    int resetScore(String depteffectIds);

    List<AvgScore> countAvg(String id);

    int updateAvgScore(AvgScore avgScore);

    List<TotalScore> countTotal(String id);

    int updateTotalScore(TotalScore totalScore);

    int checkRepeat(String seasonassessmentId, String userId);

    void deleteWorkitemfinished(String instanceid);

    void insertDeptSeasonAssTables(List<DeptSeasonAssChild> deptSeasonAssTables);

    void insertDeptSeasonEvaInfo(SubmitDeptSeasonRequest submitDeptSeasonRequest);

    void updateBasicAvg(SubmitDeptSeasonRequest submitDeptSeasonRequest);

    void updateHandleScore(SubmitDeptSeasonRequest submitDeptSeasonRequest);

    void updatePartyHandleScore(SubmitDeptSeasonRequest submitDeptSeasonRequest);

    TotalScore countDeptTotal(SubmitDeptSeasonRequest submitDeptSeasonRequest);
}
