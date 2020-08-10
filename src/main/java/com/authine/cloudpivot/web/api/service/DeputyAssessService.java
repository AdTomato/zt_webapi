package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.DeptEffect;
import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.bean.User;
import com.authine.cloudpivot.web.api.bean.VoteInfo;
import com.authine.cloudpivot.web.api.bean.deputyassess.Dept;
import com.authine.cloudpivot.web.api.bean.deputyassess.Header;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;

import java.util.HashSet;
import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/7/20 17:00
 * @Version 1.0
 */
public interface DeputyAssessService  {

    void insertDeptDeputyAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables);

    void insertSubmitDeputyAsselement(SubmitDeputyAssChild submitDeputyAssChild);

    void insertOrUpdateDeputyAssesment(String oldParentId, String id);

    List<Dept> selectDeptFromLaunch();

    List<LeadPerson> selecAssessedPeopleFromLaunch(String id);

    List<SubmitDeputyAssChild> selectAssessByDeptIdAndAssessedPersonIdAndAnnual(String deptId, String assessedPersonId,String annual);

    List<String> selectAnnualFromLaunch();

    List<Dept> selectDeptFromResult();

    List<String> selectHeaders(String deptId, String assessedPersonId, String annual);





    void insertSectionAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables);

    void insertSubmitSectionAsselement(SubmitDeputyAssChild submitDeputyAssChild);
    void insertOrUpdateSectionAssesment(String oldParentId, String id);


    List<Dept> selectSectionDeptFromLaunch();

    List<LeadPerson> selectSectionAssessedPeopleFromLaunch(String id);

    List<SubmitDeputyAssChild> selectSectionAssessByDeptIdAndAssessedPersonIdAndAnnual(String deptId, String assessedPersonId, String annual);

    List<String> selectSectionHeaders(String deptId, String assessedPersonId, String annual);

    List<Dept> selectSectionDeptFromResult();
}
