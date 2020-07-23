package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;
import org.springframework.stereotype.Repository;

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
}
