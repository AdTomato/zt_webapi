package com.authine.cloudpivot.web.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.dto.DeptDto;
import com.authine.cloudpivot.web.api.dto.DeptPerformanceAssessDto;
import com.authine.cloudpivot.web.api.dto.JudgesDeptWeightDto;
import com.authine.cloudpivot.web.api.dto.TestProject;
import com.authine.cloudpivot.web.api.mapper.HeadquarterAchievementAssessMapper;
import com.authine.cloudpivot.web.api.service.HeadquarterAchievementAssessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * @ClassName HeadquarterAchievementAssessServiceImpl
 * @author: lfh
 * @Date:2020/7/20 15:33
 * @Description:
 **/
@Service
@Slf4j
public class HeadquarterAchievementAssessServiceImpl implements HeadquarterAchievementAssessService {

    @Resource
    private HeadquarterAchievementAssessMapper headquarterAchievementAssessMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addHeadquarterAchievements(List<HeadquarterAchievement> headquarterAchievements) {
        headquarterAchievementAssessMapper.addHeadquarterAchievements(headquarterAchievements);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAssessmentProjects(List<AssessmentProject> assessmentProjectList) {
        headquarterAchievementAssessMapper.addAssessmentProjects(assessmentProjectList);
    }

    /**
     * 查询所有的总部部门业绩评价
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DeptPerformanceAssessDto> queryAllDeptAchievement(LocalDate localDate, String deptId) {
        Department dept = new Department();
        if (deptId != null) {
            dept.setId(deptId);
            dept.setType(1);
        }
        //查所有评委打分情况
        String departmentString = JSON.toJSONString(Arrays.asList(dept));
        //获取全部打过分的部门-对应权重和评委
        List<JudgesDeptWeightDto> judgesDeptWeightDtos = headquarterAchievementAssessMapper.queryAllDeptAchievement(localDate,departmentString);
        List<DeptPerformanceAssessDto> deptPerformanceAssessDtoList = new ArrayList<>();
        List<TestProject> testProjects = new ArrayList<>();

        //遍历每个部门
        for (JudgesDeptWeightDto judgesDeptWeightDto : judgesDeptWeightDtos) {
            log.info("遍历部门");
            Map<String, Double> projectScoreMap = new HashMap<>(16);
            //存储每个部门的考核内容的打分情况
            DeptPerformanceAssessDto deptPerformanceAssess = new DeptPerformanceAssessDto();
            deptPerformanceAssess.setDate(judgesDeptWeightDto.getDate());
            Department department = JSONArray.parseArray(judgesDeptWeightDto.getDept(), Department.class).get(0);
            DeptDto deptDto = headquarterAchievementAssessMapper.queryDept(department.getId());
            log.info("部门为"+deptDto.getName());
            deptPerformanceAssess.setDept(deptDto.getName());
            //一条发起打分的数据
            List<JudgesWeight> judgesWeightList = judgesDeptWeightDto.getJudgesWeightList();
            for (JudgesWeight judgesWeight : judgesWeightList) {
                log.info("遍历权重为"+judgesWeight.getWeight()+"的评委");
                //一条发起表单的评委权重数据
                List<User> users = JSONArray.parseArray(judgesWeight.getJudges(), User.class);
                Double weights = judgesWeight.getWeight();
                //遍历同一权重的评委打分数据
                for (User user : users) {
                    log.info("评委id："+user.getId());
                    //每个评委对该部门的每项考核打分数据
                    List<TestProject> projects = headquarterAchievementAssessMapper.queryJudgeScore(judgesDeptWeightDto.getBid(),JSON.toJSONString(Arrays.asList(user)));
                    for (TestProject project : projects) {
                        if (projectScoreMap.get(project.getAssessmentProject()) == null) {
                            //首次存分
                            projectScoreMap.put(project.getAssessmentProject(), project.getScore() * weights * 1.0/users.size()/100);
                        } else {
                            //已存过分
                            Double oldScore = projectScoreMap.get(project.getAssessmentProject());
                            projectScoreMap.put(project.getAssessmentProject(), oldScore + project.getScore() * weights * 1.0/users.size()/100);
                        }
                    }
                }
            }
            Set<Map.Entry<String, Double>> entries = projectScoreMap.entrySet();
            for (Map.Entry<String, Double> entry : entries) {
                TestProject testProject = new TestProject();
                testProject.setAssessmentProject(entry.getKey());
                testProject.setScore(entry.getValue());
                testProjects.add(testProject);
            }
            deptPerformanceAssess.setTestProjects(testProjects);
            deptPerformanceAssessDtoList.add(deptPerformanceAssess);
        }
        return deptPerformanceAssessDtoList;
    }

    @Override
    public List<DeptDto> queryAllAssessDepts() {
        List<String> allAssessDepts = headquarterAchievementAssessMapper.queryAllAssessDepts();
        List<DeptDto> deptDtoList = new ArrayList<>();
        if (allAssessDepts ==null || allAssessDepts.isEmpty()){
            return deptDtoList;
        }
        for (String allAssessDept : allAssessDepts) {
            Department department = JSONArray.parseArray(allAssessDept, Department.class).get(0);
            DeptDto deptDto = headquarterAchievementAssessMapper.queryDept(department.getId());
            deptDtoList.add(deptDto);
        }
        return deptDtoList;
    }

    @Override
    public List<String> queryHeader(LocalDate year, String deptId) {
        Department department = new Department();
        department.setType(1);
        department.setId(deptId);
        String dept = JSON.toJSONString(Arrays.asList(department));
        return headquarterAchievementAssessMapper.queryHeader(year,dept);
    }

    @Override
    public List<Instant> queryDateByDeptId(String deptId) {
        Department department = new Department();
        department.setId(deptId);
        department.setType(1);
        String dept = JSON.toJSONString(Arrays.asList(department));
        return headquarterAchievementAssessMapper.queryDateByDeptId(dept);
    }

    @Override
    public List<DeptDto> queryDeptListByYear(LocalDate localDate) {
        List<String> allAssessDepts = headquarterAchievementAssessMapper.queryDeptListByYear(localDate);
        List<DeptDto> deptDtoList = new ArrayList<>();
        if (allAssessDepts ==null || allAssessDepts.isEmpty()){
            return deptDtoList;
        }
        for (String allAssessDept : allAssessDepts) {
            Department department = JSONArray.parseArray(allAssessDept, Department.class).get(0);
            DeptDto deptDto = headquarterAchievementAssessMapper.queryDept(department.getId());
            deptDtoList.add(deptDto);
        }
        return deptDtoList;
    }


}
