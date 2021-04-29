package com.authine.cloudpivot.web.api.service.QualityAssessment;


import com.authine.cloudpivot.web.api.bean.QualityAssessment.InspectionPersonnel;

import java.util.List;

/**
 * 中铁考核人员维护
 * @Author Ke LongHai
 * @Date 2021/4/14 13:10
 * @Version 1.0
 */
public interface PersonnelService {

    /**
     * 返回的人员
     *
     * @return {@link List<String>}
     */
    List<InspectionPersonnel> returnPersonnel();

}
