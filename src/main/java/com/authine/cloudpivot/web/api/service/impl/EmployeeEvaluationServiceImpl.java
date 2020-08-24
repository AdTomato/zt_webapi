package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchJudges;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;
import com.authine.cloudpivot.web.api.mapper.EmployeeEvaluationMapper;
import com.authine.cloudpivot.web.api.service.EmployeeEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/8/17 16:01
 * @Version 1.0
 */
@Service
public class EmployeeEvaluationServiceImpl implements EmployeeEvaluationService {
    @Autowired
    private EmployeeEvaluationMapper employeeEvaluationMapper;

    @Override
    public List<LaunchDeputyAssChild> initPerformanceEvaluationDeputyElement() {
        return employeeEvaluationMapper.initPerformanceEvaluationDeputyElement();

    }

    @Override
    public List<LaunchDeputyAssChild> initPerformanceEvaluationSectionElement() {
        return employeeEvaluationMapper.initPerformanceEvaluationSectionElement();
    }

    @Override
    public List<LaunchJudges> initPerformanceEvaluationJudgesElement() {
        return employeeEvaluationMapper.initPerformanceEvaluationJudgesElement();
    }


}

