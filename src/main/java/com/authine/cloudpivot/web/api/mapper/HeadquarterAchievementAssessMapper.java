package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.AssessmentProject;
import com.authine.cloudpivot.web.api.bean.HeadquarterAchievement;
import com.authine.cloudpivot.web.api.dto.DeptDto;
import com.authine.cloudpivot.web.api.dto.JudgesDeptWeightDto;
import com.authine.cloudpivot.web.api.dto.JudgesProjectScoreDto;
import com.authine.cloudpivot.web.api.dto.TestProject;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * @ClassName HeadquarterAchievementAssessMapper
 * @author: lfh
 * @Date:2020/7/20 15:35
 * @Description:
 **/
public interface HeadquarterAchievementAssessMapper {
    void addHeadquarterAchievements(List<HeadquarterAchievement> headquarterAchievements);

    void addAssessmentProjects(List<AssessmentProject> assessmentProjectList);

    List<JudgesDeptWeightDto>  queryAllDeptAchievement(LocalDate localDate, String department);

    List<JudgesProjectScoreDto> queryAchievementByDept(String deptString);

    List<String> queryAllAssessDepts();

    DeptDto queryDept(String  dept);

    List<TestProject> queryJudgeScore(String bid, String user);

    List<String> queryHeader(LocalDate year, String dept);

    List<Instant> queryDateByDeptId(String dept);

    List<String> queryDeptListByYear(LocalDate localDate);
}
