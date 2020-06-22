package com.authine.cloudpivot.web.api.mapper;


import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface GraduateRecruitMapper {
    List<Map<String,Object>> selectDropdownBox(@Param("year") String year, @Param("companyId") String companyId);

    List<String> selectYearDropdownBox(String userName);

    List<Map<String,Object>> selectComDropdownBox(String year);

    BigDecimal checkremainingNum(String assignmentMajor);

    void updateremainingNum(@Param("bigDecimal") BigDecimal bigDecimal, @Param("assignmentMajor") String assignmentMajor);
}
