package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ILeaderAssessMapper {
    void insertTable(List<LeaderEvaluationTable> leaderEvaluationTables);

    void cleanAssessmentScore(String deartment_assessment);

    String getFixQuanStatus(String objectId);

    List<ADComprehensiveAssessment> getADComprehensiveAssessmentByParentId(String objectId);

    BigDecimal getAssessmentDetailResultScore(@Param("parentId") String parentId, @Param("assessmentProject") String assessmentProject);

    void updateAssessmentDetailResultScore(@Param("id") String id, @Param("resultScore") BigDecimal resultScore);

    void insertleadqualitychildTable(List<LaunLeadQuaCountRow> list);

    void insertLeadQuality(List<LaunchQuality> qualities);

    List<LaunLeadQuaCountRow> selectLaunchiLeaderqualityCount(String parentId);

    LaunLeadQuaCountRow selectRowByrowId(String oldRowId);

    void updateRow(LaunLeadQuaCountRow countRow);

    void cleanLeadQualityChildren(String parentId);

    void updateAssessmentResultScoreList(List<FinalTotalResult> lists);

    void updateLaunchFixQuanRow(LaunchFixedQuantityRow row);

    LaunchFixedQuantityRow getFixQuanAssPeopleByParentId(@Param("oldParentId") String oldParentId, @Param("assessedName") String assessedName);

    void updateAssessNum(@Param("num") BigDecimal num, @Param("oldParentId") String oldParentId, @Param("leadPersonId") String leadPersonId);

    BigDecimal getNumByOldParentId(@Param("oldParentId") String oldParentId, @Param("leadPersonId") String leadPersonId);

    void insertFixQuanAssDetails(FixQuanAssDetails fixQuanAssDetails);

    List<FixQuanAssDetails>  selectfixquanassdetails(FixQuanAssDetails fixQuanAssDetails);

    int selectfixquanassdetailsrow(FixQuanAssDetails fixQuanAssDetails);

    void insertLeadQualityDetails(List<LeaderQualityChild> leaderQualityChildren);

    LaunLeadQuaCountRow selectqualitydetails(LeaderQualityChild child);

    //void insertFixQuanScoreChildTable(List<FixQuanScoreChild> fixQuanScoreChildTable);
}
