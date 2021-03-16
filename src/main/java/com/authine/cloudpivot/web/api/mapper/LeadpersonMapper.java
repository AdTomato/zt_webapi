package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.bean.leadership.LeadPersonDO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 领导人员 Mapper 接口
 * </p>
 *
 * @author zsh
 * @since 2019-11-26
 */

public interface LeadpersonMapper {

    List<LeadPerson> getByDeptId(String deptId);

    List<Map<String, String>> getLeadpersonByDeptId(String deptId);

    void updateLeaderAge(List<LeadPersonDO> leadPersonDOList);

    List<LeadPersonDO> selectAllLeadPerson();
}
