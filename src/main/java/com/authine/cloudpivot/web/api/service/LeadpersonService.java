package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.bean.leadership.LeadPersonDO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 领导人员 服务类
 * </p>
 *
 * @author zsh
 * @since 2019-11-26
 */
public interface LeadpersonService {
    List<LeadPerson> getBydeptId(String deptId);

    void updateLeaderAge(List<LeadPersonDO> leadPersonDOList);

    List<LeadPersonDO> selectAllLeadPerson();
}
