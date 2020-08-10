package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.AssessmentProject;
import com.authine.cloudpivot.web.api.bean.HeadquarterAchievement;
import com.authine.cloudpivot.web.api.dto.DeptDto;
import com.authine.cloudpivot.web.api.dto.DeptPerformanceAssessDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * @ClassName HeadquarterAchievementAssessService
 * @author: lfh
 * @Date:2020/7/20 15:31
 * @Description:
 **/
public interface HeadquarterAchievementAssessService {


     void addHeadquarterAchievements(List<HeadquarterAchievement> headquarterAchievements);

    void addAssessmentProjects(List<AssessmentProject> assessmentProjectList);

    List<DeptPerformanceAssessDto> queryAllDeptAchievement(LocalDate localDate,String deptId);

    List<DeptDto> queryAllAssessDepts();

    /**
     * 根据年份导出表头
     * @param year
     * @param deptId
     * @return
     */
    List<String> queryHeader(LocalDate year, String deptId);

    /**
     * 根基部门id查询时间
     * @param deptId
     * @return
     */
    List<Instant> queryDateByDeptId(String deptId);

    /**
     * 根据考核年度查询所有该年度的考核部门
     * @param localDate
     * @return
     */
    List<DeptDto> queryDeptListByYear(LocalDate localDate);
}
