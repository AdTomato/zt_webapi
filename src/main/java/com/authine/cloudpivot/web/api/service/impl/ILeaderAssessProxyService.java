package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.bean.LaunchQuality;
import com.authine.cloudpivot.web.api.bean.LeadFixQuanCountInfo;
import com.authine.cloudpivot.web.api.service.ILeaderAssessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ILeaderAssessProxyService {
    @Autowired
    private ILeaderAssessService iLeaderAssessServie;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public synchronized String proxyupdateFixQuanResult(UserModel user, DepartmentModel department, LeadFixQuanCountInfo leadFixQuanCountInfo){
        String result = iLeaderAssessServie.updateFixQuanResult(user, department, leadFixQuanCountInfo);
        return result;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public synchronized String proxycountLeadQuality(LaunchQuality launchQuality, UserModel user, DepartmentModel department){
        String result = iLeaderAssessServie.countLeadQuality(launchQuality, user, department);
        return result;
    }
}
