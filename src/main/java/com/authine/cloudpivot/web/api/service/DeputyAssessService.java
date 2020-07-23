package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;

import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/7/20 17:00
 * @Version 1.0
 */
public interface DeputyAssessService {
    void insertDeptDeputyAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables);

    void insertSubmitDeputyAsselement(SubmitDeputyAssChild submitDeputyAssChild);
}
