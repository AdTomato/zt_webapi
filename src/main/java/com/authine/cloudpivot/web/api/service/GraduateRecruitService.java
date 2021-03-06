package com.authine.cloudpivot.web.api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface GraduateRecruitService {
    List<Map<String,Object>> selectDropdownBox(String year, String companyId);

    List<String> selectYearDropdownBox(String userName);

    List<Map<String,Object>> selectComDropdownBox(String year);

    BigDecimal checkremainingNum(String assignmentMajor);

    void updateremainingNum(BigDecimal bigDecimal, String assignmentMajor);

    List<String> selectPlanMajorList(String year, String companyId);

    List<String> getResumeId(String userName, String phone, String toJSONString);

    int checkDelivery(String phone, String toJSONString);
}
