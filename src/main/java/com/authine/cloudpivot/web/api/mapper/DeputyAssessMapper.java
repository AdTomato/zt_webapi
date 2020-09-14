package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.bean.deputyassess.Dept;
import com.authine.cloudpivot.web.api.bean.deputyassess.Header;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/7/20 17:06
 * @Version 1.0
 */
@Repository
public interface DeputyAssessMapper {
    void insertDeptDeputyAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables);

    void insertSubmitDeputyAsselement(SubmitDeputyAssChild submitDeputyAssChild);

    List<SubmitDeputyAssChild> selectDetails(String oldParentId, String assessedPersonId);

    void insertAssessResult(SubmitDeputyAssChild submitDeputyAssChild);

    Integer isHaveAssessresult(SubmitDeputyAssChild submitDeputyAssChild);

    void updateAssessResult(SubmitDeputyAssChild submitDeputyAssChild);

    List<Dept> selectDeptFromLaunch();

    List<String> selectAssessedPeopleFromLaunch(String id);

    List<LeadPerson> selectAssessedPeopleByIds(HashSet<String> set);

    List<LeadPerson> selectAssessedPeopleFromResult(String id);

    List<SubmitDeputyAssChild> selectAssessByDeptIdAndAssessedPersonIdAndAnnual(String deptId, String assessedPersonId, String annual);

    List<String> selectAnnualFromLaunch();

    List<String> selectHeaders(String deptId,  String annual);

    List<Dept> selectDeptFromResult();






    void insertSectionAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables);

    void insertSubmitSectionAsselement(SubmitDeputyAssChild submitDeputyAssChild);

    List<SubmitDeputyAssChild> selectSectionDetails(String oldParentId, String assessedPersonId);

    List<Dept> selectSectionDeptFromLaunch();

    List<String> selectSectionAssessedPeopleFromLaunch(String id);

    List<SubmitDeputyAssChild> selectSectionAssessByDeptIdAndAssessedPersonIdAndAnnual(String deptId, String assessedPersonId, String annual);

    List<String> selectSectionHeaders(String deptId,  String annual);

    Integer isHaveSectionAssessresult(SubmitDeputyAssChild submitDeputyAssChild);

    void updateSectionAssessResult(SubmitDeputyAssChild submitDeputyAssChild);

    void insertSectionAssessResult(SubmitDeputyAssChild submitDeputyAssChild);

    List<Dept> selectSectionDeptFromResult();

    List<LeadPerson> selectSectionAssessedPeopleFromResult(String id);

    List<SubmitDeputyAssChild> selectAssessByDeptIdAndAnnualAndSeasonAndAssessName(String deptId, String annual, String season, String assessName);

    List<SubmitDeputyAssChild> selectAssessByDeptIdAndAnnualAndAssessName(String deptId, String annual, String assessName);

    void insertDeputyDetails(List<SubmitDeputyAssChild> list);

    void insertSectionDetails(List<SubmitDeputyAssChild> list);

    List<SubmitDeputyAssChild> selectSectionAssessByDeptIdAndAnnualAndSeasonAndAssessName(String deptId, String annual, String season, String assessName);

    List<SubmitDeputyAssChild> selectSectionAssessByDeptIdAndAnnualAndAssessName(String deptId, String annual, String assessName);
}
