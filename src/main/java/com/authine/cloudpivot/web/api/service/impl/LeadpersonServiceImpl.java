package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.bean.leadership.LeadPersonDO;
import com.authine.cloudpivot.web.api.mapper.LeadpersonMapper;
import com.authine.cloudpivot.web.api.service.LeadpersonService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 领导人员 服务实现类
 * </p>
 *
 * @author zsh
 * @since 2019-11-26
 */
@Service
public class LeadpersonServiceImpl implements LeadpersonService {
    @Resource
    private LeadpersonMapper mapper;

    @Override
    public List<LeadPerson> getBydeptId(String deptId) {
        return mapper.getByDeptId(deptId);
    }

    @Override
    public void updateLeaderAge(List<LeadPersonDO> leadPersonDOList) {
        mapper.updateLeaderAge(leadPersonDOList);

    }

    @Override
    public List<LeadPersonDO> selectAllLeadPerson() {
        return mapper.selectAllLeadPerson();
    }
}
