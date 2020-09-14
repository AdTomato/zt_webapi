package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchJudges;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;

import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/8/17 15:55
 * @Version 1.0
 */
public interface EmployeeEvaluationService {


    List<LaunchDeputyAssChild> initPerformanceEvaluationDeputyElement(String id);

    List<LaunchDeputyAssChild> initPerformanceEvaluationSectionElement(String id);

    List<LaunchJudges> initPerformanceEvaluationJudgesElement(String id);
}
