package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.bean.deputyassess.Dept;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;

import java.util.List;

/**
 * 副评估服务
 *
 * @author zhengshihao
 * @date 2021/04/28
 */
public interface DeputyAssessService  {

    /**
     * 插入副职及以上表单子表考核项
     *
     * @param deptDeputyAssessTables 部门副评估表
     */
    void insertDeptDeputyAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables);

    /**
     * 插入或更新副职及以上人员结果
     *
     * @param oldParentId 发起表父id
     * @param id          id
     * @param assessName  评估的名字
     */
    void insertOrUpdateDeputyAssesment(String oldParentId, String id,String assessName);

    List<LeadPerson> selectAssessedPeopleFromResult(String id);

    List<String> selectAnnualFromLaunch();

    List<Dept> selectDeptFromResult();

    List<String> selectHeaders(String deptId,  String annual);
    List<SubmitDeputyAssChild>  selectAssessByDeptIdAndAnnualAndSeasonAndAssessName(String deptId, String annual, String season, String assessName);
    List<SubmitDeputyAssChild> selectAssessByDeptIdAndAnnualAndAssessName(String deptId, String annual, String assessName);
    void insertDeputyDetails(List<SubmitDeputyAssChild> list);


    /**
     * 插入科长及以下表单子表考核项
     *
     * @param deptDeputyAssessTables 部门副评估表
     */
    void insertSectionAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables);

    /**
     * 插入或更新科长及以下考核结果
     *
     * @param oldParentId 发起表id
     * @param id          id
     * @param assessName  评估的名字
     */
    void insertOrUpdateSectionAssesment(String oldParentId, String id,String assessName);

    List<String> selectSectionHeaders(String deptId, String annual);

    List<Dept> selectSectionDeptFromResult();

    List<LeadPerson> selectSectionAssessedPeopleFromResult(String id);

    void insertSectionDetails(List<SubmitDeputyAssChild> list);

    List<SubmitDeputyAssChild> selectSectionAssessByDeptIdAndAnnualAndSeasonAndAssessName(String deptId, String annual, String season, String assessName);

    List<SubmitDeputyAssChild> selectSectionAssessByDeptIdAndAnnualAndAssessName(String deptId, String annual, String assessName);
}
