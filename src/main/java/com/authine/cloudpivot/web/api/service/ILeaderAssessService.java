package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.bean.*;

import java.math.BigDecimal;
import java.util.List;

public interface ILeaderAssessService {
      void insertEvaluationTable(List<LeaderEvaluationTable> leaderEvaluationTables);

     void cleanAssessmentScore(String deartment_assessment);

    List<ADComprehensiveAssessment> getADComprehensiveAssessmentByParentId(String objectId);

    BigDecimal getAssessmentDetailResultScore(String parentId, String assessmentProject);


    void insertLeadqualityAndChildTable(List<LaunchQuality> qualities, List<LaunLeadQuaCountRow> list);

    void insertLeadqualityChildTable(List<LaunLeadQuaCountRow> launLeadQuaCountRows);

    String  countLeadQuality(LaunchQuality launchQuality, UserModel user, DepartmentModel department);

    void updateAssessmentResultScoreList(List<FinalTotalResult> lists);

    String updateFixQuanResult(UserModel user, DepartmentModel department, LeadFixQuanCountInfo leadFixQuanCountInfo);



    void insertFixQuanAssDetails(LeadFixQuanCountInfo leadFixQuanCountInfo, UserModel user);

    void insertleaderQualityChildren(LaunchQuality launchQuality, String userId);
}
