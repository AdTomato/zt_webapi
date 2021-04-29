package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchJudges;

import java.util.List;


/**
 * 初始化员工个人业绩考核发起表和员工能力素质评价发起表
 *
 * @author zhengshiaho
 * @date 2021/04/28
 */
public interface EmployeeEvaluationService {


    List<LaunchDeputyAssChild> initPerformanceEvaluationDeputyElement(String id);

    List<LaunchDeputyAssChild> initPerformanceEvaluationSectionElement(String id);

    List<LaunchJudges> initPerformanceEvaluationJudgesElement(String id);
}
