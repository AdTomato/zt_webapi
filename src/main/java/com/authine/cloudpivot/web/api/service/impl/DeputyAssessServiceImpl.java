package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;
import com.authine.cloudpivot.web.api.mapper.DeputyAssessMapper;
import com.authine.cloudpivot.web.api.service.DeputyAssessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/7/20 17:04
 * @Version 1.0
 */
@Service
public class DeputyAssessServiceImpl implements DeputyAssessService {
    @Autowired
    DeputyAssessMapper deputyAssessMapper;
    @Override
    public void insertDeptDeputyAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables) {
        deputyAssessMapper.insertDeptDeputyAsselement(deptDeputyAssessTables);
    }

    @Override
    public void insertSubmitDeputyAsselement(SubmitDeputyAssChild submitDeputyAssChild) {
        deputyAssessMapper.insertSubmitDeputyAsselement(submitDeputyAssChild);
    }
}
