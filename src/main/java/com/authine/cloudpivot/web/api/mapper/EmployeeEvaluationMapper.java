package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchJudges;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/8/17 16:09
 * @Version 1.0
 */
@Repository
public interface EmployeeEvaluationMapper {
    List<LaunchDeputyAssChild> initPerformanceEvaluationDeputyElement();

    List<LaunchDeputyAssChild> initPerformanceEvaluationSectionElement();

    List<LaunchJudges> initPerformanceEvaluationJudgesElement();
}
